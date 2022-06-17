package webrtc.chatservice.repository.hashtag;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.HashTag;

import java.util.List;

@SpringBootTest
@Transactional
public class HashTagRepositoryImplTest {

    @Autowired
    private HashTagRepositoryImpl hashTagRepository;

    @Test
    @DisplayName("HashTag 저장")
    public void saveHashTag() {
        //given
        HashTag hashTag = new HashTag("testTag");

        //when
        hashTagRepository.save(hashTag);

        //then
    }

    @Test
    @DisplayName("HashTag 저장 O && HashTagId 조회 O")
    public void hashTag_saveO_findByHashTagIdO() {
        //given
        HashTag hashTag = new HashTag("testTag");

        //when
        hashTagRepository.save(hashTag);
        HashTag findHashTag = hashTagRepository.findHashTagById(hashTag.getId());

        //then
        Assertions.assertThat(findHashTag).isEqualTo(hashTag);
    }

    @Test
    @DisplayName("HashTag 저장 O && HashTagName 조회 O")
    public void hashTag_saveO_findByHashTagNameIdO() {
        //given
        HashTag hashTag = new HashTag("testTag");

        //when
        hashTagRepository.save(hashTag);
        List<HashTag> findHashTags = hashTagRepository.findHashTagByName(hashTag.getTagName());

        //then
        Assertions.assertThat(findHashTags.get(0)).isEqualTo(hashTag);
    }

    @Test
    @DisplayName("HashTag 저장 X && HashTagName 조회 X")
    public void hashTag_saveX_findByHashTagNameIdX() {
        //given
        HashTag hashTag = new HashTag("testTag");

        //when
        List<HashTag> findHashTags = hashTagRepository.findHashTagByName(hashTag.getTagName());

        //then
        Assertions.assertThat(findHashTags.isEmpty()).isEqualTo(true);
    }
}
