package com.redd.backoffice.controllers.query;

import cl.tastets.life.objects.BasicEntity;
import cl.tastets.life.objects.RealmEnum;
import cl.tastets.life.objects.utils.RequestData;
import com.redd.backoffice.dao.impl.PotaDaoImpl;
import java.util.HashMap;
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
public class PotaController {
    
    @Autowired
    private PotaDaoImpl dao;
     
    public BasicEntity getCommandByDeviceType(String realm, Integer deviceTypeId, String codeTypeName) throws Exception {
        BasicEntity potaCommand = dao.findCommandByDeviceAndName(realm, deviceTypeId, codeTypeName);
        return potaCommand;
    }
    
    public BasicEntity getResponseCallbackById(String realm, Integer idPota) throws Exception {
        BasicEntity responseCallback = dao.findResponseCallbackById(realm, idPota);
        return responseCallback;
    }
    
    public List<BasicEntity> getCommandType(String realm) throws Exception {
        List<BasicEntity> response = dao.findCommandTypes(realm);
        return response;
    }
    
    public List<BasicEntity> getListCommandById(String realm, Integer deviceTypeId) throws Exception {
        return dao.findCommandListByDevType(realm, deviceTypeId);
    }
    
    public List<BasicEntity> getListCallbackByImei(String realm, String imei, RequestData request) {
        return dao.findListCallbackByImei(realm, imei, Optional.ofNullable(request.getPaginated()), Optional.ofNullable(request.getFilter()));
    }

    public BasicEntity sendPotaCommand(RealmEnum platform, String imei, Integer codeId, RequestData request) throws Exception {

        BasicEntity command = dao.findPotaCommandById("backoffice", codeId);
        command.put("potaCommandId", command.get("id"));
        command.put("platform", platform.toString());
        command.put("mid", imei);
        // si el comando tiene parametros configurables
        command = dao.setParametersForCommand(command, Optional.ofNullable(request.getPaginated()), Optional.ofNullable(request.getFilter()));
        // se crea el id serial del comando a enviar
        command.put("serialPotaMsg", dao.generatePotaMsgId("backoffice", command, "insertPotaMsg"));
        return dao.createPotaCommand(command);
    }
    
    public BasicEntity saveCallbackResponse(BasicEntity response, String platform) throws Exception {
        
        BasicEntity result = new BasicEntity();
        
        if (response.getString("status").equalsIgnoreCase("ok")) {
            // La respuesta de POTA fue exitosa
            result = dao.updateHistoryPotaMsg("backoffice", response, "updatePotaMsg");
        } else if (response.getString("status").equalsIgnoreCase("timeout")) {
            // La respuesta de POTA genero timeout (status=timeout)
            List<Map> scriptLines = (List) response.get("script_lines");
            for (Map line : scriptLines) {
                String oid = (String) (new HashMap((Map) line.get("id_line")).get("$oid"));
                result = dao.skipPotaCommand(oid, response.getString("imei"));
            }
            result.putAll(dao.updateHistoryPotaMsg("backoffice", response, "updatePotaMsg"));
        }
        return result;
    }

}
