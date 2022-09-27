package webrtc.authservice.utils.log.trace;


import webrtc.authservice.utils.log.LogStatus;

public interface LogTrace {

    LogStatus begin(String message);

    void end(LogStatus status);

    void exception(LogStatus status, Exception e);
}
