package com.redd.backoffice.controllers.query;

import cl.tastets.life.objects.backoffice.SimCard;
import cl.tastets.life.objects.utils.Paginated;
import cl.tastets.life.objects.utils.QueryFilter;
import cl.tastets.life.objects.utils.RequestData;
import com.redd.backoffice.dao.impl.SimCardDaoImpl;
import com.redd.backoffice.utils.CrudOperationEnum;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author fgodoy
 */
@Service
public class SimCardFindController {

    @Autowired
    private SimCardDaoImpl dao;
        
    public SimCard getSimByNumber(String realm, String simCardNumber) {
        return dao.findSimCardByPhoneNumber(realm, simCardNumber, Optional.ofNullable(Paginated.from()), Optional.ofNullable(QueryFilter.from()));
    }
    
    public List<SimCard> getAllSimCard(String realm, RequestData reqDat) {
        return dao.getSimCardListByFilter(realm, Optional.ofNullable(reqDat.getPaginated()), Optional.ofNullable(reqDat.getFilter()));
    }

    public List<SimCard> getProviders(String realm) {
        return dao.getProvidersByRealm(realm);
    }

    public SimCard getSimCardByIccidCode(String realm, String codeIccid) {
        return dao.findSimCardByIccid(realm, codeIccid, Optional.ofNullable(Paginated.from()), Optional.ofNullable(QueryFilter.from()));
    }
    
    public SimCard crud(SimCard simcard, CrudOperationEnum op) {

        try {
            switch (op) {
                case SAVE:
                    simcard = dao.save(simcard.getString("realm"), simcard, "insertSimCard");
                    break;
                case UPDATE:
                    simcard = dao.update(simcard.getString("realm"), simcard, "updatesimCard");
                    break;
                case DELETE:
                    break;
            }
            return simcard;

        } catch (Exception ex) {
            throw new RuntimeException("Error en crud de simcard" + ex.getMessage(), ex);
        }
    }
    
}
