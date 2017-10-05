package com.redd.backoffice.services;

import cl.tastets.life.core.framework.exceptions.WebException;
import cl.tastets.life.objects.Parameter;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.redd.backoffice.controllers.query.ParameterController;
import io.swagger.annotations.Api;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author fgodoy
 */
@RestController
@RequestMapping(value = "/backoffice/parameter", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "Parameter Service")
public class ParameterService {
    
    @Autowired
    private ParameterController paramControl;
    private static final Logger LOG = LoggerFactory.getLogger("paramService");
    
    /**
     * 
     * @param realm
     * @param codeName
     * @return 
     */
    @RequestMapping(value = "/getStatesByCodeName", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultByCodeGeneric")
    public List<Parameter> getParameterByCodeName(@RequestParam(value = "realm") String realm, @RequestParam(value = "codeName") String codeName) {
        try {
            return paramControl.findGeneralState(realm, codeName);
        } catch (Exception e) {
            LOG.error("Error al obtener la lista de estados generales", e);
            throw new WebException("Error al obtener la lista de estados generales", HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * FALLBACK METHODS
     * @param realm
     * @param codeName
     * @return 
     */
    public List<Parameter> defaultByCodeGeneric(String realm, String codeName) {
        return Arrays.asList(new Parameter());
    }
}
