package webrtc.chatservice.service.channel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.HashTag;
import webrtc.chatservice.exception.HashTagException.NotExistHashTagException;
import webrtc.chatservice.repository.hashtag.HashTagRepository;
import webrtc.chatservice.service.hashtag.HashTagServiceImpl;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class HashTagServiceImplTest {

    @InjectMocks
    private HashTagServiceImpl hashTagService;
    @Mock
    private HashTagRepository hashTagRepository;

    String tagName = "hashTag1";


    @Test
    public void HashTag_이름으로_조회성공() {
        // given
        HashTag hashTag = new HashTag(tagName);
        doReturn(Optional.of(new HashTag(tagName)))
                .when(hashTagRepository).findHashTagByName(tagName);

        // when

        HashTag findHashTag = hashTagService.findHashTagByName(tagName);

        // then
        assertThat(findHashTag.getTagName()).isEqualTo(hashTag.getTagName());

    }

    @Test
    @Transactional
    public void HashTag_이름으로_조회실패() {
        // given
        doThrow(new NotExistHashTagException())
                .when(hashTagRepository).findHashTagByName(tagName);

        // when

        // then
        Assertions.assertThrows(NotExistHashTagException.class, ()->{
            hashTagService.findHashTagByName(tagName);
        });

    }
}
