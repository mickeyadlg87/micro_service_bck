package com.redd.backoffice.dao.queries;

/**
 *
 * @author fgodoy
 */
public class QueryParameter {
    
    public static final String queryGetGeneralState = "Select de.id_estado_general as generalState, e.nombre as generalStateName, de.descripcion_estado as descriptionState, de.id as descriptionId "
            + "From backoffice.detalle_estado de, backoffice.estado_general e "
            + "Where e.nombre_codigo = ? "
            + "and e.id = de.id_estado_general;";
    
}
