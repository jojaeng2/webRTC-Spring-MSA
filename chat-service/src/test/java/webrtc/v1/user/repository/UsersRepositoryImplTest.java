package webrtc.v1.user.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.entity.ChannelUser;
import webrtc.v1.user.entity.Users;
import webrtc.v1.channel.enums.ChannelType;
import webrtc.v1.channel.repository.ChannelUserRepository;
import webrtc.v1.user.repository.UsersRepository;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static webrtc.v1.channel.enums.ChannelType.TEXT;
import static webrtc.v1.channel.enums.ChannelType.VOIP;

@DataJpaTest
public class UsersRepositoryImplTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private UsersRepository repository;
    @Autowired
    private ChannelUserRepository channelUserRepository;

    String nickname1 = "nickname1";
    String nickname2 = "nickname2";
    String password = "password";
    String email1 = "email1";
    String email2 = "email2";
    String channelName1 = "channelName1";
    String notExistChannelId = "null";
    String tag1 = "tag1";
    String tag2 = "tag2";
    String tag3 = "tag3";
    ChannelType text = TEXT;
    ChannelType voip = VOIP;

    @Test
    void 유저저장성공() {
        // given
        Users users = createUsers(nickname1, password, email1);


        // when
        Users createUsers2 = repository.save(users);

        // then
        assertThat(users.getId()).isEqualTo(createUsers2.getId());
    }

    @Test
    void findById성공() {
        //given
        Users users = createUsers(nickname1, password, email1);
        repository.save(users);

        //when
        Optional<Users> findUsers = repository.findById(users.getId());

        //then
        assertThat(findUsers.isPresent()).isTrue();
    }

    @Test
    void findById실패() {
        //given
        Users users = createUsers(nickname1, password, email1);

        //when
        Optional<Users> findUsers = repository.findById(users.getId());

        //then
        assertThat(findUsers.isPresent()).isFalse();
    }

    @Test
    void 채널ID로유저찾기성공() {
        //given
        Users users = Users.builder()
                .nickname(nickname1)
                .password(password)
                .email(email1)
                .build();
        Channel channel = createChannel(channelName1, text);
        ChannelUser channelUser = ChannelUser.builder()
                .user(users)
                .channel(channel)
                .build();
        channel.enterChannelUser(channelUser);
        em.persist(users);
        em.persist(channel);
        em.persist(channelUser);

        //when

        List<Users> findUsers = channelUserRepository.findByChannel(channel)
                .stream().map(ChannelUser::getUser)
                        .collect(toList());

//        //then
        assertThat(findUsers.get(0).getId()).isEqualTo(users.getId());
    }

    @Test
    void 채널ID로유저찾기실패() {
        //given
        Users users = Users.builder()
                .nickname(nickname1)
                .password(password)
                .email(email1)
                .build();
        Channel channel = createChannel(channelName1, text);
        repository.save(users);
        em.persist(channel);

        //when

        List<Users> findUsers = channelUserRepository.findByChannel(channel).stream()
                .map(ChannelUser::getUser)
                .collect(toList());

        //then
        assertThat(findUsers.isEmpty()).isTrue();
    }



    private Users createUsers(String name, String password, String email) {
        return Users.builder()
                .nickname(nickname1)
                .password(password)
                .email(email1)
                .build();
    }

    private Channel createChannel(String name, ChannelType type) {
        return Channel.builder()
                .channelName(name)
                .channelType(type)
                .build();
    }
}
