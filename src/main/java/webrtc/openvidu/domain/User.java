package webrtc.openvidu.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class User implements Serializable {

    @Id
    private String id;
    private Timestamp created_at;
    private Timestamp updated_at;
    private String email;
    private String password;
    private Date birthdate;
    private String phone_number;
    private String school;
    private String company;
    private String nickname;
    private Timestamp nickname_expire_at;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonIgnore
    private List<ChannelUser> channelUsers;

    public User(String password, String nickname) {
        this.id = UUID.randomUUID().toString();
        this.password = password;
        this.nickname = nickname;

        // 임시
        this.created_at = null;
        this.updated_at = null;
        this.email = null;
        this.birthdate = null;
        this.phone_number = null;
        this.school = null;
        this.company = null;
        this.nickname_expire_at = null;
    }

    public void addChannelUser(ChannelUser channelUser) {
        this.channelUsers.add(channelUser);
    }
}
