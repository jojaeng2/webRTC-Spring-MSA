package webrtc.voiceservice.util.utils.log.trace;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import webrtc.voiceservice.util.utils.log.LogId;
import webrtc.voiceservice.util.utils.log.LogStatus;

@Slf4j
@Component
public class ThreadLocalLogTrace implements LogTrace{

    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";

    private ThreadLocal<LogId> traceIdHolder = new ThreadLocal<>();

    @Override
    public LogStatus begin(String message) {
        syncLogId();
        LogId traceId = traceIdHolder.get();
        Long startTimeMs = System.currentTimeMillis();
        log.info("[{}] {}{}", traceId.getId(), addSpace(START_PREFIX, traceId.getLevel()), message);
        return new LogStatus(traceId, startTimeMs, message);
    }

    private void syncLogId() {
        LogId logId = traceIdHolder.get();
        if(logId == null)  {
            traceIdHolder.set(new LogId());
        } else {
            traceIdHolder.set(logId.createNextId());
        }
    }

    @Override
    public void end(LogStatus status) {
        complete(status, null);

    }

    @Override
    public void exception(LogStatus status, Exception e) {
        complete(status, e);
    }

    private void complete(LogStatus status, Exception e) {
        Long stopTimeMs = System.currentTimeMillis();
        long resultTimeMs = stopTimeMs - status.getStartTimeMs();
        LogId logId = status.getLogId();
        if (e == null) {
            log.info("[{}] {}{} time={}ms", logId.getId(),
                    addSpace(COMPLETE_PREFIX, logId.getLevel()), status.getMessage(),
                    resultTimeMs);
        } else {
            log.info("[{}] {}{} time={}ms ex={}", logId.getId(),
                    addSpace(EX_PREFIX, logId.getLevel()), status.getMessage(), resultTimeMs,
                    e.toString());
        }

        releaseTraceId();
    }

    private void releaseTraceId() {
        LogId traceId = traceIdHolder.get();
        if(traceId.isFirstLevel()) {
            traceIdHolder.remove(); // destroy
        } else {
            traceIdHolder.set(traceId.createPreviousId());
        }
    }


    private static String addSpace (String prefix,int level){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append((i == level - 1) ? "|" + prefix : "|   ");
        }
        return sb.toString();

    }
}
