package webrtc.v1.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import webrtc.v1.chat.enums.ClientMessageType;

public class ChatDto {

  private ChatDto() {
  }

  @Getter
  @AllArgsConstructor
  public static class SendChatDto {

    private final ClientMessageType type;
    private final String channelId;
    private final String message;
    private final String userId;
  }
}
