package com.redd.backoffice.dao.impl;

import cl.tastets.life.core.framework.dao.DataSourceDao;
import cl.tastets.life.objects.BasicEntity;
import cl.tastets.life.objects.Company;
import cl.tastets.life.objects.RealmEnum;
import cl.tastets.life.objects.utils.Paginated;
import cl.tastets.life.objects.utils.QueryFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redd.backoffice.dao.queries.QueryClient;
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
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * Implementacion dao de clientes, realiza las diferentes operaciones sobre 
 * esta entidad
 * 
 * @author aleal
 */
@Repository
public class BackoffClientDaoImpl implements DaoTemplates {
    
    @Autowired
    private DataSourceDao dao;

    private Map<String, String> queries;
    private ObjectMapper mapperForJson;
    private final Logger logger = LoggerFactory.getLogger(BackoffClientDaoImpl.class);
    
    
    @PostConstruct
    public void initQueries() {
        mapperForJson = new ObjectMapper();
        queries = new HashMap<>();
        queries.put("queryGenericCustomer" + RealmEnum.backoffice, QueryClient.queryClientBackoffice);
        queries.put("queryGenericFunPackage" + RealmEnum.backoffice, QueryClient.queryFunctionalPackageBackoffice);
        queries.put("queryTypeSaleByCustomer" + RealmEnum.backoffice, QueryClient.queryTypeSaleByClientBackoffice);
        queries.put("insertCustomer" + RealmEnum.backoffice, QueryClient.insertClientBackoffice);
        queries.put("updateCustomer" + RealmEnum.backoffice, QueryClient.updateClientBackoffice);
        queries.put("insertFunPackage" + RealmEnum.backoffice, QueryClient.insertFuntionalPackageBackoffice);
        queries.put("updateFunPackage" + RealmEnum.backoffice, QueryClient.updateFunctionalPackageBackoffice);
        queries.put("insertTypeSale" + RealmEnum.backoffice, QueryClient.insertTypeSaleByCustomerBackoffice);
        queries.put("updateTypeSale" + RealmEnum.backoffice, QueryClient.updateTypeSaleByCustomerBackoffice);
        queries.put("deleteTypeSale" + RealmEnum.backoffice, QueryClient.deleteTypeSaleByCustomerBackoffice);
    }

    public Company findClientById(String realm, Integer id, Optional<Paginated> paginated, Optional<QueryFilter> filter) {
        BasicEntity k = new BasicEntity();
        k.put("id", id);
        fillFilterWithCriteria(filter, k);
        Company company = new Company();
        List<Company> lc = getCustomerListByFilter(realm, "queryGenericCustomer", paginated, filter);
        company.putAll(lc.isEmpty() ? new Company() : lc.get(0));
        //Se agrega a la empresa encontrada la informacion de los tipos de venta en el atributo "typeSaleList"
        if (!company.isEmpty()) {
            company.put("typeSaleList", getListByCriteria(realm, dao,
                    () -> queries.get("queryTypeSaleByCustomer" + realm),
                    () -> new Object[]{"TYPE_SALE", company.getInteger("id")} //codigo en estado_general para los tipos de venta
                    )
            );
        }
        return company;
    }

    public Company findClientByExternalIdAndPlataform(String realm, Integer externalId, Optional<Paginated> paginated, Optional<QueryFilter> filter) {
        BasicEntity k = new BasicEntity();
        k.put("externalId", externalId);
        k.put("plataform", realm);
        fillFilterWithCriteria(filter, k);
        Company company = new Company();
        List<Company> lc = getCustomerListByFilter("backoffice", "queryGenericCustomer", paginated, filter);
        //Se agrega a la empresa encontrada la informacion de los tipos de venta en el atributo "typeSaleList"
        company.putAll(lc.isEmpty() ? new Company() : lc.get(0));
        if (!company.isEmpty()) {
            company.put("typeSaleList", getListByCriteria("backoffice", dao,
                    () -> queries.get("queryTypeSaleByCustomerbackoffice"),
                    () -> new Object[]{"TYPE_SALE", company.getInteger("id")} //codigo en estado_general para los tipos de venta
                    )
            );
        }
        return company;
    }
    
    public List<Company> findFunctionalitiesPackages(String realm, Optional<Paginated> paginated, Optional<QueryFilter> filter) {
        List<Map<String, Object>> listaFuncRaw = getListByFilter(realm, "queryGenericFunPackage", paginated, filter);
        List<Company> parsedLc = new ArrayList<>();
        
        listaFuncRaw.stream().map((comp) -> {
            Company newComp = new Company();
            newComp.putAll(comp);
            return newComp;
        }).forEach((newfunc) -> {
            String profileString = newfunc.getString("profile");
            newfunc.put("profile", stringToListProfiles(profileString));
            parsedLc.add(newfunc);
        });

        return parsedLc;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Company save(String realm, Company company, String query) throws Exception {
        String queryToExecute = queries.get(query + realm);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate(dao, realm).update((Connection c) -> {
            PreparedStatement ps = c.prepareStatement(queryToExecute, new String[]{"id"});
            ps.setObject(1, company.get("customerName"));
            ps.setObject(2, company.get("rut"));
            ps.setObject(3, company.get("businessName"));
            ps.setObject(4, company.get("giro"));
            ps.setObject(5, company.get("market"));
            ps.setObject(6, company.get("segment"));
            ps.setObject(7, company.get("webPage"));
            ps.setObject(8, company.get("address"));
            ps.setObject(9, company.get("customerExternalId"));
            ps.setObject(10, company.get("executive"));
            ps.setObject(11, company.get("executiveType"));
            ps.setObject(12, company.get("executivePhone"));
            ps.setObject(13, company.get("executiveAnnex"));
            ps.setObject(14, company.get("executiveMail"));
            ps.setObject(15, company.get("tradeContactName"));
            ps.setObject(16, company.get("tradeContactPhone"));
            ps.setObject(17, company.get("tradeContactPhoneTwo"));
            ps.setObject(18, company.get("tradeContactJobTitle"));
            ps.setObject(19, company.get("tradeContactMail"));
            ps.setObject(20, company.get("supplierName"));
            ps.setObject(21, company.get("supplierPhone"));
            ps.setObject(22, company.get("supplierPhoneTwo"));
            ps.setObject(23, company.get("supplierJobTitle"));
            ps.setObject(24, company.get("supplierMail"));
            ps.setObject(25, company.get("paymentCondition"));
            ps.setObject(26, company.get("oc"));
            ps.setObject(27, company.get("supplierLiberationOC"));
            ps.setObject(28, company.get("platform"));
            ps.setObject(29, company.get("modality"));
            ps.setObject(30, company.get("termContract"));
            ps.setObject(31, company.get("saleRate"));
            ps.setObject(32, company.get("prepaidRate"));
            ps.setObject(33, company.get("searchingRate"));
            ps.setObject(34, company.get("rentRate"));
            ps.setObject(35, company.get("planType"));
            ps.setObject(36, company.get("contractDate") == null ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(company.getLong("contractDate"))));
            ps.setObject(37, company.get("installedKit"));
            ps.setObject(38, company.get("quantityKit"));
            ps.setObject(39, company.get("accessory"));
            ps.setObject(40, company.get("platformManagerUser"));
            ps.setObject(41, company.get("platformManagerPhone"));
            ps.setObject(42, company.get("platformManagerSecondaryPhone"));
            ps.setObject(43, company.get("platformManagerJobTitle"));
            ps.setObject(44, company.get("platformManagerMail"));
            ps.setObject(45, company.get("observation"));
            return ps;
        }, keyHolder);

        company.put("id", keyHolder.getKey().intValue());

        // Vincula al cliente con los diferentes tipos de venta
        if (company.get("typeSaleList") != null && !company.getList("typeSaleList").isEmpty()) {
            AtomicInteger count = new AtomicInteger();
            company.getList("typeSaleList").stream().forEach((Map m) -> {
                        if ((boolean) m.get("active")) {
                            getJdbcTemplate(dao, realm).update(queries.get("insertTypeSale" + realm), new Object[]{company.getInteger("id"), m.get("typeSaleId"), m.get("rate")});
                            company.put("typesSaleAdded", count.incrementAndGet());
                        }
                    }
            );
        }

        return company;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Company update(String realm, Company company, String query) throws Exception {
        AtomicInteger countAdd = new AtomicInteger();
        AtomicInteger countUp = new AtomicInteger();
        AtomicInteger countDel = new AtomicInteger();
        String queryToExcecute = queries.get(query + realm);
        Object[] args = new Object[44];
        args[0] = company.get("customerName");
        args[1] = company.get("rut");
        args[2] = company.get("businessName");
        args[3] = company.get("giro");
        args[4] = company.get("market");
        args[5] = company.get("segment");
        args[6] = company.get("webPage");
        args[7] = company.get("address");
        args[8] = company.get("executive");
        args[9] = company.get("executiveType");
        args[10] = company.get("executivePhone");
        args[11] = company.get("executiveAnnex");
        args[12] = company.get("executiveMail");
        args[13] = company.get("tradeContactName");
        args[14] = company.get("tradeContactPhone");
        args[15] = company.get("tradeContactPhoneTwo");
        args[16] = company.get("tradeContactMail");
        args[17] = company.get("supplierName");
        args[18] = company.get("supplierPhone");
        args[19] = company.get("supplierPhoneTwo");
        args[20] = company.get("supplierMail");
        args[21] = company.get("paymentCondition");
        args[22] = company.get("modality");
        args[23] = company.get("termContract");
        args[24] = company.get("prepaidRate");
        args[25] = company.get("searchingRate");
        args[26] = company.get("rentRate");
        args[27] = company.get("planType");
        args[28] = company.get("contractDate") == null ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(company.getLong("contractDate")));
        args[29] = company.get("installedKit");
        args[30] = company.get("quantityKit");
        args[31] = company.get("accessory");
        args[32] = company.get("platformManagerUser");
        args[33] = company.get("platformManagerPhone");
        args[34] = company.get("platformManagerMail");
        args[35] = company.get("supplierJobTitle");
        args[36] = company.get("oc");
        args[37] = company.get("supplierLiberationOC");
        args[38] = company.get("tradeContactJobTitle");
        args[39] = company.get("saleRate");
        args[40] = company.get("platformManagerSecondaryPhone");
        args[41] = company.get("platformManagerJobTitle");
        args[42] = company.get("observation");
        args[43] = company.get("id");
        int id = getJdbcTemplate(dao, realm).update(queryToExcecute, args);

        // Vincula al cliente con los diferentes tipos de venta
        if (company.get("typeSaleList") != null && !company.getList("typeSaleList").isEmpty()) {
            company.getList("typeSaleList").stream().forEach((Map m) -> {
                        if ((boolean) m.get("active")) {
                            try {
                                // intenta insertar el nuevo tipo de venta para el cliente
                                getJdbcTemplate(dao, realm).update(queries.get("insertTypeSale" + realm), new Object[]{company.getInteger("id"), m.get("typeSaleId"), m.get("rate")});
                                countAdd.incrementAndGet();
                            } catch (DuplicateKeyException e) {
                                // si es que ya existe un registro, actualiza unicamente la tarifa pactada
                                getJdbcTemplate(dao, realm).update(queries.get("updateTypeSale" + realm), new Object[]{m.get("rate"), company.getInteger("id"), m.get("typeSaleId")});
                                countUp.incrementAndGet();
                            }
                        } else {
                            // elimina el tipo de venta vinculado al cliente
                            getJdbcTemplate(dao, realm).update(queries.get("deleteTypeSale" + realm), new Object[]{company.getInteger("id"), m.get("typeSaleId")});
                            countDel.incrementAndGet();
                        }
                    }
            );
        }

        return new Company().put("updateRow", id).put("addSaleTypes", countAdd.get()).put("updateSaleTypes", countUp.get()).put("deleteSaleTypes", countDel.get());
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public Company saveFunPackage(String realm, Company company, String query) throws Exception {

        String queryToExecute = queries.get(query + realm);
        String profileAsString = mapperForJson.writeValueAsString(company.get("profile"));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate(dao, realm).update((Connection c) -> {
            PreparedStatement ps = c.prepareStatement(queryToExecute, new String[]{"id"});
            ps.setObject(1, company.get("name"));
            ps.setObject(2, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            ps.setObject(3, profileAsString);
            ps.setObject(4, company.get("active"));
            return ps;
        }, keyHolder);
        
        company.remove("profile");
        return company.put("id", keyHolder.getKey().intValue());

    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public Company updateFunPackage(String realm, Company company, String query) throws Exception {
        String queryToExcecute = queries.get(query + realm);
        Object[] args = new Object[4];
        args[0] = company.get("name");
        args[1] = mapperForJson.writeValueAsString(company.get("profile"));
        args[2] = company.get("active");
        args[3] = company.get("id");
        int id = getJdbcTemplate(dao, realm).update(queryToExcecute, args);
        company.put("updateRow", id);
        company.remove("profile");
        return company;
    }

    private List<Company> getCustomerListByFilter(String realm, String query, Optional<Paginated> paginated, Optional<QueryFilter> filter) {
        List<Company> result = new ArrayList<>();
        result.addAll(getListByCriteria(realm, dao,
                () -> filterQueryValues(query + realm, paginated, filter, true),
                () -> new Object[]{}));
        return result;
    }
    
    private List<Map<String,Object>> getListByFilter(String realm, String query, Optional<Paginated> paginated, Optional<QueryFilter> filter) {
        List<Map<String, Object>> result = new ArrayList<>();
        result.addAll(getListByCriteria(realm, dao,
                () -> filterQueryValues(query + realm, paginated, filter, true),
                () -> new Object[]{}));
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
                    filteredQuery.append(" e.id").append(" = ").append(k.get("id")).append(" AND ");
                }
                if (k.get("name") != null) {
                    filteredQuery.append(" e.nombre").append(" LIKE '%").append(k.get("name")).append("%' AND ");
                }
                if (k.get("rut") != null) {
                    filteredQuery.append(" e.rut").append(" LIKE '%").append(k.get("rut")).append("%' AND ");
                }
                if (k.get("externalId") != null) {
                    filteredQuery.append(" e.id_empresa").append(" = ").append(k.get("externalId")).append(" AND ");
                }
                if (k.get("plataform") != null) {
                    filteredQuery.append(" e.plataforma").append(" LIKE '%").append(k.get("plataform")).append("%' AND ");
                }          
                if (k.get("active") != null) {
                    filteredQuery.append(" pf.activo").append(" = ").append(k.get("active")).append(" AND ");
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
                    queryReal.append(" ORDER BY pf.");
                    if (f.getString("sort").contains("name")) {
                        queryReal.append(f.getString("sort").replace("name", "nombre"));
                    }
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
    
    private List<Map> stringToListProfiles(String profilesString) {
        List<Map> listaProfiles = new ArrayList<>();

        try {
            listaProfiles = mapperForJson.readValue(profilesString, new ArrayList<>().getClass());
        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }

        return listaProfiles;
    }

}
