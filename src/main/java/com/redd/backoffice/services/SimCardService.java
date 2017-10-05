package com.redd.backoffice.services;

import cl.tastets.life.core.framework.exceptions.WebException;
import cl.tastets.life.objects.backoffice.SimCard;
import cl.tastets.life.objects.utils.RequestData;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.redd.backoffice.controllers.query.SimCardFindController;
import com.redd.backoffice.utils.CrudOperationEnum;
import io.swagger.annotations.Api;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @author fgodoy
 */
@RestController
@RequestMapping(value = "/backoffice/simcard", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "SimCard Service")
public class SimCardService {
    
    @Autowired
    private SimCardFindController simcardControl;

    private static final Logger log = LoggerFactory.getLogger("SIMCARD");
    
    /**
     *
     * @param realm
     * @param requestData
     * @return
     */
    @RequestMapping(value = "/getAllSimCards", method = RequestMethod.POST)
    @HystrixCommand(fallbackMethod = "defaultAllTypeGeneric")
    public List<SimCard> getAllSimCardPost(@RequestParam(value = "realm") String realm, @RequestBody RequestData requestData){

        try {
            return simcardControl.getAllSimCard(realm, requestData);
        } catch (Exception e) {
            throw new WebException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * trae los diferentes proveedores de simcard
     *
     * @param realm
     * @return
     */
    @RequestMapping(value = "/getProviders", method = RequestMethod.POST)
    @HystrixCommand(fallbackMethod = "defaultProviders")
    public List<SimCard> getProvidersSim(@RequestParam(value = "realm") String realm) {

        try {
            return simcardControl.getProviders(realm);
        } catch (Exception e) {
            log.error("error al buscar proveedores ", e);
            throw new WebException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @param realm
     * @param codeIccid
     * @return
     */
    @RequestMapping(value = "/getSimCardByIccid", method = RequestMethod.POST)
    @HystrixCommand(fallbackMethod = "defaultByPhoneNumber")
    public SimCard getSimCardByIccidPost(@RequestParam(value = "realm") String realm, @RequestParam(value = "iccid") String codeIccid) {

        try {
            return simcardControl.getSimCardByIccidCode(realm, codeIccid);
        } catch (Exception e) {
            log.error("error al buscar sim por iccid ", e);
            throw new WebException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
        
    /**
     *
     * @param realm
     * @param phoneNumber
     * @return
     */
    @RequestMapping(value = "/getSimCardByPhoneNumber", method = RequestMethod.GET)
    @HystrixCommand(fallbackMethod = "defaultByPhoneNumber")
    public SimCard getSimCardByPhoneNumberPost(@RequestParam(value = "realm") String realm, @RequestParam(value = "phoneNumber") String phoneNumber) {

        try {
            return simcardControl.getSimByNumber(realm, phoneNumber);
        } catch (Exception e) {
            log.error("error al buscar sim por numero ", e);
            throw new WebException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Almacena SimCard
     *
     * @param simCard 
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public SimCard save(@RequestBody SimCard simCard) {
        try {
            return simcardControl.crud(simCard, CrudOperationEnum.SAVE);
        } catch (RuntimeException e) {
            throw new WebException("Error al guardar simcard" + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Servicio que actualiza SimCards
     * 
     * @param simcard
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public SimCard update(@RequestBody SimCard simcard){
        try{
            return simcardControl.crud(simcard, CrudOperationEnum.UPDATE);
        }catch (RuntimeException e){
            throw new WebException("Error al actualizar la simcard" + e.getMessage(), HttpStatus.BAD_REQUEST);
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
    public List<SimCard> defaultAllTypeGeneric(String realm, RequestData requestData) {
        return Arrays.asList(new SimCard());
    }

    public List<SimCard> defaultProviders(String realm) {
        return Arrays.asList(new SimCard());
    }
    
    public SimCard defaultByPhoneNumber(String realm, String id) {
        return new SimCard();
    }
}
