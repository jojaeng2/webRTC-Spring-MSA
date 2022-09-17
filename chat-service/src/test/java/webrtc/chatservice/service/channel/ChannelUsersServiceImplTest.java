package webrtc.chatservice.service.channel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChannelUser;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.exception.ChannelUserException.NotExistChannelUserException;
import webrtc.chatservice.repository.channel.ChannelUserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static webrtc.chatservice.enums.ChannelType.TEXT;

@ExtendWith(MockitoExtension.class)
public class ChannelUsersServiceImplTest {

    @InjectMocks
    private ChannelUserServiceImpl channelUserService;
    @Mock private ChannelUserRepository channelUserRepository;

    String nickname1 = "nickname1";
    String nickname2 = "nickname2";
    String password = "password";
    String email1 = "email1";
    String email2 = "email2";
    String channelName1 = "channelName1";
    String tag1 = "tag1";
    String tag2 = "tag2";
    String tag3 = "tag3";
    ChannelType text = TEXT;

    @Test
    @Transactional
    public void 채널유저_조회성공() {
        // given
        Channel channel = new Channel(channelName1, text);
        Users users = new Users(nickname1, password, email1);
        doReturn(Optional.of(new ChannelUser(users, channel)))
                .when(channelUserRepository)
                .findOneChannelUser(channel.getId(), users.getId());

        // when
        ChannelUser channelUser = channelUserService.findOneChannelUser(channel.getId(), users.getId());

        // then
        assertThat(channelUser.getChannel().getId()).isEqualTo(channel.getId());
        assertThat(channelUser.getUser().getId()).isEqualTo(users.getId());
    }

    @Test
    @Transactional
    public void 채널유저_조회실패() {
        // given
        Channel channel = new Channel(channelName1, text);
        Users users = new Users(nickname1, password, email1);

        doThrow(new NotExistChannelUserException())
                .when(channelUserRepository)
                .findOneChannelUser(channel.getId(), users.getId());

        // when

        // then
        Assertions.assertThrows(NotExistChannelUserException.class, ()-> {
           channelUserRepository.findOneChannelUser(channel.getId(), users.getId());
        });
    }

}
