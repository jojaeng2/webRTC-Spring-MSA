package webrtc.chatservice.utils.log.callback;

import webrtc.chatservice.utils.log.LogStatus;
import webrtc.chatservice.utils.log.trace.LogTrace;

public class LogTemplate {

    private final LogTrace trace;

    public LogTemplate(LogTrace trace) {
        this.trace = trace;
    }

    public <T> T execute(String message, LogCallback<T> callback) {
        LogStatus status = null;

        try {
            status = trace.begin(message);

            // 로직 호출
            T result = callback.call();
            // 로직 호출 종료

            trace.end(status);

            return result;
        } catch (Exception e) {
            trace.exception(status, e);
            throw e;
        }
    }
}
