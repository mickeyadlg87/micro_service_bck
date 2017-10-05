package com.redd.backoffice.services;

import cl.tastets.life.core.framework.exceptions.WebException;
import cl.tastets.life.objects.BasicEntity;
import cl.tastets.life.objects.RealmEnum;
import cl.tastets.life.objects.utils.RequestData;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.redd.backoffice.controllers.query.PotaController;
import io.swagger.annotations.Api;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servicio relacionado con los comandos pota
 * 
 * @author aleal
 */
@RestController
@RequestMapping(value = "/backoffice/pota", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "Air Programming Service")
public class PotaService {
    
    @Autowired
    private PotaController potaControl;
    
    private static final Logger log = LoggerFactory.getLogger("POTA");
    
    /**
     * 
     * @param realm
     * @param deviceTypeId
     * @param codeTypeName
     * @return
     * @throws Exception 
     */
    @RequestMapping(value = "/getCommandByDeviceType", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE)
    @HystrixCommand(fallbackMethod = "defaultCommandByDevice")
    public BasicEntity commandByDeviceType(@RequestParam(value = "realm") String realm,
            @RequestParam(value = "deviceTypeId") Integer deviceTypeId,
            @RequestParam(value = "codeType") String codeTypeName)
            throws Exception {
        try {
            return potaControl.getCommandByDeviceType(realm, deviceTypeId, codeTypeName);
        } catch (Exception e) {
            throw new WebException("Error al buscar comando por tipo de dispositivo y nombre " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     *
     * @param realm
     * @param idPotaMsg
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getCallbackById", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE)
    @HystrixCommand(fallbackMethod = "defaultCallbackPotaById")
    public BasicEntity callbackByIdPotaMsg(@RequestParam(value = "realm") String realm,
            @RequestParam(value = "idPotaMsg") Integer idPotaMsg) throws Exception {
        try {
            return potaControl.getResponseCallbackById(realm, idPotaMsg);
        } catch (Exception e) {
            log.error("error al buscar respuesta del callback ", e);
            throw new WebException("Error al buscar respuesta al comando " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * 
     * @param realm
     * @param imei
     * @param requestData
     * @return
     * @throws Exception 
     */
    @RequestMapping(value = "/getCallbackByImei", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultListCallbackPota")
    public List<BasicEntity> callbackByImeiPotaMsg(@RequestParam(value = "realm") String realm,
            @RequestParam(value = "mid") String imei,
            @RequestBody RequestData requestData) throws Exception {
        try {
            return potaControl.getListCallbackByImei(realm, imei, requestData);
        } catch (Exception e) {
            log.error("error al buscar respuesta por imei ", e);
            throw new WebException("Error al buscar respuestas callback por imei", HttpStatus.BAD_REQUEST);
        }
    }
    
    
    /**
     * 
     * @param realm
     * @return
     * @throws Exception 
     */
    @RequestMapping(value = "/getTypes", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE)
    @HystrixCommand(fallbackMethod = "defaultCommandTypes")
    public List<BasicEntity> commandTypes(@RequestParam(value = "realm") String realm)
            throws Exception {
        try {
            return potaControl.getCommandType(realm);
        } catch (Exception e) {
            throw new WebException("Error al buscar tipos de comandos " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * 
     * @param realm
     * @param deviceTypeId
     * @return
     * @throws Exception 
     */
    @RequestMapping(value = "/getAllByDeviceType", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @HystrixCommand(fallbackMethod = "defaultListCommandByDevice")
    public List<BasicEntity> commandListByDevTypes(@RequestParam(value = "realm") String realm, @RequestParam(value = "deviceTypeId") Integer deviceTypeId)
            throws Exception {
        try {
            return potaControl.getListCommandById(realm, deviceTypeId);
        } catch (Exception e) {
            throw new WebException("Error al buscar lsita de comandos por Id " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * 
     * @param request
     * @param realm
     * @return 
     */
    @RequestMapping(value = "/saveCallbackResponse/{realm}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultCallbackPota")
    public BasicEntity saveCallbackPota(@RequestBody BasicEntity request, @PathVariable String realm) {
        try {
            return potaControl.saveCallbackResponse(request, realm);
        } catch (Exception e) {
            log.error("Error en callback pota", e);
            throw new WebException("Error al guardar callback " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * 
     * @param platform
     * @param imei
     * @param codeId
     * @return 
     */
    @RequestMapping(value = "/sendPotaCommand", method = RequestMethod.POST)
    @HystrixCommand(fallbackMethod = "defaultSendPota")
    public BasicEntity sendPotaCommand(@RequestParam(value = "platform") String platform,
                                       @RequestParam(value = "mid") String imei,
                                       @RequestParam(value = "idPotaCommand") Integer codeId) {

        try {
            return potaControl.sendPotaCommand(RealmEnum.valueOf(platform), imei, codeId, RequestData.from());
        } catch (Exception e) {
            log.error("Error enviando pota", e);
            throw new WebException("Error enviando pota : " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     *
     * @param platform
     * @param imei
     * @param codeId
     * @param requestData
     * @return
     */
    @RequestMapping(value = "/sendCommandWithVariables", method = RequestMethod.POST)
    @HystrixCommand(fallbackMethod = "defaultSendPotaWithParam")
    public BasicEntity sendPotaCommandWithParameters(@RequestParam(value = "platform") String platform,
                                                     @RequestParam(value = "mid") String imei,
                                                     @RequestParam(value = "idPotaCommand") Integer codeId,
                                                     @RequestBody RequestData requestData) {

        try {
            return potaControl.sendPotaCommand(RealmEnum.valueOf(platform), imei, codeId, requestData);
        } catch (Exception e) {
            log.error("Error enviando pota con parametros", e);
            throw new WebException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    
    /**
     * FALLBACK METHODS
     */
        
    
    public BasicEntity defaultCommandByDevice(String realm, Integer deviceTypeId, String codeTypeName) {
        return new BasicEntity();
    }
    
    public List<BasicEntity> defaultListCommandByDevice(String realm, Integer deviceTypeId) {
        return Arrays.asList(new BasicEntity());
    }
    
    public List<BasicEntity> defaultListCallbackPota(String realm, String imei, RequestData request) {
        return Arrays.asList(new BasicEntity());
    }
    
    public BasicEntity defaultCallbackPota(BasicEntity req, String realm) {
        return new BasicEntity();
    }
    
    public BasicEntity defaultCallbackPotaById(String realm, Integer idPota) {
        return new BasicEntity();
    }
    
    public BasicEntity defaultSendPota(String platform, String imei, Integer idCode) {
        return new BasicEntity();
    }

    public BasicEntity defaultSendPotaWithParam(String platform, String imei, Integer idCode, RequestData request) {
        return new BasicEntity();
    }

    public List<BasicEntity> defaultCommandTypes(String realm) {
        return Arrays.asList(new BasicEntity());
    }

}
