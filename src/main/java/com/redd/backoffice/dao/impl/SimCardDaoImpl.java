package com.redd.backoffice.dao.impl;

import cl.tastets.life.core.framework.dao.DataSourceDao;
import cl.tastets.life.objects.BasicEntity;
import cl.tastets.life.objects.RealmEnum;
import cl.tastets.life.objects.backoffice.SimCard;
import cl.tastets.life.objects.utils.Paginated;
import cl.tastets.life.objects.utils.QueryFilter;
import com.redd.backoffice.dao.queries.QuerySimCards;
import com.redd.backoffice.utils.DaoTemplates;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author fgodoy
 */
@Repository
public class SimCardDaoImpl implements DaoTemplates {
    
    @Autowired
    private DataSourceDao dao;
    private Map<String, String> queries;
    
    @PostConstruct
    public void initQueries(){
        queries = new HashMap<>();
        queries.put("queryallSimCard" + RealmEnum.backoffice, QuerySimCards.queryAllSimCardBackoffice);
        queries.put("insertSimCard" + RealmEnum.backoffice, QuerySimCards.insertSimCardBackoffice);
        queries.put("updatesimCard" + RealmEnum.backoffice, QuerySimCards.updateSimCarBackoffice);
        queries.put("queryProvidersSimCard" + RealmEnum.backoffice, QuerySimCards.queryProvidersSimCardBackoffice);
    }
    
    public SimCard findSimCardByPhoneNumber(String realm, String phoneNumber, Optional<Paginated> paginated, Optional<QueryFilter> filter) {
        BasicEntity s = new BasicEntity();
        s.put("phoneNumber", phoneNumber);
        fillFilterWithCriteria(filter, s);
        SimCard simcard = new SimCard();
        List<SimCard> ls = getSimCardListByFilter(realm, paginated, filter);
        simcard.putAll(ls.isEmpty() ? new SimCard() : ls.get(0));
        return simcard;
    }

    public SimCard findSimCardByIccid(String realm, String iccid, Optional<Paginated> paginated, Optional<QueryFilter> filter) {
        BasicEntity p = new BasicEntity();
        p.put("iccid", iccid);
        fillFilterWithCriteria(filter, p);
        SimCard simcard = new SimCard();
        List<SimCard> ls = getSimCardListByFilter(realm, paginated, filter);
        simcard.putAll(ls.isEmpty() ? new SimCard() : ls.get(0));
        return simcard;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public SimCard save(String realm, SimCard simcard, String query) throws Exception {
        String queryToExecute = queries.get(query + realm);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate(dao, realm).update((Connection s) -> {
            PreparedStatement ps = s.prepareStatement(queryToExecute, new String[]{"id"});
            ps.setObject(1, simcard.get("phoneNumber"));
            ps.setObject(2, simcard.get("iccid"));
            ps.setObject(3, simcard.get("username"));
            return ps;
        }, keyHolder);
        return simcard.put("id", keyHolder.getKey().intValue());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public SimCard update(String realm, SimCard simcard, String query) throws Exception {
        String queryToExcecute = queries.get(query + realm);
        Object[] args = new Object[5];
        args[0] = simcard.get("phoneNumber");
        args[1] = simcard.get("downDate") == null ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(simcard.getLong("downDate")));
        args[2] = simcard.get("iccid");
        args[3] = simcard.get("username");
        args[4] = simcard.get("id");
        int id = getJdbcTemplate(dao, realm).update(queryToExcecute, args);
        simcard.put("updateRow", id);
        return simcard;
    }

    public List<SimCard> getSimCardListByFilter(String realm, Optional<Paginated> paginated, Optional<QueryFilter> filter) {
        List<SimCard> result = new ArrayList<>();
        result.addAll(getListByCriteria(realm, dao,
                () -> filterQueryValues("queryallSimCard" + realm, paginated, filter, true),
                () -> new Object[]{}));
        return result;
    }

    public List<SimCard> getProvidersByRealm(String realm) {
        String queryToExecute = queries.get("queryProvidersSimCard" + realm);
        List<SimCard> result = new ArrayList<>();
        result.addAll(getListByCriteria(realm, dao,
                () -> queryToExecute,
                () -> new Object[]{}));
        return result;
    }
    
    @Override
    public String filterQueryValues(String query, Optional<Paginated> paginated, Optional<QueryFilter> filter, Boolean sortPaginated){
        StringBuilder filteredQuery = new StringBuilder();
        StringBuilder queryReal = new StringBuilder(queries.get(query));
        filter.ifPresent((f) -> {
            f.getList("filter").parallelStream().forEach((Map m) -> {
                BasicEntity k = new BasicEntity();
                k.putAll(m);
                if (k.get("id") != null){
                    filteredQuery.append(" sc.id").append(" = ").append(k.get("id")).append(" AND ");
                }
                if (k.get("phoneNumber") != null){
                    filteredQuery.append(" sc.numero_telefono").append(" = '").append(k.get("phoneNumber")).append("' AND ");
                }
                if (k.get("iccid") != null){
                    filteredQuery.append(" sc.numero_iccid").append(" = '").append(k.get("iccid")).append("' AND ");
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
                    queryReal.append(" ORDER BY sc.");
                    if (f.getString("sort").contains("createDate")) {
                        queryReal.append(f.getString("sort").replace("createDate", "fecha_creacion")).append(" DESC");
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
