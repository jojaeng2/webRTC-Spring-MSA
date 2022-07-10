package webrtc.chatservice.repository.channel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChannelHashTag;
import webrtc.chatservice.domain.HashTag;
import webrtc.chatservice.service.user.UserService;

@SpringBootTest
@Transactional
public class ChannelHashTagRepositoryTest {

    @Autowired
    private ChannelHashTagRepository channelHashTagRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void clearUserCache() {
        userService.redisDataEvict();
    }


    @Test
    @DisplayName("ChannelHsahTag 저장")
    public void saveChannelHashTag() {

        //given
        Channel channel = new Channel("testChannel", false);
        HashTag hashTag = new HashTag("testHashTag");
        ChannelHashTag channelHashTag = new ChannelHashTag();
        channelHashTag.CreateChannelHashTag(channel, hashTag);

        //when
        channelHashTagRepository.save(channelHashTag);

        //then

    }
}
