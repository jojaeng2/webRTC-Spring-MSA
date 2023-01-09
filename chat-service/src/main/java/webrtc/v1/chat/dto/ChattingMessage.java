package webrtc.v1.chat.dto;

import java.sql.Timestamp;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import webrtc.v1.chat.enums.SocketServerMessageType;
import webrtc.v1.user.entity.Users;

@Getter
@Builder
public class ChattingMessage {

  private String channelId;
  private SocketServerMessageType type;
  private String senderName;
  private String chatMessage;
  private int currentParticipants;
  private List<Users> users;
  private long logId;
  private String senderEmail;

  @Builder.Default
  private Timestamp sendTime = new Timestamp(System.currentTimeMillis());

  public ChattingMessage setType(SocketServerMessageType type) {
    this.type = type;
    return this;
  }
}
