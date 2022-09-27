package webrtc.voiceservice.util.utils.log.trace;


import webrtc.voiceservice.util.utils.log.LogStatus;

public interface LogTrace {

    LogStatus begin(String message);

    void end(LogStatus status);

    void exception(LogStatus status, Exception e);
}
