package webrtc.v1.service.chat.template;

import webrtc.v1.dto.chat.ChattingMessage;

@FunctionalInterface
public interface MessageTypeTemplate {

    ChattingMessage setMessageType(ChattingMessage type);

}
