package webrtc.v1.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import webrtc.v1.domain.ChatLog;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FindChatLogsResponse {
    private List<ChatLog> logs;
}
