package webrtc.v1.chat.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webrtc.v1.chat.dto.FindChatLogsResponse;
import webrtc.v1.chat.entity.ChatLog;
import webrtc.v1.chat.service.ChatLogService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/webrtc/chat")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ChatLogApiController {

  private final ChatLogService chatLogService;

  @GetMapping("/channel/{channelId}/{idx}")
  public ResponseEntity<FindChatLogsResponse> findChatLogs(
      @PathVariable("channelId") String channelId,
      @PathVariable("idx") String idx
  ) {
    List<ChatLog> chatLogs = chatLogService.findChatLogsByIndex(channelId, Integer.parseInt(idx));
    return new ResponseEntity<>(new FindChatLogsResponse(chatLogs), HttpStatus.OK);
  }
}
