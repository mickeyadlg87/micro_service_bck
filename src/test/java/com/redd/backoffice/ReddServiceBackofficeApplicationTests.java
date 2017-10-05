package com.redd.backoffice;

import cl.tastets.life.objects.BasicEntity;
import cl.tastets.life.objects.Company;
import cl.tastets.life.objects.RealmEnum;
import cl.tastets.life.objects.backoffice.DeviceType;
import cl.tastets.life.objects.backoffice.SimCard;
import cl.tastets.life.objects.utils.Paginated;
import cl.tastets.life.objects.utils.QueryFilter;
import cl.tastets.life.objects.utils.RequestData;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ReddServiceBackofficeApplication.class)
@WebAppConfiguration
public class ReddServiceBackofficeApplicationTests {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;
    ObjectMapper mapper = new ObjectMapper();
    
    @Value("${microservices.user}")
    private String serviceUser;
    @Value("${microservices.company}")
    private String serviceCompany;
    @Value("${microservices.auth}")
    private String serviceAuth;
    
    private final StringBuilder sb = new StringBuilder();
    private final RestTemplate rtmpl = new RestTemplate();
    private FunctionalityParser funParser = new FunctionalityParser();

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        rtmpl.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void getCustomerById() throws Exception {

        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.post("/backoffice/customer/getById?realm=backoffice&customerId=1").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Company company = mapper.readValue(result.andReturn().getResponse().getContentAsByteArray(), Company.class);
        System.out.println("-------- Customer = " + company);
        Assert.assertTrue(company.get("rut") != null);

    }
    
    @Test
    public void saveResponseCallbackTest() throws Exception {
        
        String callbackString = "{ \"_id\" : { \"$date\" : \"2016-11-09T15:59:42.951Z\"} , \"imei\" : \"868789020645452\" ,"
                + " \"script_name\" : \"2000007\" , \"script_timeout\" : 30 , \"script_date\" : { \"$date\" : \"2016-11-09T15:59:38.783Z\"} , "
                + "\"timeout_date\" : { \"$date\" : \"2016-11-09T15:59:42.951Z\"} , \"callback_url\" : \"http://www.chilerock.cl/gps/\" , "
                + "\"script_lines\" : [ { \"line\" : \"AT+GTRTO=gv300,1,,,,,,FFFF$\" , \"id_line\" : { \"$oid\" : \"582347ea0cf23cf35d872589\"} , \"request_date\" : { \"$date\" : \"2016-11-09T15:59:48.000Z\"} , \"response_date\" : { \"$date\" : \"2016-11-09T15:59:49.000Z\"} , \"response_line\" : \"+ACK:GTRTO,250803,868789020645452,gv300,RTL,FFFF,20161109155949,3FBE$\"}] ,"
                + " \"status\" : \"ok\"} ";
        
        HashMap callbackMap = mapper.readValue(callbackString, HashMap.class);

        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.post("/backoffice/pota/saveCallbackResponse/backoffice").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(callbackMap))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        BasicEntity resultCallback = mapper.readValue(result.andReturn().getResponse().getContentAsByteArray(), BasicEntity.class);
        System.out.println("-------- Result Callback = " + resultCallback);
        Assert.assertTrue(resultCallback.get("idPotaMsg") != null);

    }

    @Test
    public void getSimCardByPhoneNumber() throws Exception {

        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get("/backoffice/simcard/getSimCardByPhoneNumber?realm=backoffice&phoneNumber=+56989562325")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        SimCard simcard = mapper.readValue(result.andReturn().getResponse().getContentAsByteArray(), SimCard.class);
        System.out.println("-------- Simcard = " + simcard);
        Assert.assertTrue(simcard.get("phoneNumber") != null);

    }

    @Test
    public void getAllSimCards() throws Exception {

        RequestData req = RequestData.from();
        req.setFilter(QueryFilter.from());
        req.setPaginated(Paginated.from().put("limit", 50).put("offset", 0));

        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.post("/backoffice/simcard/getAllSimCards?realm=backoffice").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(req))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        List<SimCard> listDev = Arrays.asList(mapper.readValue(result.andReturn().getResponse().getContentAsByteArray(), SimCard[].class));
        Assert.assertTrue(listDev.size() > 0);

    }
    
    @Test
    public void getFacturationReportwithFilter() throws Exception {

        RequestData req = RequestData.from();
        req.setFilter(QueryFilter.from());
        req.setPaginated(Paginated.from().put("limit", 10).put("offset", 0));

        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.post("/backoffice/report/getFacturationReportFilter?realm=rslite").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(req))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        List<DeviceType> listReport = Arrays.asList(mapper.readValue(result.andReturn().getResponse().getContentAsByteArray(), DeviceType[].class));
        System.out.println("--- Reporte Facturacion = " + listReport.size());
        Assert.assertTrue(listReport.size() > 0);

    }

//  @Test
    public void simcardUpdate() throws Exception {
        SimCard simcard = new SimCard();
        simcard.put("id", 5);
        simcard.put("realm", "backoffice");
        simcard.put("phoneNumber", "+1111111111");
        simcard.put("creationDate", "2016-01-01T00:00:00");
        simcard.put("lowdate", null);
        simcard.put("producerId", 1);
        simcard.put("statusDescription", 5);

        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.put("/backoffice/simcard/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(simcard))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        SimCard s = mapper.readValue(result.andReturn().getResponse().getContentAsString(), SimCard.class);
        System.out.println("------ SimCard Updated = " + s);
        Assert.assertTrue(s != null);

    }

//    @Test
    public void simcardSave() throws Exception {
        SimCard simcard = new SimCard();
        simcard.put("realm", "backoffice");
        simcard.put("phoneNumber", "+2222222222");
        simcard.put("creationDate", "2016-02-02T00:00:00");
        simcard.put("producerId", 1);
        simcard.put("statusDescription", 5);

        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.post("/backoffice/simcard/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(simcard))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        SimCard s = mapper.readValue(result.andReturn().getResponse().getContentAsString(), SimCard.class);
        System.out.println("------ SimCard Save = " + s);
        Assert.assertTrue(s != null);

    }

    
    @Test
    public void customerUpdate() throws Exception {
        Company company = new Company();
        company.put("id", 1);
        company.put("realm", "backoffice");
        company.put("customerName", "Empresa Default");
        company.put("rut", "15673241-9");
        company.put("giro", "S/I");
        company.put("market", "leasing");
        company.put("segment", "camiones");
        company.put("webPage", "http://m.cosa.com");
        company.put("address", "calle 123");
        company.put("executive", "pedro perez");
        company.put("executiveType", "gerente");
        company.put("executivePhone", "328612");
        company.put("executiveAnnex", 2);
        company.put("executiveMail", "none@fake.com");
        company.put("tradeContactName", "pedro nieto");
        company.put("tradeContactPhone", "16548616");
        company.put("tradeContactPhoneTwo", "328612");
        company.put("tradeContactMail", "none@fake.com");
        company.put("supplierName", "pedro ramirez");
        company.put("supplierPhone", "5615154566");
        company.put("supplierPhoneTwo", "3286121561");
        company.put("supplierMail", "none@fake.com");
        company.put("paymentCondition", 100);
        company.put("modality", "postpago");
        company.put("termContract", 12);
        company.put("prepaidRate", 1.1);        
        company.put("searchingRate", 2.12);        
        company.put("rentRate", 3.14);
        company.put("planType", "arriendo");
        company.put("contractDate", System.currentTimeMillis());
        company.put("installedKit", "N/A");
        company.put("quantityKit", 0);
        company.put("accessory", "N/A");
        company.put("platformManagerUser", "aleal");
        company.put("platformManagerPhone", "151531515");
        company.put("platformManagerMail", "cosa@cosa.com");
        company.put("supplierJobTitle", "test supplier");
        company.put("oc", 1);
        company.put("supplierLiberationOC", "si requiere");
        company.put("tradeContactJobTitle", "test trade");
        company.put("saleRate", 1);
        company.put("platformManagerSecondaryPhone", "6464646");
        company.put("platformManagerJobTitle", "test platform");
        company.put("observation", "Esta es una nueva observacion de la compañia");
        
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.put("/backoffice/customer/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(company))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Company c = mapper.readValue(result.andReturn().getResponse().getContentAsString(), Company.class);
        System.out.println("------ Company Updated = " + c);
        Assert.assertTrue(c != null);

    }

    @Test
    public void getAllDeviceTypes() throws Exception {

        RequestData req = RequestData.from();
        req.setFilter(QueryFilter.from());
        req.setPaginated(Paginated.from().put("limit", 50).put("offset", 0));

        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.post("/backoffice/device/type/getAll?realm=backoffice").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(req))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        List<DeviceType> listDev = Arrays.asList(mapper.readValue(result.andReturn().getResponse().getContentAsByteArray(), DeviceType[].class));

        Assert.assertTrue(listDev.size() > 0);

    }
    
    @Test
    public void testAccesoriesInstalledByModemId() throws Exception {
        try {
            ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get("/backoffice/accesory/getInstalledByMid")
                    .param("realm", RealmEnum.backoffice.toString())
                    .param("mid", "861074021447399")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            BasicEntity accesorio = mapper.readValue(result.andReturn().getResponse().getContentAsByteArray(), BasicEntity.class);
            System.out.println("ACCESSORY FOR DEVICE -> " + accesorio);
            Assert.assertNotNull(accesorio.getLong("validationDate"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Test
    public void setProfileForAllUsers() {
        
        int totalUsersOk = 0;
        int totalUsersFail = 0;

        try {
            File fileJson = new File("src/main/resources/profilePerUser.json");
            Object profileObj = mapper.readValue(fileJson, Object.class);
            String profileString = mapper.writeValueAsString(profileObj);
            
            BasicEntity request = new BasicEntity();
            request.put("filter", QueryFilter.from());
            request.put("paginated", Paginated.from().put("limit", 500).put("offset", 0));
            
            /**
             * Para ejecutar la carga con todas las empresas
             */    
//            List<HashMap<String, Object>> rawListCompany = getWSResponsePOSTList(serviceCompany + "getAll?realm=rslite", request);
            
            /**
             * Para ejecutar la carga con una unica empresa
             */
            List<HashMap<String, Object>> rawListCompany = Arrays.asList(getWSResponsePOST(serviceCompany + "getById?realm=rslite&companyId=150", request));
            
            List<HashMap<String, Object>> rawListUsers;
            String profileEdit;
            Map<String, Object> profileMap;
            List<HashMap> arrayProfiles;
            System.out.println("++++++++++ Company size = " + rawListCompany.size());
            
            for (HashMap company : rawListCompany) {
                if (company.get("id") != null && !company.get("id").equals(0)) {
                    int usersOk = 0;
                    int usersFail = 0;
//                    System.out.println(serviceUser + "getAll?realm=rslite" + "&companyId=" + company.get("id") + request);
                    rawListUsers = null;
                    rawListUsers = getWSResponsePOSTList(serviceUser + "getAll?realm=rslite" + "&companyId=" + company.get("id"), request);
                    System.out.println("++++++++++ Company Id = " + company.get("id") + ", Users size = " + rawListUsers.size());
                    for (HashMap user : rawListUsers) {
                        if (user.get("userName") != null && !user.get("userName").equals("")) {
                            profileEdit = profileString.replaceAll("userSel", (String) user.get("userName")).replaceAll("plataformSel", "rslite");
                            profileMap = mapper.readValue(profileEdit, Map.class);
                            arrayProfiles = (List) profileMap.get("profiles");
                            parseProfileToFunctionality(arrayProfiles, (List) user.get("profiles"));
                            sb.setLength(0);
                            sb.append(serviceAuth).append("addProfiles?realm=")
                                    .append("rslite").append("&device=desktop&user=")
                                    .append((String) user.get("userName"));
                            try {
                                getWsResponsePUTwithObject(sb.toString(), arrayProfiles);
                                usersOk++;
//                                System.out.println("COMPANY ID = " + company.get("id") + ", USER NAME = " + user.get("userName") + " -> PROFILE OK ");
                            } catch (Exception excep) {
                                System.out.println("xxxxxxxxxx COMPANY ID = " + company.get("id") + ", USER NAME = " + user.get("userName") + " -> FAIL!, Exception.." + excep.getLocalizedMessage());
                                usersFail++;
                            }                            
                        }                        
                    }
                    totalUsersOk += usersOk;
                    totalUsersFail += usersFail;
                }
            }
            System.out.println("---------- TOTALES ---------");
            System.out.println("--- USUARIOS OK = " + totalUsersOk);
            System.out.println("--- USUARIOS FAIL = " + totalUsersFail);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
    
    private Object getWsResponsePUTwithObject(String urlWS, Object request) {
        rtmpl.put(urlWS, request);
        return request;
    }
    
    private List<HashMap<String, Object>> getWSResponsePOSTList(String urlWS, HashMap<String, Object> request) {
        return Arrays.asList(rtmpl.postForObject(urlWS, request, HashMap[].class));
    }
    
    private HashMap<String, Object> getWSResponsePOST(String urlWS, HashMap<String, Object> request) {
        return rtmpl.postForObject(urlWS, request, HashMap.class);
    }
    
    private List<HashMap> addTMSprofile(String username) throws IOException {
        File fileJson = new File("src/main/resources/profileTransManager.json");
        Object profileObj = mapper.readValue(fileJson, Object.class);
        String profileString = mapper.writeValueAsString(profileObj);
        String profileTms = profileString.replaceAll("userSel", username).replaceAll("plataformSel", "rslite");
        Map<String, Object> profileTmsMap = mapper.readValue(profileTms, Map.class);
        List<HashMap> arrayProfiles = (List) profileTmsMap.get("profiles");
        return arrayProfiles;
    }
    
    private Boolean isUserInTms(String user) {
        return funParser.getUsersForTms().contains(user);
    }
    
    private void parseProfileToFunctionality(List<HashMap> rawProfile, List<HashMap> oldFunctionalities) throws IOException {
       
        Set<HashMap> functionalities = new HashSet<>();
        List<String> enableTitles = new ArrayList<>();
        
        // Recorre los perfiles que vienen en el arreglo profiles del User, de manera que inserta en el hashset functionalities
        // cada una de las funcionalidades que posee el usuario en sus perfiles, se usa hashset para evitar funcionalidades duplicadas
        // cada profile tiene adentro un arreglo con llave "functionality"        
        oldFunctionalities.stream().map((profile) -> (List<HashMap>) profile.get("functionality")).forEach((function) -> {
            function.stream().forEach((f) -> {
                functionalities.add(f);
            });
        });
        
//        for (HashMap function : functionalities) {
//            System.out.println("funciones: " + function.get("code"));
//            if (funParser.getFuncionality().containsKey((String) function.get("code"))) {
//                enableTitles.add((String) funParser.getFuncionality().get((String) function.get("code")));
//            }
//        }  

        // recorro el hashset funcionalities, de manera que extraigo los que conincidan con las llaves del hashmap definido en la
        // clase FunctionalityParser, de esta manera se estabelce cuales titulos (titles) del perfilamiento nuevo 
        // deben quedar con el active en true
        functionalities.stream().filter((function) -> (funParser.getFuncionality().containsKey((String) function.get("code")))).forEach((function) -> {
            enableTitles.add((String) funParser.getFuncionality().get((String) function.get("code")));
        });                
                
        // se lee el perfilamiento nuevo (raw profile), donde los childs tienen los permisos de acceso a cada
        // una de las opciones de lite, de manera que se recorre el arreglo childs y a cada titulo que corresponda
        // con enabledTitles se le pone el active en true
        for (HashMap newProfile : rawProfile) {
            List<HashMap> childs = (List) newProfile.get("childs");
            String userName = (String) newProfile.get("user");
            for (HashMap child : childs) {
                if (enableTitles.contains((String) child.get("title"))) {
                    child.put("active", true);
                }
                // Esto es para agregar perfilamiento TransManager 
                if (((String) child.get("title")).equals("transManager.enabled") && isUserInTms(userName)) {
                    child.put("active", true);
                    child.put("childs", addTMSprofile(userName));
                }
            }
        }
    }

}
