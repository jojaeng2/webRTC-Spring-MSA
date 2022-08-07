package webrtc.chatservice.repository.hashtag;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import webrtc.chatservice.domain.HashTag;
import webrtc.chatservice.exception.HashTagException.NotExistHashTagException;


import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({
        HashTagRepositoryImpl.class
})
public class HashTagRepositoryImplTest {

    @Autowired
    private HashTagRepository hashTagRepository;


    String tag1 = "tag1";
    String tag2 = "tag2";
    String tag3 = "tag3";


    @Test
    public void 해시태그_저장성공() {
        //given
        HashTag hashTag = new HashTag(tag1);

        //when
        hashTagRepository.save(hashTag);

        //then
    }

    @Test
    public void 해시태그_저장성공_AND_해시태그이름조회_성공() {
        //given
        HashTag hashTag = new HashTag(tag1);
        hashTagRepository.save(hashTag);

        //when
        HashTag findHashTag = hashTagRepository.findHashTagByName(hashTag.getTagName());

        //then
        Assertions.assertThat(findHashTag).isEqualTo(hashTag);
    }

    @Test
    public void 해시태그_저장성공_AND_해시태그이름조회_실패() {
        //given
        HashTag hashTag = new HashTag(tag1);

        //when

        //then
        assertThrows(NotExistHashTagException.class, ()->{
            hashTagRepository.findHashTagByName(hashTag.getTagName());
        });
    }
}
