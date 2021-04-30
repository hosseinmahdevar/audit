package ir.webold.service;

import ir.webold.viewmodel.AuditVM;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

@Component
public class Log4JServiceImpl implements LogService{
    @Override
    public void log(LogLevel level, AuditVM auditVM, String logClass) {
        Level lvl;
        switch (level) {
            case FATAL:
                lvl=Level.FATAL;
                break;
            case WARN:
                lvl = Level.WARN;
                break;
            default :
                lvl=Level.INFO;
                break;
        }
        LogManager.getLogger(logClass).log(lvl,auditVM);
    }
}
