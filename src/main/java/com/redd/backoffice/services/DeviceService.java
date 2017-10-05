package com.redd.backoffice.services;

import cl.tastets.life.core.framework.exceptions.WebException;
import cl.tastets.life.objects.BasicEntity;
import cl.tastets.life.objects.Event;
import cl.tastets.life.objects.Parameter;
import cl.tastets.life.objects.backoffice.DeviceType;
import cl.tastets.life.objects.utils.RequestData;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.redd.backoffice.controllers.query.DeviceFindController;
import com.redd.backoffice.utils.CrudOperationEnum;
import io.swagger.annotations.Api;
import java.util.Arrays;
import java.util.List;
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
 *
 * @author aleal
 */
@RestController
@RequestMapping(value = "/backoffice/device", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "Device Service")
public class DeviceService {
    
    @Autowired
    private DeviceFindController devControl;

    /**
     *
     * @param realm
     * @param requestData
     * @return
     */
    @RequestMapping(value = "/type/getAll", method = RequestMethod.POST)
    @HystrixCommand(fallbackMethod = "defaultAllTypeGeneric")
    public List<DeviceType> getAllDeviceTypePost(@RequestParam(value = "realm") String realm, @RequestBody RequestData requestData) {
        try {
            return devControl.getAllDeviceType(realm, requestData);
        } catch (Exception e) {
            throw new WebException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     *
     * @param realm
     * @param deviceTypeId
     * @param requestData
     * @return
     */
    @RequestMapping(value = "/type/getEventsByTypeId", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultByTypeIdGeneric")
    public List<Event> getEventByDevIdPost(@RequestParam(value = "realm") String realm, @RequestParam(value = "deviceTypeId") Integer deviceTypeId,
            @RequestBody RequestData requestData) {

        try {
            return devControl.getEventsForDeviceType(realm, deviceTypeId, requestData);
        } catch (Exception e) {
            throw new WebException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * 
     * @param realm
     * @param deviceTypeId
     * @param requestData
     * @return 
     */
    @RequestMapping(value = "/type/getEventsForCertificate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultByTypeIdGeneric")
    public List<Event> getEventsForCertifificationByIdPost(@RequestParam(value = "realm") String realm, @RequestParam(value = "deviceTypeId") Integer deviceTypeId,
            @RequestBody RequestData requestData) {

        try {
            return devControl.getCertificateEventsForDevType(realm, deviceTypeId, requestData);
        } catch (Exception e) {
            throw new WebException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Almacena relacion evento por tipo de dispositivo
     *
     * @param basicEntity contiene id evento y id tipo de dispositivo
     * @return
     */
    @RequestMapping(value = "/eventType/save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BasicEntity save(@RequestBody BasicEntity basicEntity) {
        try {
            return devControl.crudEventForDevType(basicEntity, CrudOperationEnum.SAVE);
        } catch (RuntimeException e) {
            throw new WebException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * desvincula un evento de un tipo de dispositivo determinado
     *
     * @param realm
     * @param idEvent
     * @param idDeviceType
     * @return
     */
    @RequestMapping(value = "/eventType/delete/{realm}/{eventId}/{deviceTypeId}", method = RequestMethod.DELETE)
    public BasicEntity delete(@PathVariable("realm") String realm, @PathVariable("eventId") Integer idEvent, @PathVariable("deviceTypeId") Integer idDeviceType) {
        try {
            BasicEntity be = new BasicEntity();
            be.put("eventId", idEvent);
            be.put("deviceTypeId", idDeviceType);
            be.put("realm", realm);
            return devControl.crudEventForDevType(be, CrudOperationEnum.DELETE);
        } catch (RuntimeException e) {
            throw new WebException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Almacena relacion evento por tipo de dispositivo
     *
     * @param basicEntity contiene id evento y id tipo de dispositivo
     * @return
     */
    @RequestMapping(value = "/eventCertificate/save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BasicEntity saveCertificate(@RequestBody BasicEntity basicEntity) {
        try {
            if (basicEntity.containsKey("certificateEvent")) {
                return devControl.crudEventForDevType(basicEntity, CrudOperationEnum.SAVE);
            } else {
                throw new Exception("Missing certificateEvent value...");
            }
        } catch (Exception e) {
            throw new WebException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * desvincula un evento de un tipo de dispositivo determinado
     *
     * @param realm
     * @param idEvent
     * @param idDeviceType
     * @return
     */
    @RequestMapping(value = "/eventCertificate/delete/{realm}/{eventId}/{deviceTypeId}", method = RequestMethod.DELETE)
    public BasicEntity deleteCertificate(@PathVariable("realm") String realm, @PathVariable("eventId") Integer idEvent, @PathVariable("deviceTypeId") Integer idDeviceType) {
        try {
            BasicEntity be = new BasicEntity();
            be.put("eventId", idEvent);
            be.put("deviceTypeId", idDeviceType);
            be.put("certificateEvent", true);
            be.put("realm", realm);
            return devControl.crudEventForDevType(be, CrudOperationEnum.DELETE);
        } catch (RuntimeException e) {
            throw new WebException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }    

    /**
     * trae todos los accesorios disponibles
     *
     * @param realm
     * @param requestData
     * @return
     */
    @RequestMapping(value = "/accesories/getAll", method = RequestMethod.POST)
    @HystrixCommand(fallbackMethod = "defaultAllAccesories")
    public List<Parameter> getAllAccesories(@RequestParam(value = "realm") String realm, @RequestBody RequestData requestData) {
        try {
            return devControl.getAllAccesoriesWithFilter(realm, requestData);
        } catch (Exception e) {
            throw new WebException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * trae los accesorios disponibles para un tipo de dispositivo particular
     *
     * @param realm
     * @param deviceTypeId
     * @param requestData
     * @return
     */
    @RequestMapping(value = "/accesories/getByDeviceTypeId", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultByTypeIdGeneric")
    public List<Parameter> getAccesoriesByDevTypePost(@RequestParam(value = "realm") String realm, @RequestParam(value = "deviceTypeId") Integer deviceTypeId,
            @RequestBody RequestData requestData) {

        try {
            return devControl.getAccesoriesByDeviceType(realm, deviceTypeId, requestData);
        } catch (Exception e) {
            throw new WebException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Almacena relacion accesorio por tipo de dispositivo
     *
     * @param basicEntity contiene id accesorio y id tipo de dispositivo
     * @return
     */
    @RequestMapping(value = "/accesoryDeviceType/save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BasicEntity saveAccesory(@RequestBody BasicEntity basicEntity) {
        try {
            return devControl.crudAccesoriesForDevType(basicEntity, CrudOperationEnum.SAVE);
        } catch (RuntimeException e) {
            throw new WebException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * desvincula un accesorio de un tipo de dispositivo determinado
     *
     * @param realm
     * @param idAccesory
     * @param idDeviceType
     * @return
     */
    @RequestMapping(value = "/accesoryDeviceType/delete/{realm}/{accesoryId}/{deviceTypeId}", method = RequestMethod.DELETE)
    public BasicEntity deleteAccesory(@PathVariable("realm") String realm, @PathVariable("accesoryId") Integer idAccesory, @PathVariable("deviceTypeId") Integer idDeviceType) {
        try {
            BasicEntity be = new BasicEntity();
            be.put("accesoryId", idAccesory);
            be.put("deviceTypeId", idDeviceType);
            be.put("realm", realm);
            return devControl.crudAccesoriesForDevType(be, CrudOperationEnum.DELETE);
        } catch (RuntimeException e) {
            throw new WebException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * FALLBACK METHODS
     */
    /**
     *
     * @param realm
     * @param requestData
     * @return
     */
    public List<DeviceType> defaultAllTypeGeneric(String realm, RequestData requestData) {
        return Arrays.asList(new DeviceType());
    }

    public List<Parameter> defaultAllAccesories(String realm, RequestData requestData) {
        return Arrays.asList(new Parameter());
    }

    public List<Event> defaultByTypeIdGeneric(String realm, Integer id, RequestData requestData) {
        return Arrays.asList(new Event());
    }
}
