package webrtc.chatservice.service.channel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.HashTag;
import webrtc.chatservice.repository.hashtag.HashTagRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class HashTagServiceImplTest {

    @Autowired
    private HashTagService hashTagService;
    @Autowired
    private HashTagRepository hashTagRepository;

    @Test
    @DisplayName("TagName으로 HashTag 찾기 성공")
    public void findHashTagByNameO() {
        // given
        HashTag hashTag = new HashTag("TestTagName");

        // when
        hashTagRepository.save(hashTag);
        List<HashTag> hashTags = hashTagService.findHashTagByName("TestTagName");

        // then
        assertThat(hashTag).isEqualTo(hashTags.get(0));

    }

    @Test
    @DisplayName("TagName으로 HashTag 찾기 실패")
    public void findHashTagByNameX() {
        // given

        // when
        List<HashTag> hashTags = hashTagService.findHashTagByName("TestTagName");

        // then
        assertThat(hashTags.size()).isEqualTo(0);

    }
}
