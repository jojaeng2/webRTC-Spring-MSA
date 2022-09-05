package webrtc.chatservice.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import webrtc.chatservice.domain.ChatLog;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FindChatLogsResponse {
    private List<ChatLog> logs;
}
