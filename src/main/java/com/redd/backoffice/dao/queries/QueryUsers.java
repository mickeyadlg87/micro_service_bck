package com.redd.backoffice.dao.queries;

/**
 *
 * @author fgodoy
 */
public class QueryUsers {
    
    public static final String queryGetUsersByRealm = "SELECT id, realm, device, username userName, nombre name, apellido lastName, rut, cliente_id clientId, id_paquete_funcionalidad functionalityId " 
            + "FROM usuario "
            + "WHERE realm = ?;";
    
    public static final String queryUserByUsernameRealm = "SELECT id, realm, device, nombre name, apellido lastName, rut, cliente_id clientId, id_paquete_funcionalidad functionalityId "
            + "FROM usuario "
            + "WHERE realm = ? "
            + "AND username = ?;";

    public static final String insert = "INSERT INTO usuario "
            + "(realm, "
            + "device, "
            + "username, "
            + "nombre, "
            + "apellido, "
            + "rut, "
            + "cliente_id, "
            + "id_paquete_funcionalidad) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
    public static final String update = "UPDATE usuario SET "
            + "realm=?, "
            + "device=?, "
            + "username=?, "
            + "nombre=?, "
            + "apellido=?, "
            + "rut=?, "
            + "cliente_id=?, "
            + "id_paquete_funcionalidad=? "
            + "WHERE id = ? ";
    
}
