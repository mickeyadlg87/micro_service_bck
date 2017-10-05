package com.redd.backoffice.controllers.crud;

import cl.tastets.life.objects.BasicEntity;
import com.redd.backoffice.dao.impl.ReportDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author aleal
 */
@Service
public class ReportCRUDController {
    
    @Autowired
    private ReportDaoImpl dao;
    
    public BasicEntity insertReport(String realm, BasicEntity report) throws Exception {
        return dao.saveReport(realm, report, "insertReport");
    }

    public BasicEntity updateReport(String realm, BasicEntity report) throws Exception {
        return dao.updateReport(realm, report, "updateReport");
    }

}
