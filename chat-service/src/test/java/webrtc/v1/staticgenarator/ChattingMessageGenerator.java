package webrtc.v1.staticgenarator;

import static webrtc.v1.chat.enums.SocketServerMessageType.CHAT;

import java.util.ArrayList;
import java.util.List;
import webrtc.v1.chat.dto.ChattingMessage;
import webrtc.v1.chat.enums.SocketServerMessageType;
import webrtc.v1.user.entity.Users;

public class ChattingMessageGenerator {

  private final static String channelId = "ChannelId#1";
  private final static SocketServerMessageType type = CHAT;
  private final static String senderName = "SenderName#1";
  private final static String message = "Message#1";
  private final static int participants = 10;
  private final static List<Users> users = new ArrayList<>();
  private final static long logId = 10;
  private final static String senderEmail = "ds4ouj@naver.com";

  private ChattingMessageGenerator() {
  }

  public static ChattingMessage createChattingMessage() {
    return ChattingMessage.builder()
        .channelId(channelId)
        .type(type)
        .senderName(senderName)
        .chatMessage(message)
        .currentParticipants(participants)
        .users(users)
        .logId(logId)
        .senderEmail(senderEmail)
        .build();
  }
}
