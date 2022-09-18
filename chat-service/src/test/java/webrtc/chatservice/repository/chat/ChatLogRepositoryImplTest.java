package webrtc.chatservice.repository.chat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChatLog;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.enums.ClientMessageType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static webrtc.chatservice.enums.ChannelType.TEXT;
import static webrtc.chatservice.enums.ClientMessageType.ENTER;

@DataJpaTest
@Import({
        ChatLogRepositoryImpl.class
})
public class ChatLogRepositoryImplTest {

    @Autowired
    private ChatLogRepository repository;
    @Autowired
    private TestEntityManager em;

    String nickname1 = "nickname1";
    String email1 = "email1";
    String channelName1 = "channelName1";
    String message = "message";
    ChannelType text = TEXT;

    @Test
    void 채널의마지막채팅로그불러오기성공() {
        // given
        Long testcase = 23L;
        Channel channel = createChannel(channelName1, text);

        for(Long i=0L; i<testcase; i++) {
            ChatLog chatLog = createChatLog(ENTER, "testMessage" + i, "testUser2", "email1");
            chatLog.setChatLogIdx(i);
            channel.addChatLog(chatLog);
        }
        em.persist(channel);


        // when
        List<ChatLog> findChatLogs = repository.findLastChatLogsByChannelId(channel.getId());

        // then
        assertThat(findChatLogs.get(0).getIdx()).isEqualTo(22L);
    }


    @Test
    public void 채팅로그불러오기_20개미만() {
        // given
        Channel channel = new Channel(channelName1, text);
        for(Long i=0L; i<20L; i++) {
            ChatLog chatLog = new ChatLog(ENTER, "testMessage" + i, "testUser2", "email1");
            chatLog.setChatLogIdx(i);
            channel.addChatLog(chatLog);
        }

        em.persist(channel);

        // when
        List<ChatLog> chatLogs = repository.findChatLogsByChannelId(channel.getId(), 15L);

        // then
        assertThat(chatLogs.size()).isEqualTo(14L);
    }
    @Test
    void 채팅로그불러오기_20개초과() {
        // given
        Channel channel = new Channel(channelName1, text);
        for(Long i=0L; i<100L; i++) {
            ChatLog chatLog = new ChatLog(ENTER, "testMessage" + i, "testUser2", "email1");
            chatLog.setChatLogIdx(i);
            channel.addChatLog(chatLog);
        }

        em.persist(channel);

        // when
        List<ChatLog> chatLogs = repository.findChatLogsByChannelId(channel.getId(), 30L);


        // then
        assertThat(chatLogs.size()).isEqualTo(20);
    }


    private Channel createChannel(String name, ChannelType type) {
        return new Channel(name, type);
    }

    private ChatLog createChatLog(ClientMessageType type, String message, String nickname, String email) {
        return new ChatLog(type, message, nickname, email);
    }

}
