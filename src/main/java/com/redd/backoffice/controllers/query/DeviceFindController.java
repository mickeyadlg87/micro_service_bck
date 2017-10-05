package com.redd.backoffice.controllers.query;

import cl.tastets.life.objects.BasicEntity;
import cl.tastets.life.objects.Event;
import cl.tastets.life.objects.Parameter;
import cl.tastets.life.objects.backoffice.DeviceType;
import cl.tastets.life.objects.utils.RequestData;
import com.redd.backoffice.dao.impl.AccesoryDaoImpl;
import com.redd.backoffice.dao.impl.DeviceDaoImpl;
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
public class DeviceFindController {

    @Autowired
    private DeviceDaoImpl deviceDao;
    
    @Autowired
    private AccesoryDaoImpl accesoryDao;
    
    /**
     * trae todos los tipos de dispositivo disponibles
     * @param realm
     * @param reqData
     * @return 
     */
    public List<DeviceType> getAllDeviceType(String realm, RequestData reqData) {
        return deviceDao.findDeviceTypeWithFilter(realm, Optional.ofNullable(reqData.getPaginated()), Optional.ofNullable(reqData.getFilter()));
    }
    /**
     * trae los eventos habilitados para cada tipo de dispositivo
     * @param realm
     * @param devTypeId
     * @param reqDat
     * @return 
     */
    public List<Event> getEventsForDeviceType(String realm, Integer devTypeId, RequestData reqDat) {
        return deviceDao.findEventsByTypeId(realm, devTypeId, Optional.ofNullable(reqDat.getPaginated()), Optional.ofNullable(reqDat.getFilter()));
    }
    /**
     * trae los eventos para certificar dispositivos (y unidades) a traves del tipo de dispositivo
     * @param realm
     * @param devTypeId
     * @param reqDat
     * @return 
     */
    public List<Event> getCertificateEventsForDevType(String realm, Integer devTypeId, RequestData reqDat) {
        return deviceDao.findEventsForCertification(realm, devTypeId, Optional.ofNullable(reqDat.getPaginated()), Optional.ofNullable(reqDat.getFilter()));
    }
    /**
     * trae todos los accesorios (sensores) disponibles
     * @param realm
     * @param reqData
     * @return 
     */
    public List<Parameter> getAllAccesoriesWithFilter(String realm, RequestData reqData) {
        return accesoryDao.findAccesoriesWithFilter(realm, Optional.ofNullable(reqData.getPaginated()), Optional.ofNullable(reqData.getFilter()));
    }
    /**
     * trae todos los accesorios disponibles por tipo de dispositivo
     * @param realm
     * @param devTypeId
     * @param reqDat
     * @return 
     */
    public List<Parameter> getAccesoriesByDeviceType(String realm, Integer devTypeId, RequestData reqDat) {
        return accesoryDao.findAccesoriesByDevType(realm, devTypeId, Optional.ofNullable(reqDat.getPaginated()), Optional.ofNullable(reqDat.getFilter()));
    }
    
    /**
     * Administra las relaciones eventos por tipo de dispositivo, ya sea para
     * eventos normales o eventos para certificacion
     * @param eventXdevice
     * @param op
     * @return 
     */
    public BasicEntity crudEventForDevType(BasicEntity eventXdevice, CrudOperationEnum op) {

        try {
            switch (op) {
                case SAVE:
                    eventXdevice = deviceDao.saveEventFortype(eventXdevice.getString("realm"), eventXdevice, eventXdevice.containsKey("certificateEvent") ? "insertEventForCertificate" : "insertEventForDeviceTypes");
                    break;
                case DELETE:
                    eventXdevice = deviceDao.deleteEventFortype(eventXdevice.getString("realm"), eventXdevice, eventXdevice.containsKey("certificateEvent") ? "deleteEventForCertificate" : "deleteEventForDeviceTypes");
                    break;
            }
            return eventXdevice;

        } catch (Exception ex) {
            throw new RuntimeException("Error en crud de eventos por tipo de dispositivo, Operacion " + op + ", " + ex.getMessage(), ex);
        }
    }
    
    /**
     * Administra las relaciones accesorios por tipo de dispositivo
     * @param accesoryXdevice
     * @param op
     * @return 
     */
    public BasicEntity crudAccesoriesForDevType(BasicEntity accesoryXdevice, CrudOperationEnum op) {

        try {
            switch (op) {
                case SAVE:
                    accesoryXdevice = accesoryDao.saveAccesoryForType(accesoryXdevice.getString("realm"), accesoryXdevice, "insertAccesoryForDeviceTypes");
                    break;
                case DELETE:
                    accesoryXdevice = accesoryDao.deleteAccesoryForType(accesoryXdevice.getString("realm"), accesoryXdevice, "deleteAccesoryForDeviceTypes");
                    break;
            }
            return accesoryXdevice;

        } catch (Exception ex) {
            throw new RuntimeException("Error en crud de accesorios por tipo de dispositivo, Operacion " + op + ", " + ex.getMessage(), ex);
        }
    }

}
