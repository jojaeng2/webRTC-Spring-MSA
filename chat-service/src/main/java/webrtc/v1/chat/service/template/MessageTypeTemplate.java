package webrtc.v1.chat.service.template;

import webrtc.v1.chat.dto.ChattingMessage;

@FunctionalInterface
public interface MessageTypeTemplate {

    ChattingMessage setMessageType(ChattingMessage type);

}
