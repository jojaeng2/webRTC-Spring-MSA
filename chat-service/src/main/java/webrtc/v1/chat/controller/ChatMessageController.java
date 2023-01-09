package webrtc.v1.chat.controller;

import static webrtc.v1.chat.enums.ClientMessageType.CHAT;
import static webrtc.v1.chat.enums.ClientMessageType.ENTER;
import static webrtc.v1.chat.enums.ClientMessageType.EXIT;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import webrtc.v1.chat.dto.ChatDto.SendChatDto;
import webrtc.v1.chat.dto.ClientMessage;
import webrtc.v1.chat.enums.ClientMessageType;
import webrtc.v1.chat.service.ChattingService;
import webrtc.v1.chat.factory.SocketMessageFactory;
import webrtc.v1.user.entity.Users;
import webrtc.v1.user.service.UsersService;
import webrtc.v1.utils.jwt.JwtTokenUtil;


@Slf4j
@RequiredArgsConstructor
@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ChatMessageController {

  private final ChattingService chattingService;
  private final JwtTokenUtil jwtTokenUtil;
  private final UsersService usersService;
  private final SocketMessageFactory socketMessageFactory;

  /**
   * /pub/chat/room 으로 오는 메시지 반환
   */
  @MessageMapping("/chat/room")
  public void message(ClientMessage message, @Header("jwt") String jwtToken,
      @Header("channelId") String channelId, @Header("type") ClientMessageType type) {
    String userId = jwtTokenUtil.getUserIdFromToken(jwtToken);
    Users user = usersService.findOneById(userId);
    if (isEnter(type) || isExit(type) || isChat(type)) {
      socketMessageFactory.execute(type, message, user, channelId);
    }
    chattingService.send(new SendChatDto(type, channelId, message.getMessage(), userId));
  }

  boolean isEnter(ClientMessageType type) {
    return type == ENTER;
  }

  boolean isExit(ClientMessageType type) {
    return type == EXIT;
  }

  boolean isChat(ClientMessageType type) {
    return type == CHAT;
  }
}
