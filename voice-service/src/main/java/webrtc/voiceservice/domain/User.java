package webrtc.voiceservice.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class User implements Serializable {

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

    public User(String nickname, String password, String email) {
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
}
