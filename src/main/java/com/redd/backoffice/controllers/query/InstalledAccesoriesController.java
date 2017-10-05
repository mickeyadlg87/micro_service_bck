package com.redd.backoffice.controllers.query;

import cl.tastets.life.objects.BasicEntity;
import cl.tastets.life.objects.utils.RequestData;
import com.redd.backoffice.dao.impl.EventDaoImpl;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author aleal
 */
@Service
public class InstalledAccesoriesController {

    @Autowired
    private EventDaoImpl dao;

    /**
     * 
     * @param realm
     * @param imei
     * @return
     * @throws Exception 
     */
    public BasicEntity getAccesoriesByModemId(String realm, String imei) throws Exception {
        BasicEntity accesories = dao.findAccesoryByMid(realm, imei);
        return accesories;
    }
    
    /**
     * 
     * @param realm
     * @param request
     * @return
     * @throws Exception 
     */
    public List<BasicEntity> getAllListAccesories(String realm, RequestData request) throws Exception {
        List<BasicEntity> response = dao.findAccesoriesInstalled(realm, Optional.ofNullable(request.getPaginated()), Optional.ofNullable(request.getFilter()));
        return response;
    }

}
