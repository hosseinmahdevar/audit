package ir.webold.viewmodel;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditVM {
    public enum State{
        BEFORE,AFTER,BUSINESS_EXCEPTION,FATAL_EXCEPTION
    }
    private State state;
    private Object result;
    private Object input;
    private String url;
    private String ip;
    private String className;
    private String methodName;
    private String time;
    private String userId;
    private String username;
    private String rrn;
    private Long executionTime;

}
