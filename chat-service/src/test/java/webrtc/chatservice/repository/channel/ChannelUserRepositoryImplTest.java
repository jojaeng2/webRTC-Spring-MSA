package webrtc.chatservice.repository.channel;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChannelUser;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.exception.ChannelUserException;
import webrtc.chatservice.exception.ChannelUserException.NotExistChannelUserException;
import webrtc.chatservice.repository.user.UserRepository;
import webrtc.chatservice.repository.user.UserRepositoryImpl;
import webrtc.chatservice.service.user.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static webrtc.chatservice.enums.ChannelType.TEXT;
import static webrtc.chatservice.enums.ChannelType.VOIP;


@DataJpaTest
@Import({
        ChannelUserRepositoryImpl.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ChannelUserRepositoryImplTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private ChannelUserRepository channelUserRepository;

    String nickname1 = "nickname1";
    String password = "password";
    String email1 = "email1";
    String channelName1 = "channelName1";
    ChannelType text = TEXT;



    @Test
    @Transactional
    public void 채널유저_저장성공() {
        // given
        Channel channel = new Channel(channelName1, text);
        User user = new User(nickname1, password, email1);
        ChannelUser channelUser = new ChannelUser(user, channel);

        // when
        em.persist(channel);

        // then

    }
    @Test
    @Transactional
    public void 채널유저_저장후_채널ID_AND_유저ID로_조회_성공() {
        //given
        Channel channel = new Channel(channelName1, text);
        User user = new User(nickname1, password, email1);
        ChannelUser channelUser = new ChannelUser(user, channel);
        em.persist(channel);
        em.persist(user);

        //when

        ChannelUser findChannelUser = channelUserRepository.findOneChannelUser(channel.getId(), user.getId());

        //then
        assertThat(findChannelUser).isEqualTo(channelUser);
    }

    @Test
    @Transactional
    public void 채널유저_저장후_채널ID_AND_유저ID로_조회_실패() {
        //given
        Channel channel = new Channel(channelName1, text);
        User user = new User(nickname1, password, email1);
        ChannelUser channelUser = new ChannelUser(user, channel);

        //when

        //then
        assertThrows(NotExistChannelUserException.class, () -> {
            channelUserRepository.findOneChannelUser(channel.getId(), "notExist");
        });
    }
}
