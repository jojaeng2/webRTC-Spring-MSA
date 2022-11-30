package webrtc.v1.repository.hashtag;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import webrtc.v1.domain.HashTag;


import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class HashTagRepositoryImplTest {

    @Autowired
    private HashTagRepository repository;


    String tag1 = "tag1";

    @Test
    void 해시태그저장() {
        // given
        HashTag hashTag = createHashTag(tag1);

        // when
        HashTag createHashTag = repository.save(hashTag);

        // then
        assertThat(createHashTag.getId()).isEqualTo(hashTag.getId());
    }

    @Test
    void findById성공() {

        // given
        HashTag hashTag = createHashTag(tag1);
        repository.save(hashTag);

        // when
        Optional<HashTag> OpHashTag = repository.findById(hashTag.getId());

        // then
        assertThat(OpHashTag.isPresent()).isTrue();
    }

    @Test
    void findById실패() {
        // given
        HashTag hashTag = createHashTag(tag1);

        // when
        Optional<HashTag> OpHashTag = repository.findById(hashTag.getId());

        // then
        assertThat(OpHashTag.isPresent()).isFalse();
    }

    @Test
    void 해시태그이름으로찾기성공() {
        // given
        HashTag hashTag = createHashTag(tag1);
        repository.save(hashTag);

        // when
        Optional<HashTag> findHashTag = repository.findByTagName(hashTag.getTagName());

        // then
        assertThat(findHashTag.get().getTagName()).isEqualTo(hashTag.getTagName());
    }

    @Test
    void 해시태그이름으로찾기실패() {
        // given
        HashTag hashTag = createHashTag(tag1);

        // when
        Optional<HashTag> findHashTag = repository.findByTagName(hashTag.getTagName());

        // then
        assertThrows(NoSuchElementException.class, findHashTag::get);
    }

    private HashTag createHashTag(String name) {
        return HashTag.builder()
                .tagName(name)
                .build();
    }
}
