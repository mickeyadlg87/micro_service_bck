package com.redd.backoffice.dao.impl;

import cl.tastets.life.core.framework.dao.DataSourceDao;
import cl.tastets.life.objects.BasicEntity;
import cl.tastets.life.objects.RealmEnum;
import cl.tastets.life.objects.utils.Paginated;
import cl.tastets.life.objects.utils.QueryFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redd.backoffice.dao.queries.QueryPota;
import com.redd.backoffice.utils.DaoTemplates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author aleal
 */
@Repository
public class PotaDaoImpl implements DaoTemplates {

    @Autowired
    private DataSourceDao dao;
    @Value("${microservices.pota}")
    private String servicePota;
    @Value("${microservices.callback}")
    private String serviceCallback;

    private Map<String, String> queries;
    
    private RestTemplate rt;
    private HttpHeaders headers;
    private HttpEntity<String> entity;
    private ObjectMapper jacksonJsonObjectMapper;
    
    @PostConstruct
    public void initQueries() {
        
        queries = new HashMap<>();
        rt = new RestTemplate();
        headers = new HttpHeaders();
        jacksonJsonObjectMapper = new ObjectMapper();

        headers.setAccept(Arrays.asList(MediaType.APPLICATION_FORM_URLENCODED));
        entity = new HttpEntity<>("parameters", headers);
        
        
        // se establece timeout del rest template al momento de intentar conectar a pota
        rt.setRequestFactory(new SimpleClientHttpRequestFactory());
        SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) rt.getRequestFactory();
        rf.setReadTimeout(10000);
        rf.setConnectTimeout(10000);
        
        queries.put("queryAllCommandFiltered" + RealmEnum.backoffice, QueryPota.queryComandosPotaBackoffice);
        queries.put("queryHistoryPotaFiltered" + RealmEnum.backoffice, QueryPota.queryHistorialPotaBackoffice);
        queries.put("queryCommandType" + RealmEnum.backoffice, QueryPota.queryTiposComandosPotaBackoffice);
        queries.put("insertPotaMsg" + RealmEnum.backoffice, QueryPota.insertHistoryPotaBackoffice);
        queries.put("updatePotaMsg" + RealmEnum.backoffice, QueryPota.updateHistoryPotaBackoffice);
    }
    
    public BasicEntity findCommandByDeviceAndName(String realm, Integer deviceTypeId, String codeTypeName) throws Exception {
        BasicEntity k = new BasicEntity();
        QueryFilter filter = QueryFilter.from();
        Paginated paginated = Paginated.from();
        k.put("deviceTypeId", deviceTypeId);
        k.put("commandType", codeTypeName);
        fillFilterWithCriteria(Optional.of(filter), k);
        
        BasicEntity objectCommand = new BasicEntity();
        objectCommand.putAll(getByCriteria(realm, dao,
                () -> filterQueryValues("queryAllCommandFiltered" + realm, Optional.of(paginated), Optional.of(filter), Boolean.TRUE),
                () -> new Object[]{}));

        return objectCommand;
    }
    
    public BasicEntity findResponseCallbackById(String realm, Integer potaId) throws Exception {
        BasicEntity k = new BasicEntity();
        QueryFilter filter = QueryFilter.from();
        Paginated paginated = Paginated.from();
        k.put("idMsgPota", potaId);
        fillFilterWithCriteria(Optional.of(filter), k);

        BasicEntity responseCommand = new BasicEntity();
        responseCommand.putAll(getByCriteria(realm, dao,
                () -> filterQueryValues("queryHistoryPotaFiltered" + realm, Optional.of(paginated), Optional.of(filter), Boolean.TRUE),
                () -> new Object[]{}));

        return responseCommand;
    }
    
    public BasicEntity findPotaCommandById(String realm, Integer potaId) throws Exception {
        BasicEntity k = new BasicEntity();
        QueryFilter filter = QueryFilter.from();
        Paginated paginated = Paginated.from();
        k.put("id", potaId);
        fillFilterWithCriteria(Optional.of(filter), k);

        BasicEntity objectCommand = new BasicEntity();
        objectCommand.putAll(getByCriteria(realm, dao,
                () -> filterQueryValues("queryAllCommandFiltered" + realm, Optional.of(paginated), Optional.of(filter), Boolean.TRUE),
                () -> new Object[]{}));

        return objectCommand;
    }
    
    /**
     * Realiza el proceso de encolar mensajes ante POTA
     * @param params
     * @return objeto con response y id del mensaje encolado
     * @throws IOException 
     */
    public BasicEntity createPotaCommand(BasicEntity params) throws IOException {

        BasicEntity response = new BasicEntity();
        StringBuilder urlWithParam = new StringBuilder();

        urlWithParam.append(servicePota).append("script/").append("?imeis=").append(params.get("mid")).append("&")
                // script = Script a ejecutar, cada comando debe estar separado por un salto de línea,
                //          si el comando es binario debe estar representado como hexadecimal
                .append("script=").append(logicForCommand(params)).append("&")
                // name = Nombre del script. Este valor puede servir para correlacionar programaciones entre sistemas.
                // binary = Define si el script es binario o no. Debe ser “true” o "false"
                // timeout = Tiempo en el que se considera un comando como no respondido (en segundos)
                // callback_url = Define la URL donde POTA notificará éxito o error en el script.
                .append("name=").append(params.get("serialPotaMsg"))
                .append("&binary=false&timeout=30&")
                .append("callback_url=").append(serviceCallback).append(params.get("platform"));
        
        String status = rt.exchange(urlWithParam.toString(), HttpMethod.POST, entity, String.class).getBody();
        HashMap statusHash = jacksonJsonObjectMapper.readValue(status, HashMap.class);
        response.putAll(statusHash);
        // devuelvo el id serial del mensaje pota encolado, esto para poder
        // realizar skip o retry de ser necesario
        response.put("idPotaMsg", params.get("serialPotaMsg"));

        return response;
    }


    /**
     * metodo para extraer parametros configurables que deben ir dentro de los comandos POTA
     * @param params
     * @param paginated
     * @param filter
     * @return
     * @throws Exception
     */
    public BasicEntity setParametersForCommand(BasicEntity params, Optional<Paginated> paginated, Optional<QueryFilter> filter) throws Exception {

        BasicEntity response = new BasicEntity();
        response.putAll(params);

        filter.ifPresent((f) -> {
            f.getList("filter").parallelStream().forEach((Map m) -> {
                BasicEntity k = new BasicEntity();
                k.putAll(m);
                if (k.get("hourmeterTime") != null) { // sincronizacion de horometro

                    String time = String.format("%05d:%02d:%02d",
                            TimeUnit.MILLISECONDS.toHours(k.getLong("hourmeterTime")),
                            TimeUnit.MILLISECONDS.toMinutes(k.getLong("hourmeterTime")) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(k.getLong("hourmeterTime"))),
                            TimeUnit.MILLISECONDS.toSeconds(k.getLong("hourmeterTime")) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(k.getLong("hourmeterTime"))));

                    response.put("hourmeterTime", time);

                }
                if (k.get("odometerKm") != null) { // sincronizacion de odometro
                    response.put("odometerKm", k.get("odometerKm"));
                }
            });
        });

        return response;
    }
    
    /**
     * Dado que hubo timeout en el envio del comando, se envia un SKIP a pota
     * para que ya no tome en consideracion el mensaje identificado con script_name
     * (idPotaMsg)
     * @param oid id de la linea de comando que se desea saltar
     * @param imei identificador del dispositivo
     * @return 
     * @throws IOException 
     */
    public BasicEntity skipPotaCommand(String oid, String imei) throws IOException, Exception {

        BasicEntity response = new BasicEntity();
        StringBuilder urlWithParam = new StringBuilder();

        urlWithParam.append(servicePota).append("imei/").append(imei)
                .append("?action=SKIP&loid=").append(oid);

        String status = rt.exchange(urlWithParam.toString(), HttpMethod.POST, entity, String.class).getBody();
        HashMap statusHash = jacksonJsonObjectMapper.readValue(status, HashMap.class);
        response.putAll(statusHash);
        return response;
    }
    
    public List<BasicEntity> findCommandListByDevType(String realm, Integer deviceTypeId) {

        List<BasicEntity> result = new ArrayList<>();
        BasicEntity k = new BasicEntity();
        QueryFilter filter = QueryFilter.from();
        Paginated paginated = Paginated.from();
        k.put("deviceTypeId", deviceTypeId);
        fillFilterWithCriteria(Optional.of(filter), k);

        result.addAll(getListByCriteria(realm, dao,
                () -> filterQueryValues("queryAllCommandFiltered" + realm, Optional.of(paginated), Optional.of(filter), Boolean.TRUE),
                () -> new Object[]{}));
        
        return result;
    }
    
    public List<BasicEntity> findListCallbackByImei(String realm, String imei, Optional<Paginated> paginated, Optional<QueryFilter> filter) {

        List<BasicEntity> result = new ArrayList<>();
        BasicEntity k = new BasicEntity();
        k.put("imei", imei);
        fillFilterWithCriteria(filter, k);
        filter.get().put("sort", "createDate");

        result.addAll(getListByCriteria(realm, dao,
                () -> filterQueryValues("queryHistoryPotaFiltered" + realm, paginated, filter, Boolean.TRUE),
                () -> new Object[]{}));

        return result;
    }
    
    public List<BasicEntity> findCommandTypes(String realm) throws Exception {
        String queryToExcecute = queries.get("queryCommandType" + realm);
        List<BasicEntity> result = new ArrayList<>();

        result.addAll(getListByCriteria(realm, dao,
                () -> queryToExcecute,
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
                if (k.get("deviceTypeId") != null) {
                    filteredQuery.append(" cp.id_tipo_dispositivo").append(" = ").append(k.get("deviceTypeId")).append(" AND ");
                }
                if (k.get("commandType") != null) {
                    filteredQuery.append(" cp.descripcion_comando").append(" = '").append(k.get("commandType")).append("' AND ");
                }
                if (k.get("id") != null) {
                    filteredQuery.append(" cp.id").append(" = ").append(k.get("id")).append(" AND ");
                }
                if (k.get("idMsgPota") != null) {
                    filteredQuery.append(" hcp.id_msg").append(" = ").append(k.get("idMsgPota")).append(" AND ");
                }
                if (k.get("imei") != null) {
                    filteredQuery.append(" hcp.identificador_unico_dispositivo").append(" = ").append(k.get("imei")).append(" AND ");
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
                    queryReal.append(" ORDER BY hcp.");
                    if (f.getString("sort").contains("name")) {
                        queryReal.append(f.getString("sort").replace("name", "descripcion_comando"));
                    }
                    if (f.getString("sort").contains("createDate")) {
                        queryReal.append(f.getString("sort").replace("createDate", "fecha_ingreso")).append(" DESC");
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
    public Integer generatePotaMsgId(String realm, BasicEntity basicEntity, String query) throws Exception {
        String queryToExecute = queries.get(query + realm);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate(dao, realm).update((Connection s) -> {
            PreparedStatement ps = s.prepareStatement(queryToExecute, new String[]{"id"});
            ps.setObject(1, basicEntity.get("deviceTypeId"));
            ps.setObject(2, basicEntity.get("potaCommandId"));
            ps.setObject(3, basicEntity.get("mid"));
            ps.setObject(4, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            ps.setObject(5, basicEntity.get("platform"));
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public BasicEntity updateHistoryPotaMsg(String realm, BasicEntity basicEntity, String query) throws Exception {
        BasicEntity resp = new BasicEntity();
        String queryToExcecute = queries.get(query + realm);
        Object[] args = new Object[4];
        args[0] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        args[1] = basicEntity.get("status");
        args[2] = jacksonJsonObjectMapper.writeValueAsString(basicEntity);
        args[3] = basicEntity.get("script_name");

        int id = getJdbcTemplate(dao, realm).update(queryToExcecute, args);
        resp.put("updatedRow", id);
        resp.put("idPotaMsg", basicEntity.getString("script_name"));

        return resp;
    }
    
    private String logicForCommand(BasicEntity paramsForPota) {

        // cada comando debe ir separado por un salto de linea
        String com = paramsForPota.getString("command").replaceAll("\\|", "\n");

        // si el comando debe llevar el imei
        if (com.contains("IMEI")) {
            com = com.replace("%IMEI%", paramsForPota.getString("mid"));
        }
        // si el comando debe llevar el id del mensaje en pota
        if (com.contains("CORR")) {
            com = com.replace("%CORR%", String.valueOf(paramsForPota.getInteger("serialPotaMsg")));
        }
        // si el comando es para sincronizar el horometro
        if (com.contains("HOROMETER")) {
            com = com.replace("%HOROMETER%", paramsForPota.getString("hourmeterTime"));
        }

        // si el comando es para sincronizar el odometro
        if (com.contains("ODOMETER")) {
            com = com.replace("%ODOMETER%", paramsForPota.getString("odometerKm"));
        }

        // si el comando debe llevar los ultimos digitos del imei
        if (com.contains("LASTSIXIMEI")) {
            com = com.replace("%LASTSIXIMEI%", paramsForPota.getString("mid").substring(paramsForPota.getString("mid").length() - 6));
        }

        return com;

    }

}
