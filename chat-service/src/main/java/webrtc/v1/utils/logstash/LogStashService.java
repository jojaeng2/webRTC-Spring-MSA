package webrtc.v1.utils.logstash;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import webrtc.v1.utils.logstash.LogForCreateChannel;

@Slf4j
@Service
@NoArgsConstructor
public class LogStashService {

    @Async
    public void execute(LogForCreateChannel logForCreateChannel) {
        log.info(String.valueOf(logForCreateChannel));
    }
}
