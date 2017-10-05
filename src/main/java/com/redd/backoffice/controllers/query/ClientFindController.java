package com.redd.backoffice.controllers.query;

import cl.tastets.life.objects.Company;
import cl.tastets.life.objects.utils.Paginated;
import cl.tastets.life.objects.utils.QueryFilter;
import cl.tastets.life.objects.utils.RequestData;
import com.redd.backoffice.dao.impl.BackoffClientDaoImpl;
import com.redd.backoffice.utils.CrudOperationEnum;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author aleal
 */
@Service
public class ClientFindController {

    @Autowired
    private BackoffClientDaoImpl dao;

    public Company findCustomerById(String realm, Integer id) {
        return dao.findClientById(realm, id, Optional.ofNullable(Paginated.from()), Optional.ofNullable(QueryFilter.from()));
    }
    
    public Company findCustomerByExternalIdAndPlataform(String plataform, Integer externalId) {
        return dao.findClientByExternalIdAndPlataform(plataform, externalId, Optional.ofNullable(Paginated.from()), Optional.ofNullable(QueryFilter.from()));
    }
    
    public List<Company> findFunctionalitiesPackages(String realm, RequestData reqData) {
        return dao.findFunctionalitiesPackages(realm, Optional.ofNullable(reqData.getPaginated()), Optional.ofNullable(reqData.getFilter()));
    }
    
    /**
     * Servicio para hacer crud de empresas
     * @param company
     * @param op
     * @return 
     */
    public Company crud(Company company, CrudOperationEnum op) {

        try {
            switch (op) {
                case SAVE:
                    company = dao.save(company.getString("realm"), company, "insertCustomer");
                    break;
                case UPDATE:
                    company = dao.update(company.getString("realm"), company, "updateCustomer");
                    break;
                case DELETE:
                    break;
            }
            return company;

        } catch (Exception ex) {
            throw new RuntimeException("Error en crud de companies" + ex.getMessage(), ex);
        }
    }
    
    /**
     * Servicio para hacer crud de paquetes funcionales
     * @param company
     * @param op
     * @return 
     */
    public Company crudFunPackage(Company company, CrudOperationEnum op) {

        try {
            switch (op) {
                case SAVE:
                    company = dao.saveFunPackage(company.getString("realm"), company, "insertFunPackage");
                    break;
                case UPDATE:
                    company = dao.updateFunPackage(company.getString("realm"), company, "updateFunPackage");
                    break;
                case DELETE:
                    break;
            }
            return company;

        } catch (Exception ex) {
            throw new RuntimeException("Error en crud de paquetes funcionales" + ex.getMessage(), ex);
        }
    }

}
