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
    private ChatLogRepository chatLogRepository;
    @Autowired
    private TestEntityManager em;

    String nickname1 = "nickname1";
    String email1 = "email1";
    String channelName1 = "channelName1";
    String message = "message";
    ChannelType text = TEXT;


    @Test
    @Transactional
    public void 채팅로그_저장_성공() {
        //given
        Channel channel = new Channel(channelName1, text);
        em.persist(channel);
        ChatLog chatLog = new ChatLog(ENTER, message, nickname1, email1);
        chatLog.setChannel(channel);

        //when
        chatLogRepository.save(chatLog);

        //then

    }

    @Test
    @Transactional
    public void 특정채널의_마지막_채팅로그_불러오기_성공() {
        // given
        Channel channel = new Channel(channelName1, text);
        em.persist(channel);

        for(Long i=0L; i<23L; i++) {
            ChatLog chatLog = new ChatLog(ENTER, "testMessage" + i, "testUser2", "email1");
            chatLog.setChatLogIdx(i);
            chatLog.setChannel(channel);
            chatLogRepository.save(chatLog);
        }

        // when
        List<ChatLog> findChatLogs = chatLogRepository.findLastChatLogsByChannelId(channel.getId());

        // then
        assertThat(findChatLogs.get(0).getIdx()).isEqualTo(22L);
    }

    @Test
    @Transactional
    public void 채팅로그_20개씩_불러오기_성공() {
        // given
        Channel channel = new Channel(channelName1, text);
        em.persist(channel);
        for(Long i=0L; i<100L; i++) {
            ChatLog chatLog = new ChatLog(ENTER, "testMessage" + i, "testUser2", "email1");
            chatLog.setChatLogIdx(i);
            chatLog.setChannel(channel);
            chatLogRepository.save(chatLog);
        }

        // when
        List<ChatLog> chatLogs = chatLogRepository.findChatLogsByChannelId(channel.getId(), 30L);

        // then
        assertThat(chatLogs.size()).isEqualTo(20);
    }

    @Test
    @Transactional
    public void 로그20개이하_불러오기_성공() {
        // given
        Channel channel = new Channel(channelName1, text);
        em.persist(channel);
        for(Long i=0L; i<20L; i++) {
            ChatLog chatLog = new ChatLog(ENTER, "testMessage" + i, "testUser2", "email1");
            chatLog.setChatLogIdx(i);
            chatLog.setChannel(channel);
            chatLogRepository.save(chatLog);
        }

        // when
        List<ChatLog> chatLogs = chatLogRepository.findChatLogsByChannelId(channel.getId(), 15L);

        // then
        assertThat(chatLogs.size()).isEqualTo(14L);
    }

}
