package webrtc.v1.channel.enums;

public enum ChannelInfo {
    CREATE_TTL(60L * 60L),
    EXPIRE(-2L);

    private final Long ttl;


    ChannelInfo(Long ttl) {
        this.ttl = ttl;
    }

    public Long getTtl() {
        return ttl;
    }
}
