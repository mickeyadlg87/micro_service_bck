package com.redd.backoffice.dao.impl;

import cl.tastets.life.core.framework.dao.DataSourceDao;
import cl.tastets.life.objects.BasicEntity;
import cl.tastets.life.objects.Event;
import cl.tastets.life.objects.RealmEnum;
import cl.tastets.life.objects.backoffice.DeviceType;
import cl.tastets.life.objects.utils.Paginated;
import cl.tastets.life.objects.utils.QueryFilter;
import com.redd.backoffice.dao.queries.QueryDevices;
import com.redd.backoffice.utils.DaoTemplates;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
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
public class DeviceDaoImpl implements DaoTemplates {

    @Autowired
    private DataSourceDao dao;

    private Map<String, String> queries;

    @PostConstruct
    public void initQueries() {
        queries = new HashMap<>();
        queries.put("queryAllDeviceTypes" + RealmEnum.backoffice, QueryDevices.queryDeviceTypesBackoffice);
        queries.put("queryEventForCertificate" + RealmEnum.backoffice, QueryDevices.queryEventForCertificateBackoffice);
        queries.put("queryEventForDeviceTypes" + RealmEnum.backoffice, QueryDevices.queryEventForDeviceBackoffice);
        queries.put("insertEventForDeviceTypes" + RealmEnum.backoffice, QueryDevices.insertEventForDeviceTypeBackoffice);
        queries.put("deleteEventForDeviceTypes" + RealmEnum.backoffice, QueryDevices.deleteEventForDeviceTypeBackoffice);
        queries.put("insertEventForCertificate" + RealmEnum.backoffice, QueryDevices.insertEventForCertificateBackoffice);
        queries.put("deleteEventForCertificate" + RealmEnum.backoffice, QueryDevices.deleteEventForCertificateBackoffice);
    }

    public List<Event> findEventsByTypeId(String realm, Integer deviceTypeId, Optional<Paginated> paginated, Optional<QueryFilter> filter) {
        BasicEntity k = new BasicEntity();
        k.put("deviceTypeid", deviceTypeId);
        fillFilterWithCriteria(filter, k);
        List<Event> le = findEventsWithFilter(realm, paginated, filter);
        return le;
    }
    
    public List<Event> findEventsForCertification(String realm, Integer deviceTypeId, Optional<Paginated> paginated, Optional<QueryFilter> filter) {
        BasicEntity k = new BasicEntity();
        k.put("devTypeIdForCert", deviceTypeId);
        fillFilterWithCriteria(filter, k);
        List<Event> le = findEventsWithFilter(realm, paginated, filter);
        return le;
    }
    
    public List<DeviceType> findDeviceTypeWithFilter(String realm, Optional<Paginated> paginated, Optional<QueryFilter> filter) {
        List<DeviceType> result = new ArrayList<>();
        result.addAll(getListByCriteria(realm, dao,
                () -> filterQueryValues("queryAllDeviceTypes" + realm, paginated, filter, true),
                () -> new Object[]{}));
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public BasicEntity saveEventFortype(String realm, BasicEntity basicEntity, String query) throws Exception {
        String queryToExcecute = queries.get(query + realm);
        Object[] args = new Object[2];
        args[0] = basicEntity.get("eventId");
        args[1] = basicEntity.get("deviceTypeId");
        try {
            int id = getJdbcTemplate(dao, realm).update(queryToExcecute, args);
            basicEntity.put("insertRow", id);
        } catch (DuplicateKeyException ex) {
            basicEntity.put("insertRow", 0);
        }
        return basicEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public BasicEntity deleteEventFortype(String realm, BasicEntity basicEntity, String query) throws Exception {
        String queryToExecute = queries.get(query + realm);
        Object[] args = new Object[2];
        args[0] = basicEntity.get("eventId");
        args[1] = basicEntity.get("deviceTypeId");
        int id = getJdbcTemplate(dao, realm).update(queryToExecute, args);
        basicEntity.put("deleteRow", id);
        return basicEntity;
    }

    public List<Event> findEventsWithFilter(String realm, Optional<Paginated> paginated, Optional<QueryFilter> filter) {
        List<Event> result = new ArrayList<>();
        Map filtro = filter.get().getList("filter").get(0);
        if (filtro.containsKey("deviceTypeid")) {
            result.addAll(getListByCriteria(realm, dao,
                    () -> filterQueryValues("queryEventForDeviceTypes" + realm, paginated, filter, true),
                    () -> new Object[]{}));
        } else {
            result.addAll(getListByCriteria(realm, dao,
                    () -> filterQueryValues("queryEventForCertificate" + realm, paginated, filter, true),
                    () -> new Object[]{}));            
        }
        return result;
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
                    filteredQuery.append(" td.id").append(" = ").append(k.get("id")).append(" AND ");
                }
                if (k.get("deviceTypeid") != null) {
                    filteredQuery.append(" edh.id_tipo_dispositivo").append(" = ").append(k.get("deviceTypeid")).append(" AND ");
                }
                if (k.get("devTypeIdForCert") != null) {
                    filteredQuery.append(" edc.id_tipo_dispositivo").append(" = ").append(k.get("devTypeIdForCert")).append(" AND ");
                }
                if (k.get("name") != null) {
                    filteredQuery.append(" td.nombre").append(" LIKE '%").append(k.get("name")).append("%' AND ");
                }
                if (k.get("producer") != null) {
                    filteredQuery.append(" td.fabricante").append(" LIKE '%").append(k.get("producer")).append("%' AND ");
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
                    queryReal.append(" ORDER BY td.");
                    if (f.getString("sort").contains("name")) {
                        queryReal.append(f.getString("sort").replace("name", "nombre"));
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

}
