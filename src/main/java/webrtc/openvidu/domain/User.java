package webrtc.openvidu.domain;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class User implements Serializable {
    private Long userId;
    private String userName;
}
