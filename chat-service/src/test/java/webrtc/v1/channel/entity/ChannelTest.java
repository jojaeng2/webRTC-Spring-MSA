package webrtc.v1.channel.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static webrtc.v1.channel.enums.ChannelType.TEXT;

import java.sql.Timestamp;
import java.util.List;
import org.junit.jupiter.api.Test;
import webrtc.v1.channel.enums.ChannelType;
import webrtc.v1.chat.entity.ChatLog;
import webrtc.v1.staticgenarator.ChannelGenerator;
import webrtc.v1.staticgenarator.ChannelHashTagGenerator;
import webrtc.v1.staticgenarator.ChannelUserGenerator;
import webrtc.v1.staticgenarator.ChatLogGenerator;

public class ChannelTest {

  @Test
  void 채널ID조회성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();

    // when
    String id = channel.getId();

    // then
    assertThat(channel.getId()).isEqualTo(id);
  }

  @Test
  void 채널이름조회성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();

    // when
    String name = channel.getChannelName();

    // then
    assertThat(ChannelGenerator.getName()).isEqualTo(name);
  }

  @Test
  void 채널최대인원조회성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();

    // when
    int participants = channel.getLimitParticipants();

    // then
    assertThat(participants).isEqualTo(15);
  }

  @Test
  void 채널현재조회성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();

    // when
    int participants = channel.getCurrentParticipants();

    // then
    assertThat(participants).isEqualTo(0);
  }

  @Test
  void 채널수명조회성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();

    // when
    long ttl = channel.getTimeToLive();

    // then
    assertThat(ttl).isEqualTo(60L * 60L);
  }

  @Test
  void 채널타입조회성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();

    // when
    ChannelType channelType = channel.getChannelType();

    // then
    assertThat(channelType).isEqualTo(TEXT);
  }


  @Test
  void 채널마지막채팅시간조회성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();

    // when
    Timestamp latestLog = channel.getLatestLog();

    // then
    assertThat(latestLog).isEqualTo(channel.getLatestLog());
  }

  @Test
  void 채널유저조회성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();

    // when
    List<ChannelUser> channelUsers = channel.getChannelUsers();

    // then
    assertThat(channelUsers).isEqualTo(channel.getChannelUsers());
  }

  @Test
  void 채널해시태그조회성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();

    // when
    List<ChannelHashTag> channelHashTags = channel.getChannelHashTags();

    // then
    assertThat(channelHashTags).isEqualTo(channel.getChannelHashTags());
  }

  @Test
  void 채널로그조회성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();

    // when
    List<ChatLog> chatLogs = channel.getChatLogs();

    // then
    assertThat(chatLogs).isEqualTo(channel.getChatLogs());
  }

  @Test
  void 채널입장성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();
    ChannelUser channelUser = ChannelUserGenerator.createChannelUser();

    // when
    channel.enterChannelUser(channelUser);

    // then
    assertThat(channel.getCurrentParticipants()).isEqualTo(1);
  }

  @Test
  void 채널퇴장성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();
    ChannelUser channelUser = ChannelUserGenerator.createChannelUser();
    channel.enterChannelUser(channelUser);

    // when
    channel.exitChannelUser(channelUser);

    // then
    assertThat(channel.getCurrentParticipants()).isEqualTo(0);
  }

  @Test
  void 채널수명설정성공() {
    // given
    long ttl = 10L;
    Channel channel = ChannelGenerator.createTextChannel();

    // when
    channel.setTimeToLive(ttl);

    // then
    assertThat(channel.getTimeToLive()).isEqualTo(ttl);
  }

  @Test
  void 채널해시태그추가성공() {
    // given
    ChannelHashTag channelHashTag = ChannelHashTagGenerator.createChannelHashTag();
    Channel channel = channelHashTag.getChannel();

    // when
    channel.addChannelHashTag(channelHashTag);

    // then
    assertThat(channel.getChannelHashTags().get(0)).isEqualTo(channelHashTag);
  }

  @Test
  void 채널인원가득안참() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();

    // when

    // then
    assertThat(channel.isFull()).isFalse();
  }

  @Test
  void 채팅로그추가성공() {
    // given
    ChatLog chatLog = ChatLogGenerator.createChatLog();
    Channel channel = chatLog.getChannel();

    // when
    channel.addChatLog(chatLog);

    // then
    assertThat(channel.getChatLogs().get(0)).isEqualTo(chatLog);
  }

  @Test
  void 현재참가자수변경() {
    // given
    int participants = 15;
    Channel channel = ChannelGenerator.createTextChannel();

    // when
    channel.setCurrentParticipants(participants);

    // then
    assertThat(channel.isFull()).isTrue();
  }
}
