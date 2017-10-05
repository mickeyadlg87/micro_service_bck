package com.redd.backoffice.dao.queries;

/**
 *
 * @author aleal
 */
public class QueryAccesoryEvents {

    public static final String queryEventsBackoffice = "select ev.id, ev.nombre_evento as eventName, ev.fecha_homologacion as approvalDate,"
            + " ev.visible_cliente as isVisible, ev.valido_logica as valueForLogic, ev.fecha_deshabilitacion as disableDate,  (select count(distinct(e.id)) from evento e) as total"
            + " from evento ev"
            + " where 1=1 {WHERE_CLAUSE}";
    
    public static final String queryEventCertUnitBackoffice = "select ev.id as eventId, ev.nombre_evento as eventName, ac.nombre as accesoryName "
            + "from evento ev, evento_tipo_sensor evts, tipo_sensor ac "
            + "where ev.id = evts.id_evento "
            + "and ac.id = evts.id_tipo_sensor {WHERE_CLAUSE}";
    
    public static final String queryAccesoriesInstalledByDeviceBackoffice = "select cd.id_usuario as username, cd.identificador_unico as imei, "
            + " cd.fecha_accion as validationDate, cd.accesorios_instalados as installedAccesory "
            + " from certificador_dispositivo cd where 1=1 {WHERE_CLAUSE}";
    
    public static final String insertAccesoriesByDeviceBackoffice = "INSERT INTO certificador_dispositivo "
            + " (id_usuario,"
            + " identificador_unico,"
            + " fecha_accion,"
            + " accesorios_instalados)"
            + " VALUES"
            + " (?,"
            + " ?,"
            + " ?,"
            + " ?)";
    
    public static final String updateAccesoriesByDeviceBackoffice = "UPDATE certificador_dispositivo "
            + " SET "
            + " id_usuario = ?,"
            + " fecha_accion = ?, "
            + " accesorios_instalados = ? "
            + " WHERE identificador_unico = ? ";
    
    public static final String insertUnitCertifiedBackOffice = "INSERT INTO certificador_unidad "
            + " (realm,"
            + " id_unidad,"
            + " id_usuario,"
            + " identificador_unico,"
            + " fecha_accion,"
            + " nota,"
            + " fecha_creacion_unidad,"
            + " fecha_ultima_actividad,"
            + " id_tipo_dispisitivo,"
            + " nombre_tipo_dispositivo,"
            + " patente,"
            + " nombre_movil,"
            + " vin,"
            + " nombre_empresa,"
            + " id_empresa,"
            + " fecha_validacion_dispositivo,"
            + " id_tipo_venta,"
            + " id_empresa_facturar, "
            + " nombre_empresa_facturar )"
            + " VALUES"
            + " (?,"
            + " ?,"
            + " ?,"
            + " ?,"
            + " ?,"
            + " ?,"
            + " ?,"
            + " ?,"
            + " ?,"
            + " ?,"
            + " ?,"
            + " ?,"
            + " ?,"
            + " ?,"
            + " ?,"
            + " ?,"
            + " ?,"
            + " ?,"
            + " ?)";
    
    public static final String updateUnitCertifiedBackOffice = "UPDATE certificador_unidad "
            + " SET "
            + " id_usuario = ?,"
            + " identificador_unico = ?,"
            + " fecha_accion = ?, "
            + " nota = ?, "
            + " fecha_creacion_unidad = ?, "
            + " fecha_ultima_actividad = ?, "
            + " id_tipo_dispisitivo = ?, "
            + " nombre_tipo_dispositivo = ?, "
            + " patente = ?, "
            + " nombre_movil = ?, "
            + " vin = ?, "
            + " nombre_empresa = ?, "
            + " id_empresa = ?, "
            + " fecha_validacion_dispositivo = ?, "
            + " id_tipo_venta = ?, "
            + " id_empresa_facturar = ?, "
            + " nombre_empresa_facturar = ?, "
            + " WHERE realm = ? "
            + " AND id_unidad = ? ";

}
