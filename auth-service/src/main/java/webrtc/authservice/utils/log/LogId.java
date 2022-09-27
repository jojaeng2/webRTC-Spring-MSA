package webrtc.authservice.utils.log;

import lombok.Getter;

import java.util.UUID;

@Getter
public class LogId {
    private String id;
    private int level;

    public LogId(String id, int level) {
        this.id = id;
        this.level = level;
    }

    public LogId() {
        this.id = createId();
        this.level = 0;
    }


    private String createId() {

        return UUID.randomUUID().toString().substring(0, 10);
    }

    public LogId createNextId() {
        return new LogId(id, level+1);
    }

    public LogId createPreviousId() {
        return new LogId(id, level-1);
    }

    public boolean isFirstLevel() {
        return level == 0;
    }
}
