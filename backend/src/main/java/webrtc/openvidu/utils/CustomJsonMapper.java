package webrtc.openvidu.utils;

import com.google.gson.Gson;
import org.springframework.stereotype.Component;

@Component
public class CustomJsonMapper {

    public static Object jsonParse(String jsonStr, Class className) {
        Gson gson = new Gson();
        Object obj = new Object();
        try {
            obj = gson.fromJson(jsonStr, className);
        } catch (Exception e) {
            throw new RuntimeException("JSON 형식이 잘못되었습니다.");
        }
        return obj;
    }
}
