package webrtc.authservice.utils.log;


import lombok.Getter;

@Getter
public class LogStatus {

    private LogId logId;
    private Long startTimeMs;
    private String message;

    public LogStatus(LogId logId, Long startTimeMs, String message) {
        this.logId = logId;
        this.startTimeMs = startTimeMs;
        this.message = message;
    }
}
