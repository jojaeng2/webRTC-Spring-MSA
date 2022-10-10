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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
    void 이름으로조회성공() {
        // given
        doReturn(Optional.of(HashTag.builder().tagName(tagName).build()))
                .when(hashTagRepository).findByTagName(any(String.class));

        // when

        HashTag findHashTag = hashTagService.findHashTagByName(tagName);

        // then
        assertThat(findHashTag.getTagName()).isEqualTo(tagName);

    }

    @Test
    void 이름으로조회실패() {
        // given
        doReturn(Optional.empty())
                .when(hashTagRepository).findByTagName(any(String.class));

        // when

        // then
        assertThrows(NotExistHashTagException.class, ()->{
            hashTagService.findHashTagByName(tagName);
        });

    }
}
