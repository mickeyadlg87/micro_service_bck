/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redd.backoffice.controllers.query;

import cl.tastets.life.objects.BasicEntity;
import com.redd.backoffice.dao.impl.UserDaoImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author fgodoy
 */
@Service
public class UserFindController {
    
    @Autowired
    private UserDaoImpl dao;
    
    public List<BasicEntity> getUsersByRealm(String realm, String platform) throws Exception {
        List<BasicEntity> response = dao.findUserByRealm(realm, platform);
        return response;
    }
    
    public  BasicEntity getUserByUserName(String realm, String platform, String userName) throws Exception {
        BasicEntity responase = dao.findUserByUserName(realm, platform, userName);
        return responase;
    }
    
}
