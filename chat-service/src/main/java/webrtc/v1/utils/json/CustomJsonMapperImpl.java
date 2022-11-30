package webrtc.v1.utils.json;

import com.google.gson.Gson;
import org.springframework.stereotype.Component;

@Component
public class CustomJsonMapperImpl implements CustomJsonMapper{

    public Object jsonParse(String jsonStr, Class className) {
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
