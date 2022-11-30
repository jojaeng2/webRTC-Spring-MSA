package webrtc.v1.utils.log.trace;

import webrtc.v1.utils.log.LogStatus;

public interface LogTrace {

    LogStatus begin(String message);

    void end(LogStatus status);

    void exception(LogStatus status, Exception e);
}
