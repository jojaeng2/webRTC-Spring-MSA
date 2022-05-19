package webrtc.openvidu.controller.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.openvidu.domain.ChatLog;
import webrtc.openvidu.dto.ChatDto;
import webrtc.openvidu.dto.ChatDto.FindChatLogsResponse;
import webrtc.openvidu.service.chat.ChatService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/webrtc")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ChatApiController {

    private final ChatService chatService;

    @GetMapping("/channel/{id}/{idx}")
    public ResponseEntity<?> findChatLogs(@PathVariable("id") String channelId, @PathVariable("idx") String idx) {
        FindChatLogsResponse response = new FindChatLogsResponse(chatService.findTenChatLogsByIndex(channelId, Integer.parseInt(idx)));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
