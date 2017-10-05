package com.redd.backoffice.dao.queries;

/**
 *
 * @author aleal
 */
public class QueryClient {

    /**
     * queries implementacion criteria
     */
    public static final String queryClientBackoffice = "SELECT e.id,e.nombre AS customerName,e.rut,e.razon_social AS businessName,e.giro,e.mercado AS market, "
            + "e.segmento AS segment,e.pagina_web AS webPage, e.direccion AS address, e.id_empresa AS customerExternalId, e.ejecutivo AS executive, e.tipo_ejecutivo AS executiveType, e.telefono_ejecutivo AS executivePhone, "
            + "e.anexo_ejecutivo AS executiveAnnex, e.mail_ejecutivo AS executiveMail, e.contacto_comercial_nombre AS tradeContactName, e.contacto_comercial_telefono_uno AS tradeContactPhone, "
            + "e.contacto_comercial_telefono_dos AS tradeContactPhoneTwo, e.contacto_comercial_mail AS tradeContactMail, e.proveedor_contacto_nombre AS supplierName, e.proveedor_contacto_telefono_uno AS supplierPhone, "
            + "e.proveedor_contacto_telefono_dos AS supplierPhoneTwo, e.proveedor_contacto_mail AS supplierMail, e.condicion_pago AS paymentCondition, e.plataforma AS platform, "
            + "e.modalidad AS modality, e.plazo_contrato AS termContract, e.tarifa_prepago AS prepaidRate, e.tarifa_monitoreo AS searchingRate, "
            + "e.tarifa_arriendo AS rentRate, e.tipo_plan AS planType, e.fecha_contrato AS contractDate, e.equipo_instalado AS installedKit, "
            + "e.q_equipos AS quantityKit, e.accesorios AS accessory, e.administrador_plataforma_usuario AS platformManagerUser, e.administrador_plataforma_telefono AS platformManagerPhone, e.administrador_plataforma_mail AS platformManagerMail, "
            + "e.contacto_comercial_cargo  AS supplierJobTitle, e.proveedor_requiere_oc  AS oc, e.proveedor_liberacion_oc  AS supplierLiberationOC, "
            + "e.proveedor_cargo  AS tradeContactJobTitle, e.tarifa_venta AS saleRate, e.administrador_plataforma_telefono_secundario  AS platformManagerSecondaryPhone, "
            + "e.administrador_plataforma_cargo  AS platformManagerJobTitle, e.observaciones AS observation, "
            + "(select count(distinct(e.id)) from cliente e) as total "
            + "FROM cliente e "
            + "WHERE 1 = 1 "
            + "{WHERE_CLAUSE}";

    public static final String queryTypeSaleByClientBackoffice = "select tv.descripcion_estado as typeSaleDesc, ctv.id_detalle_estado_tipo_venta as typeSaleId, ctv.tarifa as rate " +
            "from cliente_tipo_venta ctv, detalle_estado tv, estado_general eg " +
            "where  eg.id=tv.id_estado_general " +
            "and tv.id = ctv.id_detalle_estado_tipo_venta " +
            "and eg.nombre_codigo = ? " +
            "and ctv.id_cliente = ? ";

    public static final String insertTypeSaleByCustomerBackoffice = "insert into cliente_tipo_venta (id_cliente,id_detalle_estado_tipo_venta,tarifa) " +
            "values (?,?,?)";

    public static final String updateTypeSaleByCustomerBackoffice = "update cliente_tipo_venta set tarifa=? where id_cliente=? and id_detalle_estado_tipo_venta=?";

    public static final String deleteTypeSaleByCustomerBackoffice = "delete from cliente_tipo_venta where id_cliente=? and id_detalle_estado_tipo_venta=?";

    public static final String insertClientBackoffice = "INSERT INTO cliente "
            + "(nombre, "
            + "rut, "
            + "razon_social, "
            + "giro, "
            + "mercado, "
            + "segmento, "
            + "pagina_web, "
            + "direccion, "
            + "id_empresa, "
            + "ejecutivo, "
            + "tipo_ejecutivo, "
            + "telefono_ejecutivo, "
            + "anexo_ejecutivo, "
            + "mail_ejecutivo, "
            + "contacto_comercial_nombre, "
            + "contacto_comercial_telefono_uno, "
            + "contacto_comercial_telefono_dos, "
            + "contacto_comercial_cargo, "
            + "contacto_comercial_mail, "
            + "proveedor_contacto_nombre, "
            + "proveedor_contacto_telefono_uno, "
            + "proveedor_contacto_telefono_dos, "
            + "proveedor_cargo, "
            + "proveedor_contacto_mail, "
            + "condicion_pago, "
            + "proveedor_requiere_oc, "
            + "proveedor_liberacion_oc, "
            + "plataforma, "
            + "modalidad, "
            + "plazo_contrato, "
            + "tarifa_venta, "
            + "tarifa_prepago, "
            + "tarifa_monitoreo, "
            + "tarifa_arriendo, "
            + "tipo_plan, "
            + "fecha_contrato, "
            + "equipo_instalado, "
            + "q_equipos, "
            + "accesorios, "
            + "administrador_plataforma_usuario, "
            + "administrador_plataforma_telefono, "
            + "administrador_plataforma_telefono_secundario, "
            + "administrador_plataforma_cargo, "
            + "administrador_plataforma_mail, "
            + "observaciones) "
            + "VALUES "
            + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    public static final String updateClientBackoffice = "UPDATE cliente "
            + "set nombre=?,"
            + "rut=?,"
            + "razon_social=?,"
            + "giro=?,"
            + "mercado=?,"
            + "segmento=?,"
            + "pagina_web=?,"
            + "direccion=?,"
            + "ejecutivo=?,"
            + "tipo_ejecutivo=?,"
            + "telefono_ejecutivo=?,"
            + "anexo_ejecutivo=?,"
            + "mail_ejecutivo=?,"
            + "contacto_comercial_nombre=?,"
            + "contacto_comercial_telefono_uno=?,"
            + "contacto_comercial_telefono_dos=?,"
            + "contacto_comercial_mail=?,"
            + "proveedor_contacto_nombre=?,"
            + "proveedor_contacto_telefono_uno=?,"
            + "proveedor_contacto_telefono_dos=?,"
            + "proveedor_contacto_mail=?,"
            + "condicion_pago=?,"
            + "modalidad=?,"
            + "plazo_contrato=?,"
            + "tarifa_prepago=?,"
            + "tarifa_monitoreo=?,"
            + "tarifa_arriendo=?,"
            + "tipo_plan=?,"
            + "fecha_contrato=?,"
            + "equipo_instalado=?,"
            + "q_equipos=?,"
            + "accesorios=?,"
            + "administrador_plataforma_usuario=?,"
            + "administrador_plataforma_telefono=?,"
            + "administrador_plataforma_mail=?,"
            + "proveedor_cargo=?,"
            + "proveedor_requiere_oc=?,"
            + "proveedor_liberacion_oc=?,"
            + "contacto_comercial_cargo=?,"
            + "tarifa_venta=?,"
            + "administrador_plataforma_telefono_secundario=?,"
            + "administrador_plataforma_cargo=?,"
            + "observaciones=? "
            + " WHERE id = ? ";
    
    public static final String insertFuntionalPackageBackoffice = "INSERT INTO paquete_funcionalidad "
            + "(nombre, "
            + "fecha_creacion, "
            + "perfil, "
            + "activo) "
            + "VALUES "
            + "(?,?,?,?)";
    
    public static final String updateFunctionalPackageBackoffice = "UPDATE paquete_funcionalidad set nombre=?,perfil=?,activo=? "
            + " WHERE id = ? ";
    
    public static final String queryFunctionalPackageBackoffice = "select pf.id, pf.nombre as name, pf.fecha_creacion as createDate, pf.perfil as profile, pf.activo as active "
            + "from paquete_funcionalidad pf "
            + "where 1=1 {WHERE_CLAUSE}";
}
