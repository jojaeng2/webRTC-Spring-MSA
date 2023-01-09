package webrtc.v1.chat.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import webrtc.v1.chat.entity.ChatLog;

@Getter
@AllArgsConstructor
public class FindChatLogsResponse {

  private final List<ChatLog> logs;
}
