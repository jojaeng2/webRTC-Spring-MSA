package webrtc.chatservice.utils.log.trace;

import webrtc.chatservice.utils.log.LogStatus;

public interface LogTrace {

    LogStatus begin(String message);

    void end(LogStatus status);

    void exception(LogStatus status, Exception e);
}
