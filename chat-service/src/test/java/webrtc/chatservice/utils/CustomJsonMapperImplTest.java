package webrtc.chatservice.utils;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import webrtc.chatservice.dto.chat.ChattingMessage;
import webrtc.chatservice.utils.json.CustomJsonMapper;
import webrtc.chatservice.utils.json.CustomJsonMapperImpl;

public class CustomJsonMapperImplTest {

    private final CustomJsonMapper customJsonMapper = new CustomJsonMapperImpl();

//    @Test
//    void json파싱() {
//        // given
//        String json =
//                "{\"channelId\":\"ed5e8782-c137-43e7-8154-3b41002a36a0\",\"type\":\"RENEWAL\",\"nickname\":\"ksw\",\"chatMessage\":\"\",\"currentParticipants\":1,\"users\":[{\"id\":\"bc494584-4644-4089-b685-69ec431759f9\",\"email\":\"ksw111\",\"nickname\":\"ksw\"}],\"logId\":0,\"senderEmail\":\"ksw111\",\"sendTime\":1663753275172}\n";
//
//        // when
//        customJsonMapper.jsonParse(json, ChattingMessage.class);
//        // then

//    }
}
