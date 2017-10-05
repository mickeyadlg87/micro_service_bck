package com.redd.backoffice.dao.impl;

import cl.tastets.life.core.framework.dao.DataSourceDao;
import cl.tastets.life.objects.BasicEntity;
import cl.tastets.life.objects.Event;
import cl.tastets.life.objects.RealmEnum;
import cl.tastets.life.objects.utils.Paginated;
import cl.tastets.life.objects.utils.QueryFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redd.backoffice.dao.queries.QueryAccesoryEvents;
import com.redd.backoffice.utils.DaoTemplates;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author aleal
 */
@Repository
public class EventDaoImpl implements DaoTemplates {

    @Autowired
    private DataSourceDao dao;

    private Map<String, String> queries;
    private ObjectMapper mapperForJson = new ObjectMapper();

    @PostConstruct
    public void initQueries() {
        queries = new HashMap<>();
        queries.put("queryAllEvents" + RealmEnum.backoffice, QueryAccesoryEvents.queryEventsBackoffice);
        queries.put("queryEventsForCertificateUnit" + RealmEnum.backoffice, QueryAccesoryEvents.queryEventCertUnitBackoffice);
        queries.put("insertAccesoriesByDevice" + RealmEnum.backoffice, QueryAccesoryEvents.insertAccesoriesByDeviceBackoffice);
        queries.put("updateAccesoriesByDevice" + RealmEnum.backoffice, QueryAccesoryEvents.updateAccesoriesByDeviceBackoffice);
        queries.put("queryAccesoriesInstalled" + RealmEnum.backoffice, QueryAccesoryEvents.queryAccesoriesInstalledByDeviceBackoffice);
        queries.put("insertUnitCertified" + RealmEnum.backoffice, QueryAccesoryEvents.insertUnitCertifiedBackOffice);
        queries.put("updateUnitCertified" + RealmEnum.backoffice, QueryAccesoryEvents.updateUnitCertifiedBackOffice);
    }

    public List<Event> findEventsWithFilter(String realm, Optional<Paginated> paginated, Optional<QueryFilter> filter) {
        List<Event> result = new ArrayList<>();
        result.addAll(getListByCriteria(realm, dao,
                () -> filterQueryValues("queryAllEvents" + realm, paginated, filter, true),
                () -> new Object[]{}));
        return result;
    }

    public List<Event> findEventsByAccesories(String realm, Optional<Paginated> paginated, Optional<QueryFilter> filter) {
        List<Event> result = new ArrayList<>();
        result.addAll(getListByCriteria(realm, dao,
                () -> filterQueryValues("queryEventsForCertificateUnit" + realm, paginated, filter, true),
                () -> new Object[]{}));
        return result;
    }

    /**
     * Busca todos los accesorios instalados por modem id en el backoffice
     * @param realm
     * @param paginated
     * @param filter
     * @return
     * @throws Exception
     */
    public List<BasicEntity> findAccesoriesInstalled(String realm, Optional<Paginated> paginated, Optional<QueryFilter> filter) throws Exception {
        List<BasicEntity> result = new ArrayList<>();
        result.addAll(getListByCriteria(realm, dao,
                () -> filterQueryValues("queryAccesoriesInstalled" + realm, paginated, filter, true),
                () -> new Object[]{}));
        return result;
    }

    /**
     * Busca los accesorios instalados en un modem id particular
     * @param realm
     * @param mid
     * @return
     * @throws Exception
     */
    public BasicEntity findAccesoryByMid(String realm, String mid) throws Exception {
        BasicEntity k = new BasicEntity();
        QueryFilter filter = QueryFilter.from();
        Paginated paginated = Paginated.from();
        k.put("mid", mid);
        fillFilterWithCriteria(Optional.of(filter), k);
        BasicEntity accesoriesByMid = new BasicEntity();
        List<BasicEntity> lacc = findAccesoriesInstalled(realm, Optional.of(paginated), Optional.of(filter));
        accesoriesByMid.putAll(lacc.isEmpty() ? new BasicEntity() : lacc.get(0));

        if (!accesoriesByMid.isEmpty()) {
            String installedAcc = accesoriesByMid.getString("installedAccesory");
            accesoriesByMid.put("installedAccesory", stringToListAccesories(installedAcc));
        }

        return accesoriesByMid;
    }

    @Override
    public String filterQueryValues(String query, Optional<Paginated> paginated, Optional<QueryFilter> filter, Boolean sortPaginated) {
        StringBuilder filteredQuery = new StringBuilder();
        StringBuilder queryReal = new StringBuilder(queries.get(query));
        filter.ifPresent((f) -> {
            f.getList("filter").parallelStream().forEach((Map m) -> {
                BasicEntity k = new BasicEntity();
                k.putAll(m);
                if (k.get("id") != null) {
                    filteredQuery.append(" ev.id").append(" = ").append(k.get("id")).append(" AND ");
                }
                if (k.get("isVisible") != null) {
                    filteredQuery.append(" ev.visible_cliente").append(" = ").append(k.get("isVisible")).append(" AND ");
                }
                if (k.get("mid") != null) {
                    filteredQuery.append(" cd.identificador_unico").append(" = '").append(k.get("mid")).append("' AND ");
                }
                if (k.get("valueForLogic") != null) {
                    filteredQuery.append(" ev.valido_logica").append(" = '").append(k.get("valueForLogic")).append("' AND ");
                }
                if (k.get("eventName") != null) {
                    filteredQuery.append(" ev.nombre_evento").append(" LIKE '%").append(k.get("eventName")).append("%' AND ");
                }
                if (k.get("accesoryListId") != null) {
                    String ids = k.getIntegerList("accesoryListId").stream().map(i -> i.toString()).collect(Collectors.joining(","));
                    filteredQuery.append(" evts.id_tipo_sensor IN(").append(ids).append(") AND ");
                }
                if (k.get("dateFromTo") != null) {
                    List<Long> dateFromTo = k.getOrDefault("dateFromTo", new ArrayList<Long>(), List.class);
                    filteredQuery.append(" cd.fecha_accion BETWEEN '").
                            append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(dateFromTo.get(0)))).
                            append("' AND '").
                            append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(dateFromTo.get(1)))).append("' AND ");
                }
            });
            //Le saco el ultimo AND al string de los filtros y le agrego el and al comienzo
            if (filteredQuery.toString().endsWith(" AND ")) {
                filteredQuery.replace(filteredQuery.lastIndexOf(" AND "), filteredQuery.length() - 1, " ");
                filteredQuery.insert(0, " AND ");
            }
            //Le concateno al queryReal al final el SORT
            if (sortPaginated) {
                if (f.get("sort") != null) {
                    queryReal.append(" ORDER BY ev.");
                    if (f.getString("sort").contains("name")) {
                        queryReal.append(f.getString("sort").replace("name", "nombre_evento"));
                    }
                    if (f.getString("sort").contains("createDate")) {
                        queryReal.append(f.getString("sort").replace("createDate", "fecha_creacion"));
                    }
                }
            }
        });
        //Le concateno al queryReal al final el despues del SORT el paginado
        if (sortPaginated) {
            paginated.ifPresent(p -> {
                queryReal.append(" LIMIT ").append(p.getLimit()).append(" OFFSET ").append(p.getOffset());
            });
        }
        //En este punto el queryReal esta con sort y limit, y tengo que reemplazar donde aparezce WHERE_CLASE por el filterdQuery
        return queryReal.toString().replace("{WHERE_CLAUSE}", filteredQuery.toString());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public BasicEntity saveAccesoriesByDev(String realm, BasicEntity basicEntity, String query) throws Exception {
        String queryToExcecute = queries.get(query + realm);
        Object[] args = new Object[4];
        args[0] = basicEntity.get("username");
        args[1] = basicEntity.get("imei");
        args[2] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(basicEntity.getLong("validationDate")));
        args[3] = mapperForJson.writeValueAsString(basicEntity.get("installedAccesory"));
        try {
            int id = getJdbcTemplate(dao, realm).update(queryToExcecute, args);
            basicEntity.put("insertRow", id);
        } catch (DuplicateKeyException ex) {
            basicEntity.put("insertRow", 0);
        }
        return basicEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public BasicEntity updateAccesoriesByDev(String realm, BasicEntity basicEntity, String query) throws Exception {
        String queryToExcecute = queries.get(query + realm);
        Object[] args = new Object[4];
        args[0] = basicEntity.get("username");
        args[1] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(basicEntity.getLong("validationDate")));
        args[2] = mapperForJson.writeValueAsString(basicEntity.get("installedAccesory"));
        args[3] = basicEntity.get("imei");
        try {
            int id = getJdbcTemplate(dao, realm).update(queryToExcecute, args);
            basicEntity.put("updatedRow", id);
        } catch (DuplicateKeyException ex) {
            basicEntity.put("updatedRow", 0);
        }
        return basicEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public BasicEntity saveUnitCertified(String realm, BasicEntity basicEntity, String query) throws Exception {
        String queryToExcecute = queries.get(query + realm);
        Object[] args = new Object[19];
        args[0] = basicEntity.get("realmTrue");
        args[1] = basicEntity.get("unitId");
        args[2] = basicEntity.get("username");
        args[3] = basicEntity.get("imei");
        args[4] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(basicEntity.getLong("actionDate")));
        args[5] = basicEntity.get("note");
        args[6] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(basicEntity.getLong("creationUnitDate")));
        args[7] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(basicEntity.getLong("lastActivityDate")));
        args[8] = basicEntity.get("devicetypeId");
        args[9] = basicEntity.get("devicetypeName");
        args[10] = basicEntity.get("plateNumber");
        args[11] = basicEntity.get("unitName");
        args[12] = basicEntity.get("vin");
        args[13] = basicEntity.get("companyName");
        args[14] = basicEntity.get("companyId");
        args[15] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(basicEntity.getLong("deviceValidationDate")));
        args[16] = basicEntity.get("typeSale");
        args[17] = basicEntity.get("companyFactSelected");
        args[18] = basicEntity.get("companyFactSelectedName");
        try {
            int id = getJdbcTemplate(dao, realm).update(queryToExcecute, args);
            basicEntity.put("insertRow", id);
        } catch (DuplicateKeyException ex) {
            basicEntity.put("insertRow", 0);
        }
        return basicEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public BasicEntity updateUnitCertified(String realm, BasicEntity basicEntity, String query) throws Exception {
        String queryToExcecute = queries.get(query + realm);
        Object[] args = new Object[16];
        args[0] = basicEntity.get("username");
        args[1] = basicEntity.get("imei");
        args[2] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(basicEntity.getLong("actionDate")));
        args[3] = basicEntity.get("note");
        args[4] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(basicEntity.getLong("creationUnitDate")));
        args[5] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(basicEntity.getLong("lastActivityDate")));
        args[6] = basicEntity.get("devicetypeId");
        args[7] = basicEntity.get("devicetypeName");
        args[8] = basicEntity.get("plateNumber");
        args[9] = basicEntity.get("unitName");
        args[10] = basicEntity.get("vin");
        args[11] = basicEntity.get("companyName");
        args[12] = basicEntity.get("companyId");
        args[13] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(basicEntity.getLong("deviceValidationDate")));
        args[12] = basicEntity.get("typeSale");
        args[12] = basicEntity.get("companyFactSelected");
        args[12] = basicEntity.get("companyFactSelectedName");
        args[14] = basicEntity.get("realmTrue");
        args[15] = basicEntity.get("unitId");
        try {
            int id = getJdbcTemplate(dao, realm).update(queryToExcecute, args);
            basicEntity.put("updatedRow", id);
        } catch (DuplicateKeyException ex) {
            basicEntity.put("updatedRow", 0);
        }
        return basicEntity;
    }

    private List<Map> stringToListAccesories(String accesoriesString) {
        List<Map> listaAccesorios = new ArrayList<>();

        try {
            listaAccesorios = mapperForJson.readValue(accesoriesString, new ArrayList<>().getClass());
        } catch (Exception ex) {
            Logger.getRootLogger().info(ex);
        }

        return listaAccesorios;
    }

}
