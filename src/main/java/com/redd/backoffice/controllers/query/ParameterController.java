package com.redd.backoffice.controllers.query;

import cl.tastets.life.objects.Parameter;
import com.redd.backoffice.dao.impl.ParameterDaoImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author fgodoy
 */
@Service
public class ParameterController {
    
    @Autowired
    private ParameterDaoImpl dao;
    
    public List<Parameter> findGeneralState(String realm, String codeName) throws Exception {
        List<Parameter> response = dao.findStateByCodeName(realm, codeName);
        return response;
    }
    
}
