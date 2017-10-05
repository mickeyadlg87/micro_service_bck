package com.redd.backoffice.dao.impl;

import cl.tastets.life.core.framework.dao.DataSourceDao;
import cl.tastets.life.objects.BasicEntity;
import cl.tastets.life.objects.RealmEnum;
import cl.tastets.life.objects.utils.Paginated;
import cl.tastets.life.objects.utils.QueryFilter;
import com.redd.backoffice.dao.queries.QueryUsers;
import com.redd.backoffice.utils.DaoTemplates;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
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
public class UserDaoImpl implements DaoTemplates {
    
    @Autowired
    private DataSourceDao dao;

    private Map<String, String> queries;
    
    @PostConstruct
    public void initQueries() {
        queries = new HashMap<>();
        queries.put("queryGetUsersByRealm" + RealmEnum.backoffice, QueryUsers.queryGetUsersByRealm);
        queries.put("queryUserByUsernameRealm" + RealmEnum.backoffice, QueryUsers.queryUserByUsernameRealm);
        queries.put("insert" + RealmEnum.backoffice, QueryUsers.insert);
        queries.put("update" + RealmEnum.backoffice, QueryUsers.update);
    }
    
    /**
     * Obtiene usuario por plataforma
     *
     * @param realm
     * @param platform
     * @return
     */
    public List<BasicEntity> findUserByRealm(String realm, String platform) {
        List<BasicEntity> result = new ArrayList<>();
        result.addAll(getListByCriteria(realm, dao,
                () -> queries.get("queryGetUsersByRealm" + realm),
                () -> new Object[]{platform}));
        return result;
    }
    
    public BasicEntity findUserByUserName(String realm, String platform, String userName) {
        BasicEntity result = new BasicEntity();
        result.putAll(getByCriteria(realm, dao,
                () -> queries.get("queryUserByUsernameRealm" + realm),
                () -> new Object[]{platform, userName}));
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public BasicEntity save(String realm, BasicEntity basicEntity, String query) throws Exception {
        String queryToExecute = queries.get(query + realm);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate(dao, realm).update((Connection s) -> {
            PreparedStatement ps = s.prepareStatement(queryToExecute, new String[]{"id"});
            ps.setObject(1, basicEntity.get("platform"));
            ps.setObject(2, basicEntity.get("device"));
            ps.setObject(3, basicEntity.get("userName"));
            ps.setObject(4, basicEntity.get("name"));
            ps.setObject(5, basicEntity.get("lastName"));
            ps.setObject(6, basicEntity.get("rut"));
            ps.setObject(7, basicEntity.get("clientId"));
            ps.setObject(8, basicEntity.get("functionalityId"));
            return ps;
        }, keyHolder);
        basicEntity.put("id", keyHolder.getKey().intValue());
        return basicEntity;
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public BasicEntity update(String realm, BasicEntity basicEntity, String query) throws Exception {
        String queryToExcecute = queries.get(query + realm);
        Object[] args = new Object[9];
        args[0] = basicEntity.get("platform");
        args[1] = basicEntity.get("device");
        args[2] = basicEntity.get("userName");
        args[3] = basicEntity.get("name");
        args[4] = basicEntity.get("lastName");
        args[5] = basicEntity.get("rut");
        args[6] = basicEntity.get("clientId");
        args[7] = basicEntity.get("functionalityId");
        args[8] = basicEntity.get("id");
        try {
            int id = getJdbcTemplate(dao, realm).update(queryToExcecute, args);
            basicEntity.put("updatedRow", id);
        } catch (DuplicateKeyException ex) {
            basicEntity.put("updatedRow", 0);
        }
        return basicEntity;
    }

    @Override
    public String filterQueryValues(String query, Optional<Paginated> paginated, Optional<QueryFilter> filter, Boolean sortPaginated) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
