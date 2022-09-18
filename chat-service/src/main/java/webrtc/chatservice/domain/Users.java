package webrtc.chatservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

@Entity
@Getter
@NoArgsConstructor
@RedisHash("users")
public class Users implements Serializable {

    @Id
    @Column(name = "user_id")
    private String id;
    private String email;
    @JsonIgnore
    private String password;
    private String nickname;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<ChannelUser> channelUsers;

    public Users(String nickname, String password, String email) {
        this.id = UUID.randomUUID().toString();
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.channelUsers = new ArrayList<>();

    }

    public void addChannelUser(ChannelUser channelUser) {
        this.channelUsers.add(channelUser);
    }

    public void removeChannelUser(ChannelUser channelUser) {
        this.channelUsers.remove(channelUser);
    }
}
