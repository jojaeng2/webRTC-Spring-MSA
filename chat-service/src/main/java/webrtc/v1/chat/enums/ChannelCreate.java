package webrtc.v1.chat.enums;

import lombok.Getter;

@Getter
public enum ChannelCreate {

    NOTICE("[알림]"),
    MESSAGE(" 님이 채팅방을 생성했습니다."),
    EMAIL("NOTICE"),
    NICKNAME("NOTICE");

    private final String message;

    ChannelCreate(String message) {
        this.message = message;
    }
}
