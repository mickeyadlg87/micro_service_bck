package com.redd.backoffice.controllers.crud;

import cl.tastets.life.objects.BasicEntity;
import com.redd.backoffice.dao.impl.UserDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author fgodoy
 */
@Service
public class UserCRUDController {
    
    @Autowired
    private UserDaoImpl dao;
    
    public BasicEntity insert(String realm, BasicEntity user) throws Exception {
        return dao.save(realm, user, "insert");
    }
    
    public BasicEntity update(String realm, BasicEntity user) throws Exception {
        return dao.update(realm, user, "update");
    }
    
}
