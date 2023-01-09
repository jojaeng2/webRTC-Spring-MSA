package webrtc.v1.staticgenarator;

import webrtc.v1.chat.entity.ChatLog;
import webrtc.v1.chat.enums.ClientMessageType;

public class ChatLogGenerator {

  private static final ClientMessageType type = ClientMessageType.CHAT;
  private static final String message ="MESSAGE";
  private static final String senderNickname = "jojaeng2";
  private static final String senderEmail = "ds4ouj@naver.com";

  private ChatLogGenerator() {}

  public static ChatLog createChatLog() {
    return ChatLog.builder()
        .message(message)
        .type(type)
        .senderEmail(senderEmail)
        .senderNickname(senderNickname)
        .build();
  }
}
