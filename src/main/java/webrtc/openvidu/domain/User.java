package webrtc.openvidu.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class User implements Serializable {

    @Id
    @Column(name = "user_id")
    @JsonIgnore
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

    @OneToOne
    private Point point;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonIgnore
    private Set<ChannelUser> channelUsers = new HashSet<>();

    public User(String nickname, String password) {
        this.id = UUID.randomUUID().toString();

        this.nickname = nickname;
        this.password = password;

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
        channelUser.setUser(this);
    }

    public void removeChannelUser(ChannelUser channelUser) {
        this.channelUsers.remove(channelUser);
    }
}
