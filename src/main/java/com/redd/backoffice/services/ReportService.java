package com.redd.backoffice.services;

import cl.tastets.life.core.framework.exceptions.WebException;
import cl.tastets.life.objects.BasicEntity;
import cl.tastets.life.objects.Company;
import cl.tastets.life.objects.utils.RequestData;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.redd.backoffice.controllers.crud.ReportCRUDController;
import com.redd.backoffice.controllers.query.ReportFindController;
import io.swagger.annotations.Api;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

/**
 *
 * @author fgodoy
 */
@RestController
@RequestMapping(value = "/backoffice/report", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "Report Service")
public class ReportService {
    
    @Autowired
    private ReportFindController reportController;
    
    @Autowired
    private ReportCRUDController reportCrudController;

    private static final org.slf4j.Logger log = LoggerFactory.getLogger("REPORT");
    
    @RequestMapping(value = "/getAllFacturationReport", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultFacturationReport")
    public List<BasicEntity> allFacturationReport(@RequestParam(value = "realm") String realm)
            throws Exception {
        try {
            return reportController.getFacturationReport(realm);
        } catch (Exception e) {
            Logger.getRootLogger().error(e.toString());
            throw new WebException("Error al obtener el reporte de facturacion", HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "/getAllFacturationReportAsync", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultFacturationReport")
    public DeferredResult<List<BasicEntity>> allFacturationReportAsync(@RequestParam(value = "realm") String realm)
            throws Exception {
        try {
            final DeferredResult<List<BasicEntity>> deferredResult = new DeferredResult<>(100000);
            reportController.getFacturationReportAsync(realm, deferredResult);
            return deferredResult;
        } catch (Exception e) {
            Logger.getRootLogger().error(e.toString());
            throw new WebException("Error al obtener el reporte de facturacion", HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "/getUnsubscribeReport", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultUnsubscribeReport")
    public List<BasicEntity> getUnsubscribeReport(@RequestParam(value = "realm") String realm, @RequestParam(value = "from") Long from, @RequestParam(value = "to") Long to) {
        try {
            return reportController.getUnsubscribeReport(realm, from, to);
        } catch (Exception e) {
            Logger.getRootLogger().error(e.toString());
            throw new WebException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "/getFacturationReportFilter", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultFacturationReportFilter")
    public List<BasicEntity> facturationReportFilter(@RequestParam(value = "realm") String realm,
            @RequestBody RequestData requestData) throws Exception {
        try {
            return reportController.getFacturationReportWithFilter(realm, requestData);
        } catch (Exception e) {
            Logger.getRootLogger().error(e.toString());
            throw  new WebException("Error al obtener los primeros cien datos del reporte de facturacion", HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "/getQuantityForCompanyPaginated", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultFacturationReportFilter")
    public List<BasicEntity> quantityForCompanyPaginated(@RequestParam(value = "realm") String realm,
            @RequestBody RequestData requestData) throws Exception {
        try {
            return reportController.getQuantityForCompanyPaginated(realm, requestData);
        } catch (Exception e) {
            Logger.getRootLogger().error(e.toString());
            throw new WebException("Error al obtener cantidad de moviles", HttpStatus.BAD_REQUEST);
        }
    }
        
    @RequestMapping(value = "/getQuantityBySaleType", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE)
    @HystrixCommand(fallbackMethod = "defaultFacturationReport")
    public List<BasicEntity> getQuantityForSaleType(@RequestParam(value = "realm") String realm)
            throws Exception {
        try {
            return reportController.getQuantityForSaleType(realm);
        } catch (Exception e) {
            Logger.getRootLogger().error(e.toString());
            throw new WebException("Error al buscar cantidad de moviles", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/getKeepAliveForEachCustomer", method = RequestMethod.POST)
    @HystrixCommand(fallbackMethod = "defaultFacturationReport")
    public List<Company> getKeepAliveByCompany(@RequestParam(value = "realm") String realm)
            throws Exception {
        try {
            return reportController.getQuantityKeepAliveByCompany(realm);
        } catch (Exception e) {
            log.error("Error al buscar keepAlive ", e);
            throw new WebException("Error al buscar keepAlive ", HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "/saveReport", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BasicEntity save(@RequestBody BasicEntity report) {
        try {
            return reportCrudController.insertReport(report.getString("realm"), report);
        } catch (Exception e) {
            Logger.getRootLogger().error(e.toString());
            throw new WebException("Error al guardar reporte", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/updateReport", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BasicEntity update(@RequestBody BasicEntity report) {
        try {
            return reportCrudController.updateReport(report.getString("realm"), report);
        } catch (Exception e) {
            Logger.getRootLogger().error(e.toString());
            throw new WebException("Error al actualizar reporte", HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "/getCertifiedReport", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultCertifiedReport")
    public List<BasicEntity> getCertifiedReport(@RequestParam(value = "realm") String realm, @RequestParam(value = "platform") String platform, @RequestParam(value = "from") Long from, @RequestParam(value = "to") Long to) {
        try {
            return reportController.getCertifiedReport(realm, platform, from, to);
        } catch (Exception e) {
            Logger.getRootLogger().error(e.toString());
            throw new WebException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public List<BasicEntity> defaultFacturationReport(String realm) {
        return Arrays.asList(new BasicEntity());
    }
    
    public List<BasicEntity> defaultFacturationReportFilter(String realm, RequestData requestData) {
        return Arrays.asList(new BasicEntity());
    }
    
    public List<BasicEntity> defaultUnsubscribeReport(String realm, Long from, Long to) {
        return Arrays.asList(new BasicEntity());
    }
    
    public List<BasicEntity> defaultCertifiedReport(String realm, String platform, Long from, Long to) {
        return Arrays.asList(new BasicEntity());
    }
    
        
    
}
