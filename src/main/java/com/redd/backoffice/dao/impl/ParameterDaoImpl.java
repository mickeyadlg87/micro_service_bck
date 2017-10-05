package com.redd.backoffice.dao.impl;

import cl.tastets.life.core.framework.dao.DataSourceDao;
import cl.tastets.life.objects.Parameter;
import cl.tastets.life.objects.RealmEnum;
import cl.tastets.life.objects.utils.Paginated;
import cl.tastets.life.objects.utils.QueryFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redd.backoffice.dao.queries.QueryParameter;
import com.redd.backoffice.utils.DaoTemplates;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author fgodoy
 */
@Repository
public class ParameterDaoImpl implements DaoTemplates{
    
    @Autowired
    private DataSourceDao dao;
    
    private Map<String, String> queries;
    private ObjectMapper mapperForJson;
    
    @PostConstruct
    public void initQueries() {
        mapperForJson = new ObjectMapper();
        queries = new HashMap<>();
        queries.put("queryListaEstadosGenerales" + RealmEnum.backoffice, QueryParameter.queryGetGeneralState);      
    }
    
    public List<Parameter> findStateByCodeName(String realm, String codeName) throws Exception {
        String queryToExcecute = queries.get("queryListaEstadosGenerales" + realm);
        List<Parameter> response = new ArrayList<>();
        
        Object[] arg = new Object[1];
        arg[0] = codeName;
        
        response.addAll(getListByCriteria(realm, dao,
                () -> queryToExcecute,
                () -> arg));
        
        return response;
    }

    @Override
    public String filterQueryValues(String query, Optional<Paginated> paginated, Optional<QueryFilter> filter, Boolean sortPaginated) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
