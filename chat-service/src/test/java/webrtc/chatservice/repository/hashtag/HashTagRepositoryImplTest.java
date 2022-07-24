package webrtc.chatservice.repository.hashtag;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.HashTag;
import webrtc.chatservice.exception.HashTagException;
import webrtc.chatservice.exception.HashTagException.NotExistHashTagException;
import webrtc.chatservice.service.user.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class HashTagRepositoryImplTest {

    @Autowired
    private HashTagRepositoryImpl hashTagRepository;

    @Autowired
    private UserService userService;


    String tag1 = "tag1";
    String tag2 = "tag2";
    String tag3 = "tag3";

    @BeforeEach
    public void clearUserCache() {
        userService.redisDataEvict();
    }


    @Test
    @Transactional
    public void 해시태그_저장성공() {
        //given
        HashTag hashTag = new HashTag(tag1);

        //when
        hashTagRepository.save(hashTag);

        //then
    }

    @Test
    @Transactional
    public void 해시태그_저장성공_AND_조회성공() {
        //given
        HashTag hashTag = new HashTag(tag1);
        hashTagRepository.save(hashTag);

        //when
        HashTag findHashTag = hashTagRepository.findHashTagById(hashTag.getId());

        //then
        Assertions.assertThat(findHashTag).isEqualTo(hashTag);
    }

    @Test
    @Transactional
    public void 해시태그_저장성공_AND_해시태그이름조회_성공() {
        //given
        HashTag hashTag = new HashTag(tag1);

        //when
        hashTagRepository.save(hashTag);
        HashTag findHashTag = hashTagRepository.findHashTagByName(hashTag.getTagName());

        //then
        Assertions.assertThat(findHashTag).isEqualTo(hashTag);
    }

    @Test
    @Transactional
    public void 해시태그_저장성공_AND_해시태그이름조회_실패() {
        //given
        HashTag hashTag = new HashTag("testTag");

        //when

        //then
        assertThrows(NotExistHashTagException.class, ()->{
            hashTagRepository.findHashTagByName(hashTag.getTagName());
        });
    }
}
