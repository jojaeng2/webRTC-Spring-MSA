package webrtc.chatservice.service.chat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChatLog;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.enums.ClientMessageType;
import webrtc.chatservice.repository.channel.ChannelDBRepository;
import webrtc.chatservice.repository.chat.ChatLogRepository;
import webrtc.chatservice.repository.users.UsersRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static webrtc.chatservice.enums.ChannelType.TEXT;
import static webrtc.chatservice.enums.ChannelType.VOIP;

@ExtendWith(MockitoExtension.class)
public class ChatLogServiceTest {

    @InjectMocks
    private ChatLogServiceImpl chatLogService;
    @Mock
    private UsersRepository usersRepository;
    @Mock private ChannelDBRepository channelDBRepository;
    @Mock private ChatLogRepository chatLogRepository;
    @Mock private ChannelTopic channelTopic;
    @Mock private RedisTemplate redisTemplate;

    String nickname1 = "nickname1";
    String nickname2 = "nickname2";
    String password = "password";
    String email1 = "email1";
    String email2 = "email2";
    String channelName1 = "channelName1";
    String notExistChannelId = "null";
    String tag1 = "tag1";
    String tag2 = "tag2";
    String tag3 = "tag3";
    ChannelType text = TEXT;
    ChannelType voip = VOIP;

    @Test
    @Transactional
    public void 첫번째_채팅로그_저장성공() {
        // given
        Channel channel = new Channel(channelName1, text);
        doReturn(new ArrayList<>())
                .when(chatLogRepository).findLastChatLogsByChannelId(channel.getId());

        // when
        Long chatId = chatLogService.saveChatLog(ClientMessageType.ENTER, "testMessage", nickname1, channel, email1);

        // then
        assertThat(chatId).isEqualTo(1L);
    }

    @Test
    @Transactional
    public void N번째_채팅로그_저장성공() {
        // given
        Long testcase = 20L;
        List<ChatLog> chatLogs = new ArrayList<>();
        ChatLog chatLog = new ChatLog();
        chatLog.setChatLogIdx(testcase);
        chatLogs.add(chatLog);

        Channel channel = new Channel(channelName1, text);
        doReturn(chatLogs)
                .when(chatLogRepository).findLastChatLogsByChannelId(channel.getId());

        // when
        Long chatId = chatLogService.saveChatLog(ClientMessageType.ENTER, "testMessage", nickname1, channel, email1);

        // then
        assertThat(chatId).isEqualTo(testcase + 1L);
    }

//    @Test
//    @Transactional
//    public void 채팅로그_20개미만_불러오기_성공() {
//        // given
//        Long testcase = 10L;
//        Channel channel = new Channel(channelName1, text);
//
//
//        for(Long i=0L; i<testcase; i++) {
//            chatService.saveChatLog(ClientMessageType.ENTER, "testMessage", nickname1, channel, email1);
//        }
//
//        // when
//        List<ChatLog> chatLogs = chatService.findChatLogsByIndex(channel.getId(), testcase+1L);
//
//        // then
//        assertThat(Long.valueOf(chatLogs.size())).isEqualTo(testcase);
//    }
//
//    @Test
//    @Transactional
//    public void 채팅로그_20개초과_불러오기_성공() {
//        // given
//        Long testcase = 30L;
//        Channel channel = channelDBRepository.findChannelByChannelName(channelName1);
//
//        for(Long i=0L; i<testcase; i++) {
//            chatService.saveChatLog(ClientMessageType.ENTER, "testMessage", nickname1, channel, email1);
//        }
//
//        // when
//        List<ChatLog> chatLogs = chatService.findChatLogsByIndex(channel.getId(), testcase+1L);
//
//        // then
//        assertThat(Long.valueOf(chatLogs.size())).isEqualTo(20L);
//    }
}
