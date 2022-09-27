package webrtc.chatservice.utils.log.callback;

import org.springframework.stereotype.Component;
import webrtc.chatservice.utils.log.LogStatus;
import webrtc.chatservice.utils.log.trace.LogTrace;

@Component
public class LogTemplate {

    private final LogTrace trace;

    public LogTemplate(LogTrace trace) {
        this.trace = trace;
    }

    public <T> T execute(String className, String methodName, LogCallback<T> callback) {
        LogStatus status = null;
        String message = className + "." + methodName;

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
