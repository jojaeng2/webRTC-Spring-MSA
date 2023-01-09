package webrtc.v1.chat.entity;

import static webrtc.v1.chat.enums.ChannelCreate.EMAIL;
import static webrtc.v1.chat.enums.ChannelCreate.MESSAGE;
import static webrtc.v1.chat.enums.ChannelCreate.NICKNAME;
import static webrtc.v1.chat.enums.ChannelCreate.NOTICE;
import static webrtc.v1.chat.enums.ClientMessageType.CREATE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.chat.enums.ClientMessageType;
import webrtc.v1.user.entity.Users;

@Getter
@Builder
@RedisHash("chatLog")
public class ChatLog implements Serializable {

  @JsonIgnore
  @Builder.Default
  private String id = UUID.randomUUID().toString();

  @Builder.Default
  private int idx = 0;

  @Enumerated(EnumType.STRING)
  private ClientMessageType type;
  private String message;
  private String senderNickname;
  private String senderEmail;

  @Builder.Default
  private Timestamp sendTime = new Timestamp(System.currentTimeMillis());

  public static ChatLog createChannelLog(Channel channel, Users user) {
    ChatLog chatLog = ChatLog.builder()
        .type(CREATE)
        .message(NOTICE.getMessage() + user.getNickname() + MESSAGE.getMessage())
        .senderNickname(NICKNAME.getMessage())
        .senderEmail(EMAIL.getMessage())
        .build();
    channel.setLatestLog(chatLog);
    return chatLog;
  }

  public static ChatLog createChatLog(ClientMessageType type, String message, Channel channel, Users user) {
    ChatLog chatLog = ChatLog.builder()
        .type(type)
        .message(message)
        .senderNickname(user.getNickname())
        .senderEmail(user.getEmail())
        .build();
    channel.setLatestLog(chatLog);
    return chatLog;
  }

  public void setChatLogIdx(int idx) {
    this.idx = idx;
  }
}
