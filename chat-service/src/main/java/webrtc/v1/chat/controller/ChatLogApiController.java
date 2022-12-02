package webrtc.v1.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.v1.chat.entity.ChatLog;
import webrtc.v1.chat.dto.FindChatLogsResponse;
import webrtc.v1.chat.service.ChatLogService;

import java.util.List;

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
        List<ChatLog> chatLogs = chatLogService.findChatLogsByIndex(channelId, Long.parseLong(idx));
        return new ResponseEntity<>(new FindChatLogsResponse(chatLogs), HttpStatus.OK);
    }
}
