package webrtc.v1.chat.enums;

public enum RedisKeys {
    CHAT_LOG("LOG_"),
    LAST_INDEX("LAST_INDEX_"),
    BLANK("_");

    private final String prefix;

    RedisKeys(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
