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
    private Timestamp created_at;
    private Timestamp updated_at;
    private String email;
    @JsonIgnore
    private String password;
    private Date birthdate;
    private String phone_number;
    private String school;
    private String company;
    private String nickname;
    private Timestamp nickname_expire_at;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonIgnore
    private Set<ChannelUser> channelUsers = new HashSet<>();

    public Users(String nickname, String password, String email) {
        this.id = UUID.randomUUID().toString();
        this.nickname = nickname;
        this.password = password;
        this.email = email;

        // 임시
        this.created_at = null;
        this.updated_at = null;
        this.birthdate = null;
        this.phone_number = null;
        this.school = null;
        this.company = null;
        this.nickname_expire_at = null;

    }

    public void addChannelUser(ChannelUser channelUser) {
        this.channelUsers.add(channelUser);
    }

    public void removeChannelUser(ChannelUser channelUser) {
        this.channelUsers.remove(channelUser);
    }
}
