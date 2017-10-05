package com.redd.backoffice.dao.impl;

import cl.tastets.life.core.framework.dao.DataSourceDao;
import cl.tastets.life.objects.BasicEntity;
import cl.tastets.life.objects.Parameter;
import cl.tastets.life.objects.RealmEnum;
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
public class AccesoryDaoImpl implements DaoTemplates {

    @Autowired
    private DataSourceDao dao;

    private Map<String, String> queries;

    @PostConstruct
    public void initQueries() {
        queries = new HashMap<>();
        queries.put("queryAllAccesories" + RealmEnum.backoffice, QueryDevices.queryAccesoriesBackoffice);
        queries.put("queryAccesoriesForDeviceTypes" + RealmEnum.backoffice, QueryDevices.queryAccessoriesForDeviceBackoffice);
        queries.put("insertAccesoryForDeviceTypes" + RealmEnum.backoffice, QueryDevices.insertAccesoryForDeviceTypeBackoffice);
        queries.put("deleteAccesoryForDeviceTypes" + RealmEnum.backoffice, QueryDevices.deleteAccesoryForDeviceTypeBackoffice);
    }

    public List<Parameter> findAccesoriesWithFilter(String realm, Optional<Paginated> paginated, Optional<QueryFilter> filter) {
        List<Parameter> result = new ArrayList<>();
        result.addAll(getListByCriteria(realm, dao,
                () -> filterQueryValues("queryAllAccesories" + realm, paginated, filter, true),
                () -> new Object[]{}));
        return result;
    }

    public List<Parameter> findAccesoriesForDevTypeWithFilter(String realm, Optional<Paginated> paginated, Optional<QueryFilter> filter) {
        List<Parameter> result = new ArrayList<>();
        result.addAll(getListByCriteria(realm, dao,
                () -> filterQueryValues("queryAccesoriesForDeviceTypes" + realm, paginated, filter, true),
                () -> new Object[]{}));
        return result;
    }

    public List<Parameter> findAccesoriesByDevType(String realm, Integer deviceTypeId, Optional<Paginated> paginated, Optional<QueryFilter> filter) {
        BasicEntity k = new BasicEntity();
        k.put("deviceTypeid", deviceTypeId);
        fillFilterWithCriteria(filter, k);
        List<Parameter> le = findAccesoriesForDevTypeWithFilter(realm, paginated, filter);
        return le;
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
                    filteredQuery.append(" ts.id").append(" = ").append(k.get("id")).append(" AND ");
                }
                if (k.get("deviceTypeid") != null) {
                    filteredQuery.append(" sdh.id_tipo_dispositivo").append(" = ").append(k.get("deviceTypeid")).append(" AND ");
                }
                if (k.get("variable") != null) {
                    filteredQuery.append(" ts.variable").append(" LIKE '%").append(k.get("variable")).append("%' AND ");
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
                    queryReal.append(" ORDER BY ts.");
                    if (f.getString("sort").contains("name")) {
                        queryReal.append(f.getString("sort").replace("name", "nombre"));
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
    public BasicEntity saveAccesoryForType(String realm, BasicEntity basicEntity, String query) throws Exception {
        String queryToExcecute = queries.get(query + realm);
        Object[] args = new Object[2];
        args[0] = basicEntity.get("accesoryId");
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
    public BasicEntity deleteAccesoryForType(String realm, BasicEntity basicEntity, String query) throws Exception {
        String queryToExecute = queries.get(query + realm);
        Object[] args = new Object[2];
        args[0] = basicEntity.get("accesoryId");
        args[1] = basicEntity.get("deviceTypeId");
        int id = getJdbcTemplate(dao, realm).update(queryToExecute, args);
        basicEntity.put("deleteRow", id);
        return basicEntity;
    }

}
