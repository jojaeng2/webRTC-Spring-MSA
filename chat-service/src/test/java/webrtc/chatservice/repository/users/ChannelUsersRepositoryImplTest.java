package webrtc.chatservice.repository.users;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.*;
import webrtc.chatservice.enums.ChannelType;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static webrtc.chatservice.enums.ChannelType.TEXT;


@DataJpaTest
public class ChannelUsersRepositoryImplTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private ChannelUserRepository repository;

    String nickname1 = "nickname1";
    String password = "password";
    String email1 = "email1";
    String channelName1 = "channelName1";
    ChannelType text = TEXT;



    @Test
    @Transactional
    void 채널유저저장성공() {
        // given
        Channel channel = createChannel(channelName1, text);
        Users users = createUsers(nickname1, password, email1);
        ChannelUser channelUser = ChannelUser.builder()
                .user(users)
                .channel(channel)
                .build();
        channel.enterChannelUser(channelUser);

        // when
        ChannelUser findChannelUser = repository.save(channelUser);

        // then
        assertThat(channelUser.getId()).isEqualTo(findChannelUser.getId());
    }

    @Test
    @Transactional
    void 채널유저조회성공() {
        // given
        Channel channel = createChannel(channelName1, text);
        Users users = createUsers(nickname1, password, email1);
        ChannelUser channelUser = createChannelUser(users, channel);

        em.persist(users);
        em.persist(channel);
        repository.save(channelUser);

        // when
        System.out.println("users = " + users.getId());
        System.out.println("channelUser = " + channelUser.getUser().getId());
        Optional<ChannelUser> OpCU = repository.findByChannelAndUser(channel, users);
        ChannelUser findChannelUser = OpCU.get();

        // then
        assertThat(channelUser.getId()).isEqualTo(findChannelUser.getId());
    }

    @Test
    @Transactional
    void 채널유저조회실패() {
        // given
        Channel channel = createChannel(channelName1, text);
        Users users = createUsers(nickname1, password, email1);

        // when
        Optional<ChannelUser> OpCU = repository.findByChannelAndUser(channel, users);

        // then
        assertThrows(NoSuchElementException.class, () -> OpCU.get());
    }


    private Channel createChannel(String name, ChannelType type) {
        return Channel.builder()
                .channelName(name)
                .channelType(type)
                .build();
    }

    private Users createUsers(String name, String password, String email) {
        return Users.builder()
                .nickname(nickname1)
                .password(password)
                .email(email1)
                .build();
    }

    private ChannelUser createChannelUser(Users user, Channel channel) {
        ChannelUser channelUser = ChannelUser.builder()
                .user(user)
                .channel(channel)
                .build();
        channel.enterChannelUser(channelUser);
        return channelUser;
    }
}
