package webrtc.openvidu.repository.channel;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;
import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.domain.ChannelUser;
import webrtc.openvidu.domain.User;
import webrtc.openvidu.exception.ChannelUserException.NotExistChannelUserException;
import webrtc.openvidu.repository.user.UserRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class ChannelUserRepositoryImplTest {

    @Autowired
    private ChannelUserRepository channelUserRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("ChannelUser 저장 O && (channelId + userId) 조회 O")
    public void channelUser_saveO_findO() {
        //given
        Channel channel = new Channel("TestChannel");
        User user = new User("user", "user", "email1");
        ChannelUser channelUser = new ChannelUser();

        //when

        channelUser.setChannel(channel);
        channelUser.setUser(user);
        channelRepository.save(channel);
        userRepository.saveUser(user);
        channelUserRepository.save(channelUser);

        ChannelUser findChannelUser = channelUserRepository.findOneChannelUser(channelUser.getChannel().getId(), channelUser.getUser().getId());

        //then
        Assertions.assertThat(findChannelUser.getChannel().getId()).isEqualTo(channel.getId());
        Assertions.assertThat(findChannelUser.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("ChannelUser 저장 O && (channelId + userId) 조회 X")
    public void channelUser_saveO_findX() {
        //given
        Channel channel = new Channel("TestChannel");
        User user = new User("user", "user", "email1");
        ChannelUser channelUser = new ChannelUser();

        //when

        channelUser.setChannel(channel);
        channelUser.setUser(user);
        channelRepository.save(channel);
        userRepository.saveUser(user);
        channelUserRepository.save(channelUser);


        //then
        assertThrows(NotExistChannelUserException.class,
                () -> channelUserRepository.findOneChannelUser(channel.getId(), "NotEXIST"));
    }

    @Test
    @DisplayName("ChannelUser 저장 O && 삭제 O")
    public void channelUser_saveO_delO() {
        //given
        Channel channel = new Channel("TestChannel");
        User user = new User("user", "user", "email1");
        ChannelUser channelUser = new ChannelUser();

        //when
        channelUser.setChannel(channel);
        channelUser.setUser(user);
        channelRepository.save(channel);
        userRepository.saveUser(user);
        channelUserRepository.save(channelUser);

        ChannelUser findChannelUser = channelUserRepository.findOneChannelUser(channelUser.getChannel().getId(), channelUser.getUser().getId());

        /* then
        channelUser 조회 -> channelUser 삭제 -> channelUser 조회 (Exception 발생)
         */
        Assertions.assertThat(findChannelUser.getChannel().getId()).isEqualTo(channel.getId());
        Assertions.assertThat(findChannelUser.getUser().getId()).isEqualTo(user.getId());

        channelUserRepository.delete(channelUser);
        assertThrows(NotExistChannelUserException.class,
                () -> channelUserRepository.findOneChannelUser(channel.getId(), "NotEXIST"));
    }

    @Test
    @DisplayName("ChannelUser 저장 X && 삭제 X")
    public void channelUser_saveX_delX() {
        //given
        ChannelUser channelUser = new ChannelUser();


        //when


        // then

        assertThrows(InvalidDataAccessApiUsageException.class,
                ()-> {
                    channelUserRepository.delete(channelUser);
                });
    }
}
