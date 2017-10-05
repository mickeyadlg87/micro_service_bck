package com.redd.backoffice.dao.queries;

/**
 *
 * @author fgodoy
 */
public class QueryReports {
    
    public static final String queryFacturationReportRsLite = "SELECT m.id as movilId ,p.nombre as plan, e.nombre as companyName, m.vin as vin, "
            + "case m.empresa_id_facturar when 0 then e.razon_social else fnGetRazonSocialFacturar(m.id) end as companyToBill, "
            + "case m.empresa_id_facturar when 0 then e.rut else fnGetRutFacturar(m.id) end as rutToBill, "
            + "m.nombre as movilName,  m.patente as plateNumber, g.imei, tg.nombre as deviceTypeName, m.fecha_ultima_actividad as lastActivityDate, m.ultimo_evento_fecha as lastEventDate, "
            + "g.fecha_creacion as gpsCreateDate, s.numero_telefono as simcard, m.id_tipo_venta_movil as saleTypeId, tvm.nombre as saleTypeName, fnEvaluateState(m.id) as movilState, "
            + "m.fecha_validado as FechaCertificacion, "
            + "s.proveedor_id, "
            + "spro.nombre as ProveedorSimcard "
            + "FROM movil m "
            + "INNER JOIN empresa e ON e.id= m.empresa_id "
            + "INNER JOIN plan p on p.id=e.plan_id "
            + "INNER JOIN gps_movil gm on m.id = gm.movil_id "
            + "INNER JOIN gps g on gm.gps_imei = g.imei "
            + "INNER JOIN tipo_dispositivo_gps tg on tg.id = g.tipo_dispositivo_gps_id "
            + "INNER JOIN tipo_venta_movil tvm on tvm.id = m.id_tipo_venta_movil "
            + "LEFT JOIN sim_card_gps sg on sg.gps_imei = g.imei "
            + "LEFT JOIN sim_card s on s.id = sg.sim_card_id "
            + "LEFT JOIN sim_proveedor spro on spro.id = s.proveedor_id "
            + "WHERE "
            + "m.validado= 1 "
            + "AND m.fecha_validado IS NOT NULL "
            + "AND m.fecha_baja IS NULL "
            + "ORDER BY e.id ";
    
    public static final String queryUnitByCompanyRsLite = "Select Count(e.id) as quantity, e.nombre as company "
            + "From rsLite3.movil m, rsLite3.empresa e "
            + "WHERE e.id = m.empresa_id "
            + "AND m.validado= 1 "
            + "AND m.fecha_validado IS NOT NULL "
            + "AND m.fecha_baja IS NULL "
            + "Group by e.id "
            + "Order by 1 desc ";
    
    public static final String queryUnitBySaleTypeRsLite = "Select Count(m.id_tipo_venta_movil) as quantity, tvm.nombre as saleTypeName "
            + "From rsLite3.movil m, rsLite3.tipo_venta_movil tvm "
            + "WHERE tvm.id = m.id_tipo_venta_movil "
            + "AND m.validado= 1 "
            + "AND m.fecha_validado IS NOT NULL "
            + "AND m.fecha_baja IS NULL "
            + "Group by m.id_tipo_venta_movil ";
    
    public static final String insertReport = "INSERT INTO `backoffice`.`reporte` "
            + "(`filename`, "
            + "`fecha_inicio`, "
            + "`id_detalle_tipo_reporte`, "
            + "`username`) "
            + "VALUES "
            + "(?, "
            + " ?, "
            + " ?, "
            + " ?);";
    
    public static final String updateReport = "UPDATE `backoffice`.`reporte` "
            + "SET "
            + "`fecha_fin` = ?, "
            + "`error` = ? "
            + "WHERE `id` = ?;";
    
    public static final String queryUnsubscribeReportRsLite = "SELECT e.nombre companyName, m.nombre unitName, m.patente plateNumber, m.vin, gm.gps_imei imei, s.numero_telefono simcard, "
            + "m.fecha_baja downDate, m.fecha_validado validateDate, m.ingresado_por registerBy "
            + "FROM movil m "
            + "INNER JOIN empresa e ON e.id= m.empresa_id "
            + "INNER JOIN plan p on p.id=e.plan_id "
            + "INNER JOIN gps_movil gm on m.id = gm.movil_id "
            + "INNER JOIN gps g on gm.gps_imei = g.imei "
            + "LEFT JOIN sim_card_gps sg on sg.gps_imei = g.imei "
            + "LEFT JOIN sim_card s on s.id = sg.sim_card_id "
            + "WHERE m.fecha_baja IS NOT NULL "
            + "and m.fecha_baja BETWEEN ? and ? "
            + "ORDER BY e.id, m.id;";
    
    public static final String queryCetifiedReport = "SELECT id_unidad unitId, id_usuario username, identificador_unico imei, fecha_accion actionDate, fecha_creacion_unidad creationUnitDate, fecha_ultima_actividad lastActivityDate, "
            + "nombre_tipo_dispositivo devicetypeName, patente plateNumber, nombre_movil unitName, fecha_validacion_dispositivo deviceValidationDate, vin, nombre_empresa companyName, "
            + "de.descripcion_estado TypeSale, "
            + "nombre_empresa_facturar companyNameToBill "
            + "FROM  backoffice.certificador_unidad,  backoffice.detalle_estado de, backoffice.estado_general e "
            + "WHERE realm = ? "
            + "AND de.id = id_tipo_venta "
            + "AND fecha_accion BETWEEN ? AND ? "
            + "AND e.nombre_codigo = 'TYPE_SALE' "
            + "AND e.id = de.id_estado_general "
            + "ORDER BY fecha_accion desc;";

    public static final String queryAgregateKeepAlive = "Select count(*) as countByCompany, tabla_keepalive.id_cliente as companyId, " +
            "tabla_keepalive.nombre_cliente as companyName, tabla_keepalive.rut_cliente as companyRut, " +
            "tabla_keepalive.tiene_keepalive as hasKeepAlive " +
            "FROM " +
            "(SELECT " +
            " case when abs(TIMESTAMPDIFF(DAY,CONVERT_TZ(now(),'+00:00','+03:00'),m.fecha_ultima_actividad)) > 30 then 0 " +
            " when abs(TIMESTAMPDIFF(DAY,CONVERT_TZ(now(),'+00:00','+03:00'),m.fecha_ultima_actividad)) <= 30 then 1 end as tiene_keepalive, " +
            " e.id as id_cliente, " +
            " e.rut as rut_cliente, " +
            " e.nombre as  nombre_cliente " +
            " FROM movil m " +
            " INNER JOIN empresa e ON e.id= m.empresa_id " +
            " INNER JOIN gps_movil gm on m.id = gm.movil_id " +
            " INNER JOIN gps g on gm.gps_imei = g.imei " +
            " INNER JOIN tipo_dispositivo_gps tg on tg.id= g.tipo_dispositivo_gps_id " +
            " LEFT JOIN sim_card_gps sg on sg.gps_imei = g.imei " +
            " LEFT JOIN sim_card s on s.id = sg.sim_card_id " +
            " LEFT JOIN sim_proveedor spro on spro.id = s.proveedor_id " +
            "Where m.empresa_id = e.id " +
            "and m.id = gm.movil_id " +
            "and m.validado = 1 " +
            "and m.fecha_validado is not null " +
            "and m.fecha_baja is null) as tabla_keepalive " +
            "GROUP BY tabla_keepalive.id_cliente, tabla_keepalive.tiene_keepalive ";
   
}
