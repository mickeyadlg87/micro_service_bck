
package com.redd.backoffice.dao.queries;

/**
 *
 * @author aleal
 */
public class QueryPota {
    
    public static final String queryComandosPotaBackoffice = "select cp.id, cp.comando_a_enviar as command, cp.descripcion_comando as commandType, "
            + "cp.protocolo as protocol, cp.tipo_respuesta as responseType, cp.id_tipo_dispositivo as deviceTypeId, cp.titulo_perfil as profileTitle from comando_pota cp "
            + "where 1=1 {WHERE_CLAUSE}";
    
    public static final String queryHistorialPotaBackoffice = "select hcp.id_msg as idPotaMsg, hcp.identificador_unico_dispositivo as mid, hcp.id_comando_pota as idPotaCommand, hcp.fecha_ingreso as sendDate, "
            + "hcp.fecha_respuesta as responseDate, hcp.plataforma as platform, hcp.msg_status as status, hcp.msg_lineas_script as callbackResponse from historial_comando_pota hcp "
            + "where 1=1 {WHERE_CLAUSE}";
    
    public static final String queryTiposComandosPotaBackoffice = "select distinct cp.descripcion_comando as commandType from comando_pota cp";
    
    public static final String insertHistoryPotaBackoffice = "INSERT INTO historial_comando_pota "
            + "(id_tipo_dispositivo,"
            + " id_comando_pota,"
            + " identificador_unico_dispositivo,"
            + " fecha_ingreso,"
            + " plataforma)"
            + " VALUES "
            + "(?,?,?,?,?)";
    
    public static final String updateHistoryPotaBackoffice = "UPDATE historial_comando_pota "
            + " SET "
            + " fecha_respuesta  = ?,"
            + " msg_status = ?,"
            + " msg_lineas_script = ? "
            + " WHERE id_msg = ? ";

}
