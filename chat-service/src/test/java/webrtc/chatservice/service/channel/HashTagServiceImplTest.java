package webrtc.chatservice.service.channel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.HashTag;
import webrtc.chatservice.exception.HashTagException;
import webrtc.chatservice.exception.HashTagException.NotExistHashTagException;
import webrtc.chatservice.repository.hashtag.HashTagRepository;
import webrtc.chatservice.service.user.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class HashTagServiceImplTest {

    @Autowired
    private HashTagService hashTagService;
    @Autowired
    private HashTagRepository hashTagRepository;

    String tagName = "hashTag1";


    @Test
    public void HashTag_이름으로_조회성공() {
        // given
        HashTag hashTag = new HashTag(tagName);

        // when
        hashTagRepository.save(hashTag);
        HashTag findHashTag = hashTagService.findHashTagByName(tagName);

        // then
        assertThat(findHashTag).isEqualTo(hashTag);

    }

    @Test
    @Transactional
    public void HashTag_이름으로_조회실패() {
        // given

        // when

        // then
        Assertions.assertThrows(NotExistHashTagException.class, ()->{
            hashTagService.findHashTagByName(tagName);
        });

    }
}
