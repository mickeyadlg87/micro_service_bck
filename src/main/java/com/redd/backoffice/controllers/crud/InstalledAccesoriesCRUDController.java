package com.redd.backoffice.controllers.crud;

import cl.tastets.life.objects.BasicEntity;
import com.redd.backoffice.dao.impl.EventDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author aleal
 */
@Service
public class InstalledAccesoriesCRUDController {
    
    @Autowired
    private EventDaoImpl dao;
    
    public BasicEntity insertAccesoriesByDev(String realm, BasicEntity accesories) throws Exception{
        return dao.saveAccesoriesByDev(realm, accesories, "insertAccesoriesByDevice");
    }
    
    public BasicEntity updateAccesoriesByDev(String realm, BasicEntity accesories) throws Exception{
        return dao.updateAccesoriesByDev(realm, accesories, "updateAccesoriesByDevice");
    }
    
}
