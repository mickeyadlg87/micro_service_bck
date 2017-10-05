package com.redd.backoffice.services;

import cl.tastets.life.core.framework.exceptions.WebException;
import cl.tastets.life.objects.BasicEntity;
import cl.tastets.life.objects.Event;
import cl.tastets.life.objects.utils.RequestData;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.redd.backoffice.controllers.query.EventFindController;
import io.swagger.annotations.Api;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping(value = "/backoffice/event", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "Event Service")
public class EventService {
    
    @Autowired
    private EventFindController eventControl;

    @RequestMapping(value = "/getAll", method = RequestMethod.POST)
    @HystrixCommand(fallbackMethod = "defaultAllEventGeneric")
    public List<Event> getAllEventsPost(@RequestParam(value = "realm") String realm, @RequestBody RequestData requestData) {
        try {
            return eventControl.findAllEventsByFilter(realm, requestData);
        } catch (Exception e) {
            throw new WebException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "/saveUnitCertified", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BasicEntity saveUnitCertified(@RequestBody BasicEntity unitCert) {
        try {
            return eventControl.insertUnitCertified(unitCert.getString("realm"), unitCert);
        } catch (Exception e) {
            throw new WebException("Error al guardar cetificador unidad", HttpStatus.BAD_REQUEST);
        }
    }
    
        @RequestMapping(value = "/updateUnitCertified", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BasicEntity updateUnitCertified(@RequestBody BasicEntity unitCert) {
        try {
            return eventControl.updateUnitCertified(unitCert.getString("realm"), unitCert);
        } catch (Exception e) {
            throw new WebException("Error al actualizar info certificador unidad", HttpStatus.BAD_REQUEST);
        }
    }
    
    
    @RequestMapping(value = "/getEventsForCertificateByMid", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE)
    @HystrixCommand(fallbackMethod = "defaultEventByMid")
    public List<Event> eventForUnitCertification(@RequestParam(value = "realm") String realm,
            @RequestParam(value = "deviceTypeId") Integer deviceTypeId,
            @RequestParam(value = "mid") String mid)
            throws Exception {
        try {
            return eventControl.getEventForCertificateUnits(realm, deviceTypeId, mid);
        } catch (Exception e) {
            Logger.getRootLogger().error(e.toString());
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
    public List<Event> defaultAllEventGeneric(String realm, RequestData requestData) {
        return Arrays.asList(new Event());
    }
    
    public List<Event> defaultEventByMid(String realm, Integer deviceTypeId, String mid) {
        return Arrays.asList(new Event());
    }

}
