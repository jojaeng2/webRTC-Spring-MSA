package webrtc.chatservice.repository.users;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChannelUser;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.exception.UserException.NotExistUserException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static webrtc.chatservice.enums.ChannelType.TEXT;
import static webrtc.chatservice.enums.ChannelType.VOIP;

@DataJpaTest
public class UsersRepositoryImplTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private UsersRepository repository;

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
        Users createUsers = repository.save(users);

        // then
        assertThat(users.getId()).isEqualTo(createUsers.getId());
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
    void 이메일로유저찾기성공() {
        //given
        Users users = createUsers(nickname1, password, email1);
        repository.save(users);

        //when
        Optional<Users> OpUsers = repository.findByEmail(email1);

        //then
        assertThat(OpUsers.isPresent()).isTrue();
    }

    @Test
    void 이메일로유저찾기실패() {
        //given
        Users users = createUsers(nickname1, password, email1);

        //when
        Optional<Users> OpUsers = repository.findByEmail(email1);

        //then
        assertThat(OpUsers.isPresent()).isFalse();
    }

    @Test
    void 채널ID로유저찾기성공() {
        //given
        Users users = new Users(nickname1, password, email1);
        Channel channel = createChannel(channelName1, text);
        ChannelUser channelUser = new ChannelUser(users, channel);
        repository.save(users);
        em.persist(channel);
        em.persist(channelUser);

        //when

        List<Users> findUsers = repository.findUsersByChannelId(channel.getId());

        //then
        assertThat(findUsers.get(0).getId()).isEqualTo(users.getId());
    }

    @Test
    void 채널ID로유저찾기실패() {
        //given
        Users users = new Users(nickname1, password, email1);
        Channel channel = createChannel(channelName1, text);
        repository.save(users);
        em.persist(channel);

        //when

        List<Users> findUsers = repository.findUsersByChannelId(channel.getId());

        //then
        assertThat(findUsers.isEmpty()).isTrue();
    }



    private Users createUsers(String name, String password, String email) {
        return new Users(name, password, email);
    }

    private Channel createChannel(String name, ChannelType type) {
        return new Channel(name, type);
    }
}
