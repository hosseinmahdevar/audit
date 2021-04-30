package ir.webold.service;


import ir.webold.filter.AppFilter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

public abstract class AbstractAuditService implements AuditService{
    @Override
    public HttpServletRequest getCurrentRequest(){
        return ((ServletRequestAttributes)Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }
    @Override
    public String getRRN() {
        return getCurrentRequest().getHeader(AppFilter.RRN);
    }

    @Override
    public String getIp() {
        return getCurrentRequest().getRemoteAddr();
    }

    @Override
    public String getRequestUrl() {
        return  getCurrentRequest().getRequestURI();
    }

}
