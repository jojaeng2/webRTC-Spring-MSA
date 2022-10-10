package webrtc.chatservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@AllArgsConstructor
@Builder
public class Users implements Serializable {

    @Id
    @Column(name = "user_id")
    @Builder.Default
    private String id = UUID.randomUUID().toString();
    private String email;
    @JsonIgnore
    private String password;
    private String nickname;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    @Builder.Default
    private List<ChannelUser> channelUsers = new ArrayList<>();

}
