package webrtc.chatservice.utils.json;

public interface CustomJsonMapper {

    Object jsonParse(String jsonStr, Class className);
}
