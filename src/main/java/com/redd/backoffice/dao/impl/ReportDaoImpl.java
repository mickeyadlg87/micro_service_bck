package com.redd.backoffice.dao.impl;

import cl.tastets.life.core.framework.dao.DataSourceDao;
import cl.tastets.life.objects.BasicEntity;
import cl.tastets.life.objects.Company;
import cl.tastets.life.objects.RealmEnum;
import cl.tastets.life.objects.utils.Paginated;
import cl.tastets.life.objects.utils.QueryFilter;
import com.redd.backoffice.dao.queries.QueryReports;
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
public class ReportDaoImpl implements DaoTemplates {

    @Autowired
    private DataSourceDao dao;

    private Map<String, String> queries;

    @PostConstruct
    public void initQueries() {
        queries = new HashMap<>();
        queries.put("queryAllFacturationReport" + RealmEnum.rslite, QueryReports.queryFacturationReportRsLite);
        queries.put("queryCantidadMovilPorEmpresa" + RealmEnum.rslite, QueryReports.queryUnitByCompanyRsLite);
        queries.put("queryCantidadMovilPorTipoVenta" + RealmEnum.rslite, QueryReports.queryUnitBySaleTypeRsLite);
        queries.put("queryMovilesDadosDeBaja" + RealmEnum.rslite, QueryReports.queryUnsubscribeReportRsLite);
        queries.put("queryKeepAlivePorEmpresa" + RealmEnum.rslite, QueryReports.queryAgregateKeepAlive);
        
        queries.put("queryUnidadesCertifcadas" + RealmEnum.backoffice, QueryReports.queryCetifiedReport);
        queries.put("insertReport" + RealmEnum.backoffice, QueryReports.insertReport);
        queries.put("updateReport" + RealmEnum.backoffice, QueryReports.updateReport);
    }

    /**
     * Reporte bajas de unidades
     *
     * @param realm
     * @param from
     * @param to
     * @return
     * @throws Exception
     */
    public List<BasicEntity> findUnsubscribeReport(String realm, Long from, Long to) throws Exception {
        String queryToExcecute = queries.get("queryMovilesDadosDeBaja" + realm);
        List<BasicEntity> result = new ArrayList<>();
        
        Object[] args = new Object[2];
        args[0] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(from));
        args[1] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(to));
                        
        result.addAll(getListByCriteria(realm, dao,
                () -> queryToExcecute,
                () -> args));

        return result;

    }
    
    /**
     * Reporte unidades certificadas
     *
     * @param realm
     * @param platform
     * @param from
     * @param to
     * @return
     * @throws Exception
     */
    public List<BasicEntity> findCertifiedReport(String realm, String platform, Long from, Long to) throws Exception {
        String queryToExcecute = queries.get("queryUnidadesCertifcadas" + realm);
        List<BasicEntity> response = new ArrayList<>();
        
        Object[] arg = new Object[3];
        arg[0] = platform;
        arg[1] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(from));
        arg[2] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(to));
                        
        response.addAll(getListByCriteria(realm, dao,
                () -> queryToExcecute,
                () -> arg));

        return response;

    }

    
    
    /**
     * Filtra el reporte de facturacion con un limite de datos
     *
     * @param realm
     * @param paginated
     * @param filter
     * @return
     * @throws Exception
     */
    public List<BasicEntity> findFacturationReportWithFilter(String realm, Optional<Paginated> paginated, Optional<QueryFilter> filter) throws Exception {
        List<BasicEntity> result = new ArrayList<>();
        result.addAll(getListByCriteria(realm, dao,
                () -> getQueryWithLimitAndOffset("queryAllFacturationReport", realm, paginated, filter),
                () -> new Object[]{}));
        return result;
    }
    
    /**
     * Obtiene la cantidad de moviles de cada empresa con limit y offset
     * @param realm
     * @param paginated
     * @param filter
     * @return
     * @throws Exception 
     */
    public List<BasicEntity> findQuantityForCompany(String realm, Optional<Paginated> paginated, Optional<QueryFilter> filter) throws Exception {
        List<BasicEntity> result = new ArrayList<>();
        result.addAll(getListByCriteria(realm, dao,
                () -> getQueryWithLimitAndOffset("queryCantidadMovilPorEmpresa", realm, paginated, filter),
                () -> new Object[]{}));
        return result;
    }
    /**
     * Obtiene el total de datos para el Reporte de facturacion
     *
     * @param realm
     * @return
     */
    public List<BasicEntity> findFacturationReport(String realm) {
        List<BasicEntity> result = new ArrayList<>();
        result.addAll(getListByCriteria(realm, dao,
                () -> getQueryWithLimitAndOffset("queryAllFacturationReport", realm, null, null),
                () -> new Object[]{}));
        return result;
    }
    
    /**
     * Obtiene la cantidad de unidades por tipo de venta
     * @param realm
     * @return
     * @throws Exception 
     */
    public List<BasicEntity> findQuantityForSaleType(String realm) throws Exception {
        List<BasicEntity> result = new ArrayList<>();
        result.addAll(getListByCriteria(realm, dao,
                () -> queries.get("queryCantidadMovilPorTipoVenta" + realm),
                () -> new Object[]{}));
        return result;
    }

    public List<Company> findKeepAliveForAllCompanies(String realm) throws Exception {
        List<Company> resultRaw = new ArrayList<>();
        List<Company> companiesWithKeepAlive = new ArrayList<>();
        List<Company> companiesWithOutKeepAlive = new ArrayList<>();
        resultRaw.addAll(getListByCriteria(realm, dao,
                () -> queries.get("queryKeepAlivePorEmpresa" + realm),
                () -> new Object[]{}));


        // Se separan las empresas que tienen moviles con KA (keep alive) y las que no
        // dado que una empresa puede tener tanto moviles con KA como moviles sin KA,
        // se hacen dos listas por separado.
        resultRaw.stream().forEach((Map company) -> {
            Company c = new Company().put("companyId", company.get("companyId"))
                    .put("companyRut", company.get("companyRut"))
                    .put("companyName", company.get("companyName"));

            if ((Integer) company.get("hasKeepAlive") == 1) {
                companiesWithKeepAlive.add(c.put("withoutKA", 0).put("withKA", company.get("countByCompany")));
            } else {
                companiesWithOutKeepAlive.add(c.put("withKA", 0).put("withoutKA", company.get("countByCompany")));
            }
        });

        // Se recorren ambas listas fusionando en un solo objeto cada empresa,
        // contando sus moviles con KA y sus moviles sin KA
        companiesWithOutKeepAlive.stream().forEach((comWithoutKA) -> {
            companiesWithKeepAlive.stream().filter((comWKA) -> comWKA.getInteger("companyId").equals(comWithoutKA.getInteger("companyId"))).findFirst().ifPresent((empresaConKa) -> {
                        empresaConKa.put("withoutKA", comWithoutKA.getInteger("withoutKA"));
                        comWithoutKA.put("hasMobileWithKA", true);
                    }
            );
        });
        // Si hay empresas que solo tienen moviles SIN KA, se agregan a la lista a retornar
        companiesWithOutKeepAlive.stream().filter((comp) -> comp.get("hasMobileWithKA") == null).forEach((compSinKa -> companiesWithKeepAlive.add(compSinKa)));

        resultRaw.clear();
        companiesWithOutKeepAlive.clear();

        return companiesWithKeepAlive;
    }

    private String getQueryWithLimitAndOffset(String query, String realm, Optional<Paginated> paginated, Optional<QueryFilter> filter) {
        StringBuilder filteredQuery = new StringBuilder(queries.get(query + realm));
        if (filter != null && filter.isPresent()) {
            paginated.ifPresent(p -> {
                filteredQuery.append(" LIMIT ").append(p.getLimit()).append(" OFFSET ").append(p.getOffset());
            });
        }
        return filteredQuery.toString();
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public BasicEntity saveReport(String realm, BasicEntity basicEntity, String query) throws Exception {
        String queryToExcecute = queries.get(query + realm);
        KeyHolder keyHolder = new GeneratedKeyHolder();
                
        getJdbcTemplate(dao, realm).update((Connection s) -> {
            PreparedStatement ps = s.prepareStatement(queryToExcecute, new String[]{"id"});
            ps.setObject(1, basicEntity.get("fileName"));
            ps.setObject(2, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            ps.setObject(3, basicEntity.get("reportTypeId"));
            ps.setObject(4, basicEntity.get("userName"));
            return ps;
        }, keyHolder);
        
        basicEntity.put("id", keyHolder.getKey().intValue());
        return basicEntity;
        
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public BasicEntity updateReport(String realm, BasicEntity basicEntity, String query) throws Exception {
        String queryToExcecute = queries.get(query + realm);
        Object[] args = new Object[3];
        args[0] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(basicEntity.getLong("successDate")));
        args[1] = basicEntity.get("error");
        args[2] = basicEntity.get("reportId");
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
