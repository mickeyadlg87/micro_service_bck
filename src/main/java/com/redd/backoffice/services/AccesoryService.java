package com.redd.backoffice.services;

import cl.tastets.life.core.framework.exceptions.WebException;
import cl.tastets.life.objects.BasicEntity;
import cl.tastets.life.objects.utils.RequestData;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.redd.backoffice.controllers.crud.InstalledAccesoriesCRUDController;
import com.redd.backoffice.controllers.query.InstalledAccesoriesController;
import io.swagger.annotations.Api;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 *
 * @author aleal
 */
@RestController
@RequestMapping(value = "/backoffice/accesory", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "Accesory Service")
public class AccesoryService {
    
    @Autowired
    private InstalledAccesoriesCRUDController accesoryCrudController;
    
    @Autowired
    private InstalledAccesoriesController accesoryController;
    
    @RequestMapping(value = "/saveByDevice", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BasicEntity save(@RequestBody BasicEntity accesoriesByDevice) {
        try {
            return accesoryCrudController.insertAccesoriesByDev(accesoriesByDevice.getString("realm"), accesoriesByDevice);
        } catch (Exception e) {
            throw new WebException("Error al guardar accesorio por dispositivo", HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "/updateByDevice", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BasicEntity update(@RequestBody BasicEntity accesoriesByDevice) {
        try {
            return accesoryCrudController.updateAccesoriesByDev(accesoriesByDevice.getString("realm"), accesoriesByDevice);
        } catch (Exception e) {
            throw new WebException("Error al actualizar accesorio por dispositivo", HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "/getInstalledByMid", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE)
    @HystrixCommand(fallbackMethod = "defaultAccesories")
    public BasicEntity getAccesoriesByImei(@RequestParam(value = "realm") String realm, @RequestParam(value = "mid") String mid)
            throws Exception {
        try {
            return accesoryController.getAccesoriesByModemId(realm, mid);
        } catch (Exception e) {
            throw new WebException("Error al buscar accesorios del movil", HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "/getAllInstalled", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultAccesoriesInstalled")
    public List<BasicEntity> allInstalled(@RequestParam(value = "realm") String realm,
            @RequestBody RequestData requestData) throws Exception {
        try {
            return accesoryController.getAllListAccesories(realm, requestData);
        } catch (Exception e) {
            throw new WebException("Error al buscar accesorios instalados", HttpStatus.BAD_REQUEST);
        }
    }
    
    // FALLBACK METHODS
    
    public BasicEntity defaultAccesories(String realm, String mid) {
        return new BasicEntity();
    }
    
    public List<BasicEntity> defaultAccesoriesInstalled(String realm, RequestData requestData) {
        return Arrays.asList(new BasicEntity());
    }

}
