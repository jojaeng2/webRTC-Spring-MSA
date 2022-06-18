package webrtc.voiceservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor
public class User implements Serializable {

    @Id
    @Column(name = "user_id")
    private String id;
    private String email;
    @JsonIgnore
    private String password;
    private String nickname;

}
