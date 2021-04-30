package ir.webold.service;

import javax.servlet.http.HttpServletRequest;

public interface AuditService {
    public String getIp();
    public HttpServletRequest getCurrentRequest();
    public String getRequestUrl();
    public String getUserId();
    public String getUsername();
    //RandomRequestNumber
    public String getRRN();

}
