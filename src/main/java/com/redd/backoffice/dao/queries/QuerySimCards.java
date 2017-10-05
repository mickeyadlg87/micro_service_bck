package com.redd.backoffice.dao.queries;

/**
 *
 * @author fgodoy
 */
public class QuerySimCards {

    public static final String queryAllSimCardBackoffice = "SELECT sc.id, sc.numero_telefono as phoneNumber, sc.fecha_creacion as creationDate, sc.numero_iccid as iccid, "
            + "sc.id_usuario as username, sc.fecha_baja as downDate, (select count(distinct(s.id)) from sim_card s) as total "
            + "from sim_card sc "
            + "where 1 = 1 {WHERE_CLAUSE}";

    public static final String queryProvidersSimCardBackoffice = "SELECT p.id, p.nombre as name, p.nombre as alias "
            + "from proveedor_simcard p "
            + "where 1 = 1 ";

    public static final String insertSimCardBackoffice = "INSERT INTO sim_card "
            + "(numero_telefono, "
            + "numero_iccid, "
            + "id_usuario, "
            + "fecha_creacion) "
            + "VALUES (?, ?, ?, now())";

    public static final String updateSimCarBackoffice = "UPDATE sim_card SET "
            + "numero_telefono=?, "
            + "fecha_baja=?, "
            + "numero_iccid=?, "
            + "id_usuario=? "
            + "WHERE id = ? ";
    
}
