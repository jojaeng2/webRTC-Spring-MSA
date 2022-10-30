package webrtc.chatservice.dto.logstash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.enums.ChannelType;

import static webrtc.chatservice.enums.ChannelType.VOIP;

@Getter
@NoArgsConstructor
public class LogForCreateChannel {


    private Information information;
    private Msg msg;

    public LogForCreateChannel(String client_ip, String client_host, String method, String user_agent,
                               String user_id, String channel_id, String channel_name, ChannelType channel_type) {

        this.information = new Information(client_ip, client_host, method, user_agent, channel_type);
        this.msg = new Msg(user_id, channel_id, channel_name, channel_type);
    }

    @Getter
    @NoArgsConstructor
    class Information {
        private String client_ip;
        private String client_host;
        private String level;
        private String logger_name;
        private String method;
        private String user_agent;

        public Information(String client_ip, String client_host, String method, String user_agent, ChannelType channelType) {
            this.client_ip = client_ip;
            this.client_host = client_host;
            this.logger_name = setLoggerName(channelType);
            this.method = method;
            this.user_agent = user_agent;
        }



        private String setLoggerName(ChannelType channelType) {
            if(channelType.equals(VOIP)) {
                return "VOICE-Log";
            } else {
                return "TEXT-Log";
            }
        }
    }

    @Getter
    @NoArgsConstructor
    class Msg {
        private String user_id;
        private String channel_id;
        private String channel_name;
        private ChannelType channel_type;
        private String message;

        public Msg(String user_id, String channel_id, String channel_name, ChannelType channel_type) {
            this.user_id = user_id;
            this.channel_id = channel_id;
            this.channel_name = channel_name;
            this.channel_type = channel_type;
            this.message = setMessage(channel_type);
        }

         String setMessage(ChannelType channelType) {
            if(channelType.equals(VOIP)) {
                return "VOICE CHANNEL CREATE";
            } else {
                return "TEXT CHANNEL CREATE";
            }
        }
    }
}
