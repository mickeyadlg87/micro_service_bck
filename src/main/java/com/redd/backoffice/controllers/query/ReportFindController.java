package com.redd.backoffice.controllers.query;

import cl.tastets.life.objects.BasicEntity;
import cl.tastets.life.objects.Company;
import cl.tastets.life.objects.utils.RequestData;
import com.redd.backoffice.dao.impl.ReportDaoImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;
import java.util.Collections;
import org.apache.log4j.Logger;
import org.springframework.web.context.request.async.DeferredResult;

/**
 *
 * @author fgodoy
 */
@Service
public class ReportFindController {
    
    @Autowired
    private ReportDaoImpl dao;
    
    public List<BasicEntity> getFacturationReport(String realm) throws Exception {
        List<BasicEntity> response = dao.findFacturationReport(realm);
        return response;
    }
    
    public List<BasicEntity> getUnsubscribeReport(String realm, Long from, Long to) throws Exception {
        List<BasicEntity> response = dao.findUnsubscribeReport(realm, from, to);
        return response;
    }
    
    public List<BasicEntity> getCertifiedReport(String realm, String platform, Long from, Long to) throws Exception {
        List<BasicEntity> response = dao.findCertifiedReport(realm, platform, from, to);
        return response;
    }
        
    public void getFacturationReportAsync(String realm, DeferredResult<List<BasicEntity>> deferredResult) throws Exception {
        CompletableFuture.runAsync(() -> {
            Observable<List<BasicEntity>> obBe = Observable.defer(() -> {
                final List<BasicEntity> result = Collections.synchronizedList(new ArrayList<>());
                result.addAll(dao.findFacturationReport(realm));
                return Observable.just(result);
            });
            obBe.subscribe((List<BasicEntity> t) -> {
                deferredResult.setResult(t);
            }, (Throwable t) -> {
                Logger.getRootLogger().warn("Error reporte facturacion = " + t.getMessage(), t);
                deferredResult.setResult(Arrays.asList(new BasicEntity()));
            });
        }).exceptionally((Throwable t) -> {
            Logger.getRootLogger().error("CompletableFutureError en getReporteFacturacionAsync " + t.getMessage());
            deferredResult.setResult(Arrays.asList(new BasicEntity()));
            return null;
        });
    }
    
    public List<BasicEntity> getFacturationReportWithFilter(String realm, RequestData request) throws Exception {
        List<BasicEntity> response = dao.findFacturationReportWithFilter(realm, Optional.ofNullable(request.getPaginated()), Optional.ofNullable(request.getFilter()));
        return response;
    }
    
    public List<BasicEntity> getQuantityForSaleType(String realm) throws Exception {
        return dao.findQuantityForSaleType(realm);
    }

    public List<Company> getQuantityKeepAliveByCompany(String realm) throws Exception {
        return dao.findKeepAliveForAllCompanies(realm);
    }
    
    public List<BasicEntity> getQuantityForCompanyPaginated(String realm, RequestData request) throws Exception {
        return dao.findQuantityForCompany(realm, Optional.ofNullable(request.getPaginated()), Optional.ofNullable(request.getFilter()));
    }
    
}
