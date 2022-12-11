package webrtc.v1.service.chat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.chat.entity.ChatLog;
import webrtc.v1.chat.repository.ChatLogRedisRepository;
import webrtc.v1.chat.service.ChatLogServiceImpl;
import webrtc.v1.channel.enums.ChannelType;
import webrtc.v1.chat.enums.ClientMessageType;
import webrtc.v1.user.entity.Users;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static webrtc.v1.channel.enums.ChannelType.TEXT;


@ExtendWith(MockitoExtension.class)
public class ChatLogServiceTest {

    @InjectMocks
    private ChatLogServiceImpl chatLogService;
    @Mock
    private ChatLogRedisRepository chatLogRedisRepository;

    String nickname1 = "nickname1";
    String email1 = "email1";
    String channelName1 = "channelName1";
    ChannelType text = TEXT;
    ClientMessageType type = ClientMessageType.CHAT;
    int maxi = 20;
    int idx = 10;
    int lastIndex = 300;

    @Test
    void 채팅로그저장성공빈배열아님() {
        // given
        Channel channel = createChannel(channelName1, text);
        doReturn(2)
                .when(chatLogRedisRepository).findLastIndex(any(String.class));

        // when
        long resultIdx = chatLogService.save(ClientMessageType.CHAT, "test", channel, new Users());

        // then
        assertThat(resultIdx).isEqualTo(2L);

    }

    @Test
    void 인덱스로로그찾기성공() {
        // given
        Channel channel = createChannel(channelName1, text);
        doReturn(logList20())
                .when(chatLogRedisRepository).findByChannelIdAndIndex(any(String.class), any(Integer.class));

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
                .when(chatLogRedisRepository).findByChannelIdAndIndex(any(String.class), any(Integer.class));

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
            chatLog.setChatLogIdx(i);
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
