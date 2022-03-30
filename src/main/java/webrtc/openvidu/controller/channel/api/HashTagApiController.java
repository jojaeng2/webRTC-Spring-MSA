package webrtc.openvidu.controller.channel.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webrtc.openvidu.dto.hashtag.HashTagRequest;
import webrtc.openvidu.dto.hashtag.HashTagResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/webrtc")
public class HashTagApiController {

    @PostMapping("/hashtag/{tagname}")
    public ResponseEntity<HashTagResponse> searchHashTag(@RequestBody HashTagRequest request) {

    }
}
