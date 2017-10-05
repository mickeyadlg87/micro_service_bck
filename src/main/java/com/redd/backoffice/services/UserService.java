package com.redd.backoffice.services;

import cl.tastets.life.core.framework.exceptions.WebException;
import cl.tastets.life.objects.BasicEntity;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.redd.backoffice.controllers.crud.UserCRUDController;
import com.redd.backoffice.controllers.query.UserFindController;
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
@RequestMapping(value = "/backoffice/user", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "User Service")
public class UserService {
    
    @Autowired
    private UserFindController userController;
    
    @Autowired
    private UserCRUDController userCrudController;

    private static final Logger log = LoggerFactory.getLogger("USER");

    @RequestMapping(value = "/getUsersByRealm", method = RequestMethod.POST)
    @HystrixCommand(fallbackMethod = "defaultProfileByRealm")
    public List<BasicEntity> getAllProfileByRealm(@RequestParam(value = "realm") String realm, @RequestParam(value = "platform") String platform)
            throws Exception {
        try {
            return userController.getUsersByRealm(realm, platform);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new WebException("Error al obtener la lista de usuarios", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/getByUserName", method = RequestMethod.GET)
    @HystrixCommand(fallbackMethod = "defaultProfileByUsername")
    public BasicEntity getByUserName(@RequestParam(value = "realm") String realm, @RequestParam(value = "platform") String platform, @RequestParam(value = "userName") String userName)
            throws Exception {
        try {
            return userController.getUserByUserName(realm, platform, userName);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new WebException("Error al obtener el usuario", HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public BasicEntity save(@RequestBody BasicEntity user) {
        try {
            return userCrudController.insert(user.getString("realm"), user);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new WebException("Error al insertar usuario: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public BasicEntity update(@RequestBody BasicEntity user) {
        try {
            return userCrudController.update(user.getString("realm"), user);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new WebException("Error al actualizar usuario: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    public List<BasicEntity> defaultProfileByRealm(String realm, String platform) {
        return Arrays.asList(new BasicEntity());
    }
    
    public BasicEntity defaultProfileByUsername(String realm, String platform, String userName) {
        return new BasicEntity();
    }
    
}
