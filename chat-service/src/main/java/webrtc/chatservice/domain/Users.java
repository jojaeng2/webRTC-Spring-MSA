package webrtc.chatservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonIgnore
    private Set<ChannelUser> channelUsers = new HashSet<>();

    public Users(String nickname, String password, String email) {
        this.id = UUID.randomUUID().toString();
        this.nickname = nickname;
        this.password = password;
        this.email = email;

    }

    public void addChannelUser(ChannelUser channelUser) {
        this.channelUsers.add(channelUser);
    }

    public void removeChannelUser(ChannelUser channelUser) {
        this.channelUsers.remove(channelUser);
    }
}
