package com.redd.backoffice.services;

import cl.tastets.life.core.framework.exceptions.WebException;
import cl.tastets.life.objects.Company;
import cl.tastets.life.objects.utils.RequestData;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.redd.backoffice.controllers.query.ClientFindController;
import com.redd.backoffice.utils.CrudOperationEnum;
import io.swagger.annotations.Api;
import java.util.Arrays;
import java.util.List;
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
 * Controller asociado a la informacion de clientes en backoffice
 *
 * @author aleal
 */
@RestController
@RequestMapping(value = "/backoffice/customer", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "Customer Service")
public class ClientService {
    
    @Autowired
    private ClientFindController customerControl;

    /**
     * Busqueda de clientes por Id en base de datos de backoffice
     *
     * @param realm
     * @param customerId
     * @return
     */
    @RequestMapping(value = "/getById", method = RequestMethod.POST)
    @HystrixCommand(fallbackMethod = "defaultByIdGeneric")
    public Company getClientByIdPost(@RequestParam(value = "realm") String realm, @RequestParam(value = "customerId") Integer customerId) {

        try {
            return customerControl.findCustomerById(realm, customerId);
        } catch (Exception e) {
            throw new WebException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
    
    /**
     * Busqueda de clientes por id externo y plataforma a la cual pertenece
     * 
     * @param plataform
     * @param externalCustomerId
     * @return 
     */
    @RequestMapping(value = "/getByExternalId", method = RequestMethod.POST)
    @HystrixCommand(fallbackMethod = "defaultByIdGeneric")
    public Company getCustomerByExternalIdPost(@RequestParam(value = "plataform") String plataform, @RequestParam(value = "externalCustomerId") Integer externalCustomerId) {

        try {
            return customerControl.findCustomerByExternalIdAndPlataform(plataform, externalCustomerId);
        } catch (Exception e) {
            throw new WebException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
    
    /**
     * retorna los paquetes funcionales disponibles
     * @param realm
     * @param requestData
     * @return 
     */
    @RequestMapping(value = "/package/getPackageFunctionality", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultByRequest")
    public List<Company> getFunctionalitiesPack(@RequestParam(value = "realm") String realm,
            @RequestBody RequestData requestData) {

        try {
            return customerControl.findFunctionalitiesPackages(realm, requestData);
        } catch (Exception e) {
            throw new WebException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Servicio que guarda clientes
     *
     * @param company cliente a guardar
     * @return cliente guardada
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Company save(@RequestBody Company company) {
        try {
            return customerControl.crud(company, CrudOperationEnum.SAVE);
        } catch (RuntimeException e) {
            throw new WebException("Error al guardar empresa " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Servicio que actualiza clientes
     *
     * @param company
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Company update(@RequestBody Company company) {
        try {
            return customerControl.crud(company, CrudOperationEnum.UPDATE);
        } catch (RuntimeException e) {
            throw new WebException("Error al actualizar empresa " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    
    /**
     * Servicio que guarda paquetes funcionales
     *
     * @param company estructura del paquete funcional a guardar
     * @return cliente guardada
     */
    @RequestMapping(value = "/package/save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Company saveFunctionalPackage(@RequestBody Company company) {
        try {
            return customerControl.crudFunPackage(company, CrudOperationEnum.SAVE);
        } catch (RuntimeException e) {
            throw new WebException("Error al guardar paquete funcional " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Servicio que actualiza paquetes funcionales
     *
     * @param company
     * @return
     */
    @RequestMapping(value = "/package/update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Company updateFunctionalPackage(@RequestBody Company company) {
        try {
            return customerControl.crudFunPackage(company, CrudOperationEnum.UPDATE);
        } catch (RuntimeException e) {
            throw new WebException("Error al actualizar paquete funcional " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * FALLBACK METHODS
     */
    /**
     *
     * @param realm
     * @param id
     * @return
     */
    public Company defaultByIdGeneric(String realm, Integer id) {
        return new Company();
    }
    
    public List<Company> defaultByRequest(String realm, RequestData requestData) {
        return Arrays.asList(new Company());
    }

}
