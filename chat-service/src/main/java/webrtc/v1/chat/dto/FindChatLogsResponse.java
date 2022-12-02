package webrtc.v1.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import webrtc.v1.chat.entity.ChatLog;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FindChatLogsResponse {
    private List<ChatLog> logs;
}
