package webrtc.chatservice.utils.log.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import webrtc.chatservice.utils.log.LogStatus;
import webrtc.chatservice.utils.log.trace.LogTrace;

@Component
@RequiredArgsConstructor
public class LogTemplate {

    private final LogTrace trace;

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
