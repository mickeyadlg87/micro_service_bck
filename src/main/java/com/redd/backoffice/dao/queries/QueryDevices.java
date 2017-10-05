package com.redd.backoffice.dao.queries;

/**
 *
 * @author aleal
 */
public class QueryDevices {

    public static final String queryDeviceTypesBackoffice = "SELECT td.id, td.nombre as name, td.fabricante as producer, td.fecha_creacion as creationDate, (select count(distinct(t.id)) from tipo_dispositivo t) as total"
            + " FROM tipo_dispositivo td WHERE 1 = 1 {WHERE_CLAUSE}";

    public static final String queryEventForDeviceBackoffice = "select edh.id_evento as eventId, ev.nombre_evento as eventName from evento_dispositivo_habilitado edh, evento ev "
            + " where ev.id = edh.id_evento"
            + " {WHERE_CLAUSE}";
    
    public static final String queryEventForCertificateBackoffice = "select edc.id_evento as eventId, ev.nombre_evento as eventName from validacion_dispositivo_evento edc, evento ev "
            + " where ev.id = edc.id_evento"
            + " {WHERE_CLAUSE}";

    public static final String queryAccessoriesForDeviceBackoffice = "select sdh.id_tipo_sensor as accesoryId, ts.nombre as accesoryName from sensor_dispositivo_habilitado sdh, tipo_sensor ts "
            + " where ts.id = sdh.id_tipo_sensor"
            + " {WHERE_CLAUSE}";

    public static final String queryAccesoriesBackoffice = "select ts.id, ts.nombre as accesoryName, ts.variable as variable, "
            + "ts.fabricante as accesoryProducer, (select count(distinct(tse.id)) from tipo_sensor tse) as total "
            + "from tipo_sensor ts "
            + "where 1=1 {WHERE_CLAUSE}";

    public static final String insertEventForDeviceTypeBackoffice = "insert into evento_dispositivo_habilitado (id_evento,id_tipo_dispositivo) values (?,?)";

    public static final String deleteEventForDeviceTypeBackoffice = "delete from evento_dispositivo_habilitado where id_evento = ? and id_tipo_dispositivo = ?";
    
    public static final String insertEventForCertificateBackoffice = "insert into validacion_dispositivo_evento (id_evento,id_tipo_dispositivo) values (?,?)";

    public static final String deleteEventForCertificateBackoffice = "delete from validacion_dispositivo_evento where id_evento = ? and id_tipo_dispositivo = ?";

    public static final String insertAccesoryForDeviceTypeBackoffice = "insert into sensor_dispositivo_habilitado (id_tipo_sensor,id_tipo_dispositivo) values (?,?)";

    public static final String deleteAccesoryForDeviceTypeBackoffice = "delete from sensor_dispositivo_habilitado where id_tipo_sensor = ? and id_tipo_dispositivo = ?";

}
