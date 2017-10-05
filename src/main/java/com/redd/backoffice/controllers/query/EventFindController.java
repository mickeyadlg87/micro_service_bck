package com.redd.backoffice.controllers.query;

import cl.tastets.life.objects.BasicEntity;
import cl.tastets.life.objects.Event;
import cl.tastets.life.objects.utils.Paginated;
import cl.tastets.life.objects.utils.QueryFilter;
import cl.tastets.life.objects.utils.RequestData;
import com.redd.backoffice.dao.impl.DeviceDaoImpl;
import com.redd.backoffice.dao.impl.EventDaoImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author aleal
 */
@Service
public class EventFindController {

    @Autowired
    private EventDaoImpl eventDao;
    
    @Autowired
    private DeviceDaoImpl deviceDao;
    
    /**
     * trae todos los eventos disponibles en backoffice
     * @param realm
     * @param request
     * @return
     * @throws Exception 
     */
    public List<Event> findAllEventsByFilter(String realm, RequestData request) throws Exception {
        List<Event> response = eventDao.findEventsWithFilter(realm, Optional.ofNullable(request.getPaginated()), Optional.ofNullable(request.getFilter()));
        return response;
    }
    
    /**
     * inserta datos de la certificacion de la unidad
     * @param realm
     * @return 
     */
    public BasicEntity insertUnitCertified(String realm, BasicEntity unitCert) throws Exception{
        return eventDao.saveUnitCertified(realm, unitCert, "insertUnitCertified");
    }
    
    public BasicEntity updateUnitCertified(String realm, BasicEntity unitCert) throws Exception{
        return eventDao.updateUnitCertified(realm, unitCert, "updateUnitCertified");
    }
    
    /**
     * trae los eventos necesarios para certificar una unidad identificada por su modem id
     * @param realm
     * @param deviceTypeId
     * @param mid
     * @return
     * @throws Exception 
     */
    public List<Event> getEventForCertificateUnits(String realm, Integer deviceTypeId, String mid) throws Exception {
        List<Event> response = deviceDao.findEventsForCertification(realm, deviceTypeId, Optional.ofNullable(Paginated.from()), Optional.ofNullable(QueryFilter.from()));
        BasicEntity accesorios = eventDao.findAccesoryByMid(realm, mid);
        List<Integer> idSensores = new ArrayList<>();
        if (!accesorios.isEmpty()) {
            List<Map> instalados = accesorios.getList("installedAccesory");
            instalados.stream().forEach((m) -> {
                idSensores.add((Integer) m.get("accesoryId"));
            });
            if (!idSensores.isEmpty()) {
                Paginated p = Paginated.from();
                QueryFilter f = new QueryFilter().put("filter", Arrays.asList(new Event().put("accesoryListId", idSensores)));
                List<Event> eventosDeAccesorio = eventDao.findEventsByAccesories(realm, Optional.of(p), Optional.of(f));
                response.addAll(eventosDeAccesorio);
            }
        }
        return response;
    }

}
