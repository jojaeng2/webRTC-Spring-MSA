package webrtc.openvidu.repository.channel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.domain.ChannelHashTag;
import webrtc.openvidu.domain.HashTag;

@SpringBootTest
@Transactional
public class ChannelHashTagRepositoryTest {

    @Autowired
    private ChannelHashTagRepository channelHashTagRepository;

    @Test
    @DisplayName("ChannelHsahTag 저장")
    public void saveChannelHashTag() {

        //given
        Channel channel = new Channel("testChannel");
        HashTag hashTag = new HashTag("testHashTag");
        ChannelHashTag channelHashTag = new ChannelHashTag();
        channelHashTag.CreateChannelHashTag(channel, hashTag);

        //when
        channelHashTagRepository.save(channelHashTag);

        //then

    }
}