package webrtc.openvidu.domain;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MyData implements Serializable {
    private static final long serialVersionUID = -7353484588260422449L;
    private String studentId;
    private String name;
}