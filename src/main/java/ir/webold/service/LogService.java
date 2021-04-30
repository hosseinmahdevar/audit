package ir.webold.service;

import ir.webold.viewmodel.AuditVM;
import org.springframework.boot.logging.LogLevel;

public interface LogService {
    public void log(LogLevel level, AuditVM auditVM,String logClass);
}
