package webrtc.openvidu.utils;

public interface CustomJsonMapper {

    Object jsonParse(String jsonStr, Class className);
}
