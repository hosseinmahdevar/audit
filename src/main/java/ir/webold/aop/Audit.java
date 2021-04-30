package ir.webold.aop;

import ir.webold.filter.AppFilter;
import ir.webold.service.AuditService;
import ir.webold.service.LogService;
import ir.webold.viewmodel.AuditVM;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;

@Aspect
@Component
public class Audit {
    private final AuditService auditService;
    private final SimpleDateFormat simpleDateFormat;
    private final LogService logService;
    private static String exceptionPackage;

    @Value("${audit.business.exception:ir.webold}")
    private void setExceptionPackage(String exceptionPackage) {
        Audit.exceptionPackage = exceptionPackage;
    }

    public Audit(AuditService auditService, LogService logService) {
        this.auditService = auditService;
        this.logService = logService;
        simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
    }

    //exceptions
    @AfterThrowing(pointcut = "within(@org.springframework.web.bind.annotation.RestController *)", throwing = "ex")
    public void logAfterThrowingAll(JoinPoint joinPoint, Exception ex) throws Throwable {
        AuditVM auditVM = getAuditReqVM(joinPoint);
        auditVM.setResult(ex.getMessage());
        boolean isBusinessException = ex.getClass().getPackage().getName().startsWith(exceptionPackage);
        auditVM.setState(isBusinessException ? AuditVM.State.BUSINESS_EXCEPTION : AuditVM.State.FATAL_EXCEPTION);
        logService.log(isBusinessException ? LogLevel.WARN : LogLevel.FATAL, auditVM, joinPoint.getSignature().getDeclaringTypeName());
        throw ex;
    }


    @Before("within(@org.springframework.web.bind.annotation.RestController *)")
    public void beforeRestCall(JoinPoint joinPoint) {
        AuditVM auditVM = getAuditReqVM(joinPoint);
        auditVM.setInput(Arrays.asList(joinPoint.getArgs()));
        auditVM.setState(AuditVM.State.BEFORE);
        logService.log(LogLevel.INFO, auditVM, joinPoint.getSignature().getDeclaringTypeName());
    }

    @AfterReturning(pointcut = "within(@org.springframework.web.bind.annotation.RestController *)", returning = "result")
    public void afterRestCall(JoinPoint joinPoint, Object result) {
        AuditVM auditVM = getAuditReqVM(joinPoint);
        auditVM.setResult(result);
        auditVM.setState(AuditVM.State.AFTER);
        logService.log(LogLevel.INFO, auditVM, joinPoint.getSignature().getDeclaringTypeName());
    }

    private AuditVM getAuditReqVM(JoinPoint joinPoint) {
        AuditVM auditVM = AuditVM.builder()
                .methodName(joinPoint.getSignature().getName())
                .className(joinPoint.getSignature().getDeclaringTypeName())
                .url(auditService.getRequestUrl())
                .ip(auditService.getIp())
                .userId(auditService.getUserId())
                .username(auditService.getUsername())
                .rrn(auditService.getRRN())
                .time(simpleDateFormat.format(new Timestamp(System.currentTimeMillis())))
                .build();
        String startTime = auditService.getCurrentRequest().getHeader(AppFilter.START_TIME);
        if (startTime != null)
            auditVM.setExecutionTime(System.currentTimeMillis() - Long.parseLong(startTime));
        return auditVM;
    }


}
