package webrtc.chatservice.service.chat.template;

import webrtc.chatservice.dto.chat.ChattingMessage;

@FunctionalInterface
public interface CreateChattingMessageTemplate {

    ChattingMessage setMessageType(ChattingMessage type);

}
