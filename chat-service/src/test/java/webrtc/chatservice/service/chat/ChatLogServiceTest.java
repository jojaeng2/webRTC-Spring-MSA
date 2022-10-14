package webrtc.chatservice.service.chat;

import org.junit.jupiter.api.Assertions;
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
import webrtc.chatservice.repository.channel.ChannelListRepository;
import webrtc.chatservice.repository.chat.ChatLogRepository;
import webrtc.chatservice.repository.users.UsersRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static webrtc.chatservice.enums.ChannelType.TEXT;
import static webrtc.chatservice.enums.ChannelType.VOIP;

@ExtendWith(MockitoExtension.class)
public class ChatLogServiceTest {

    @InjectMocks
    private ChatLogServiceImpl chatLogService;
    @Mock
    private ChatLogRepository chatLogRepository;

    String nickname1 = "nickname1";
    String email1 = "email1";
    String channelName1 = "channelName1";
    ChannelType text = TEXT;
    ClientMessageType type = ClientMessageType.CHAT;
    int maxi = 20;
    Long idx = 10L;
    Long lastIndex = 300L;

    @Test
    void 채팅로그저장성공빈배열() {
        // given
        Channel channel = createChannel(channelName1, text);
        doReturn(new ArrayList<>())
                .when(chatLogRepository).findLastChatLogsByChannelId(any(String.class));

        // when
        long resultIdx = chatLogService.saveChatLog(ClientMessageType.CHAT, "test", nickname1, channel, email1);

        // then
        assertThat(resultIdx).isEqualTo(1L);
    }

    @Test
    void 채팅로그저장성공빈배열아님() {
        // given
        Channel channel = createChannel(channelName1, text);
        doReturn(createLastChatLog())
                .when(chatLogRepository).findLastChatLogsByChannelId(any(String.class));

        // when
        long resultIdx = chatLogService.saveChatLog(ClientMessageType.CHAT, "test", nickname1, channel, email1);

        // then
        assertThat(resultIdx).isEqualTo(lastIndex+1L);

    }

    @Test
    void 마지막로그찾기성공() {
        // given
        Channel channel = createChannel(channelName1, text);
        doReturn(List.of(createChatLog()))
                .when(chatLogRepository).findLastChatLogsByChannelId(any(String.class));

        // when
        ChatLog chatLog = chatLogService.findLastChatLogsByChannelId(channel.getId());

        // then
        assertThat(chatLog).isNotNull();
    }

    @Test
    void 마지막로그찾기실패() {
        // given
        Channel channel = createChannel(channelName1, text);
        doReturn(new ArrayList<>())
                .when(chatLogRepository).findLastChatLogsByChannelId(any(String.class));

        // when

        // then
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> chatLogService.findLastChatLogsByChannelId(channel.getId()));

    }

    @Test
    void 인덱스로로그찾기성공() {
        // given
        Channel channel = createChannel(channelName1, text);
        doReturn(logList20())
                .when(chatLogRepository).findChatLogsByChannelId(any(String.class), any(Long.class));

        // when
        List<ChatLog> result = chatLogService.findChatLogsByIndex(channel.getId(), idx);

        // then
        assertThat(result.size()).isEqualTo(maxi);
    }

    @Test
    void 인덱스로로그찾기실패() {
        // given
        Channel channel = createChannel(channelName1, text);
        doReturn(EmptyList())
                .when(chatLogRepository).findChatLogsByChannelId(any(String.class), any(Long.class));

        // when
        List<ChatLog> result = chatLogService.findChatLogsByIndex(channel.getId(), idx);

        // then
        assertThat(result).isEmpty();
    }

    private List<ChatLog> logList20() {
        List<ChatLog> chatLogs = new ArrayList<>();
        for(int i=1; i<=maxi; i++) {
            ChatLog chatLog = ChatLog.builder()
                    .type(type)
                    .message("테스트")
                    .senderNickname(nickname1)
                    .senderEmail(email1)
                    .build();
            chatLog.setChatLogIdx((long) i);
            chatLogs.add(chatLog);
        }
        return chatLogs;
    }

    private List<ChatLog> EmptyList() {
        return new ArrayList<>();
    }

    private List<ChatLog> createLastChatLog() {
        ChatLog chatLog = createChatLog();
        chatLog.setChatLogIdx(lastIndex);
        return List.of(chatLog);
    }

    private ChatLog createChatLog() {
        return ChatLog.builder()
                .type(type)
                .message("테스트")
                .senderNickname(nickname1)
                .senderEmail(email1)
                .build();
    }
    private Channel createChannel(String name, ChannelType type) {
        return Channel.builder()
                .channelName(name)
                .channelType(type)
                .build();
    }

}
