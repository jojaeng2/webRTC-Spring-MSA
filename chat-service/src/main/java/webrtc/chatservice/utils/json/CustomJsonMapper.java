package webrtc.chatservice.utils.json;

import webrtc.chatservice.dto.chat.ChattingMessage;

public interface CustomJsonMapper {

    Object jsonParse(String jsonStr, Class className);

}
