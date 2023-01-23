package webrtc.v1.utils.logstash;

public class LogStashDto {

  public static class CreateChannelLog {

    private final String user_id;
    private final String channel_id;
    private final String channel_name;
    private final String message;

    public CreateChannelLog(String user_id, String channel_id, String channel_name,
        String message) {
      this.user_id = user_id;
      this.channel_id = channel_id;
      this.channel_name = channel_name;
      this.message = message;
    }

    @Override
    public String toString() {
      return "{" +
          "user_id:'" + user_id + '\'' +
          ", channel_id:'" + channel_id + '\'' +
          ", channel_name:'" + channel_name + '\'' +
          ", message:'" + message + '\'' +
          '}';
    }
  }
}
