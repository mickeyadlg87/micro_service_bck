package com.redd.backoffice;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author aleal
 */
public class FunctionalityParser {

    private Map<String, Object> funcionality = new HashMap();
    private List<String> usersForTms = new ArrayList<>();

    public FunctionalityParser() {
        /**
         * USUARIOS QUE TIENEN ACCESO AL TMS
         */
        usersForTms.add("gaston-ciba");
        usersForTms.add("acampos.test");
        usersForTms.add("TSB.admin");
        usersForTms.add("sbienvenido");
        usersForTms.add("hgatica");
        usersForTms.add("SBBTS");
        usersForTms.add("pchau");
        usersForTms.add("IBARAHONA");
        usersForTms.add("despacho");
        usersForTms.add("76582060-k");
        usersForTms.add("cac");
        usersForTms.add("despachochau");
        usersForTms.add("FLOTA");
        usersForTms.add("ENUNEZ");
        usersForTms.add("Coordinador 2");
        usersForTms.add("Coordinador 3");
        usersForTms.add("Coordinador4");
        usersForTms.add("Coordinador1");
        usersForTms.add("Coordinador5");
        usersForTms.add("wkother");
        usersForTms.add("fvasquez");
        usersForTms.add("gbrand");
        usersForTms.add("lcarcamo");
        usersForTms.add("overa");
        usersForTms.add("GGarcia");
        usersForTms.add("RNAVARRO");
        usersForTms.add("fbernales-fhc");
        usersForTms.add("anafhc");
        usersForTms.add("91520000-1");
        usersForTms.add("CORDINACION");
        usersForTms.add("mtobar");
        usersForTms.add("elias-hogar");
        usersForTms.add("C_Rivera");        
        usersForTms.add("G_Brand");
        usersForTms.add("STML.admin");
        usersForTms.add("Cmino");
        usersForTms.add("sofia");
        usersForTms.add("jlvega");
        usersForTms.add("TYAN.admin");
        usersForTms.add("ana.yanguas");
        usersForTms.add("yanguas");
        usersForTms.add("pmartinez.yanguas");
        usersForTms.add("jbernales.yaguas");
        usersForTms.add("rcornejo");
        usersForTms.add("KAM.yanguas");
        usersForTms.add("TMAT.admin");
        usersForTms.add("MasterTrans1");
        usersForTms.add("MasterTrans2");
        usersForTms.add("MasterTrans3");
        usersForTms.add("MasterTrans4");
        usersForTms.add("MasterTrans5");

        /**
         * FUNCIONALIDADES DE RSLITE
         */
        funcionality.put("ADMIN_FUNC", "configuration.isAdmin");
        funcionality.put("FRONT_FLOTA_FUNC", "administration.fleets_panel");
        funcionality.put("FRONT_MOVIL_FUNC", "administration.vehicles_Panel");
        funcionality.put("FRONT_CONDUCTOR_FUNC", "administration.drivers_Panel");
        funcionality.put("FRONT_TRAILER_FUNC", "administration.trailer");
//        funcionality.put("FRONT_TIPOMOVIL_FUNC", this);
        funcionality.put("FRONT_GEOCERCA_FUNC", "administration.geofence_Panel");
        funcionality.put("FRONT_ALARMS_BASIC_FUNC", "administration.notifications_Panel");
//        funcionality.put("FRONT_ALARMAWEB_FUNC", "administration.alarms_Panel"); // alarmas web desaparece segun indicacion front end 7/oct/16
        funcionality.put("FRONT_CARTOLASEMANAL_FUNC", "reports.weeklyPanel");
        funcionality.put("FRONT_CARTOLAFLOTA_FUNC", "reports.fleetPanel");
        funcionality.put("FRONT_REPORTEEVENTO_FUNC", "reports.eventsPanel");
        funcionality.put("FRONT_DASHBOARD_FUNC", "reports.dashboardPanel");
        funcionality.put("FRONT_KMRECORRIDO_FUNC", "reports.kilometersPanel");
        funcionality.put("USUARIO_FUNC", "administration.users_Panel");
        funcionality.put("FRONT_EDL_FUNC", "reports.edlPanel");
        funcionality.put("FRONT_INACTIVEMOVIL_FUNC", "reports.inactivesVehicles");
//        funcionality.put("FRONT_KMRECORRIDO_NEWFUNC", this);
        funcionality.put("FRONT_EXCESOVELO_FUNC", "reports.overspeed");
        funcionality.put("FRONT_DETALLEEVENTOS_FUNC", "reports.eventDetail");
        funcionality.put("FRONT_FUERAHORARIO_FUNC", "reports.useAfterHours");
        funcionality.put("FRONT_CLIENTES_FUNC", "reports.client");
        funcionality.put("FRONT_VIAJES_FUNC", "reports.trips");
        funcionality.put("FRONT_DETENCIONES_FUNC", "reports.detentions");
        funcionality.put("FRONT_MOTORACTIVITY_FUNC", "reports.activityMotor");
        funcionality.put("FRONT_AEMOTOR_FUNC", "reports.aeMotors");
        funcionality.put("FRONT_ESTADOMOVIL_FUNC", "reports.movilState");
        funcionality.put("FRONT_ULTIMAPOSI_FUNC", "reports.lastState");
//        funcionality.put("FRONT_DASHBOARD_FUNC_NEW", this);
//        funcionality.put("FRONT_DRIVING_REPORT", "reports.drivers");
        funcionality.put("FRONT_ALARM_REPORT", "reports.alarm");
        funcionality.put("FRONT_DRIVER_REPORT", "reports.drivers");
        funcionality.put("FRONT_TEMPERATURE_REPORT", "reports.temperatureReport");
        funcionality.put("FRONT_DRIVERRANKINGRFID_FUNC", "reports.rfidPanel");
        funcionality.put("FRONT_TEMPERATURE_CONSOLIDATE", "reports.consolidatedTemperature");
        funcionality.put("FRONT_ALARMS_ADVANCED_FUNC", "administration.alarms_Predefined");
        funcionality.put("FRONT_HOURMETER_LEASED_REPORT", "reports.HourmeterLeased");
//        funcionality.put("FRONT_CLIENT_NEW_FUNC", this);
        funcionality.put("FRONT_CONSOLIDATED_NEW_FUNC", "reports.consolidated");
//        funcionality.put("FRONT_TRIPS_NEW_FUNC", this);
//        funcionality.put("FRONT_DETENTIONS_NEW_FUNC", this);
//        funcionality.put("FRONT_AEMOTOR_NEW_FUNC", this);
//        funcionality.put("FRONT_KM_BY_DAY_FUNC", this);

    }

    public Map<String, Object> getFuncionality() {
        return funcionality;
    }

    public List<String> getUsersForTms() {
        return usersForTms;
    }
    
}
