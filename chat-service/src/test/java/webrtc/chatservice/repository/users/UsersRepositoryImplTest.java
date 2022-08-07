package webrtc.chatservice.repository.users;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static webrtc.chatservice.enums.ChannelType.TEXT;
import static webrtc.chatservice.enums.ChannelType.VOIP;

@DataJpaTest
@Import({
        UsersRepositoryImpl.class
})
public class UsersRepositoryImplTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private UsersRepository usersRepository;

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
    @Transactional
    public void 유저저장_성공() {
        // given
        Users users = new Users("users", "users", "email");

        // when
        usersRepository.saveUser(users);

        // then

    }

    @Test
    @Transactional
    public void 유저저장_성공_AND_이메일로조회_성공() {
        //given
        Users users = new Users(nickname1, password, email1);
        usersRepository.saveUser(users);

        //when
        Users findUsers = usersRepository.findUserByEmail(email1);

        //then
        assertThat(findUsers).isEqualTo(users);
    }

    @Test
    @Transactional
    public void 유저저장_성공_AND_이메일로조회_실패() {
        //given
        Users users = new Users(nickname1, password, email1);
        usersRepository.saveUser(users);

        //when


        //then
        assertThrows(NotExistUserException.class,
                () -> usersRepository.findUserByEmail(email2));
    }

    @Test
    @Transactional
    public void 유저채널입장성공_AND_채널ID로조회_성공() {
        //given
        Users users = new Users(nickname1, password, email1);
        usersRepository.saveUser(users);
        Channel channel = new Channel(channelName1, text);
        ChannelUser channelUser = new ChannelUser(users, channel);
        em.persist(channel);
        //when

        List<Users> findUsers = usersRepository.findUsersByChannelId(channel.getId());

        //then
        assertThat(findUsers.get(0)).isEqualTo(users);
    }

    @Test
    @Transactional
    public void 유저채널입장성공_AND_채널ID로조회_실패() {
        //given
        Users users = new Users(nickname1, password, email1);
        usersRepository.saveUser(users);
        Channel channel = new Channel(channelName1, text);
        ChannelUser channelUser = new ChannelUser(users, channel);

        //when

        List<Users> usersList = usersRepository.findUsersByChannelId("NotExistChannelId");

        //then
        assertThat(usersList.isEmpty()).isEqualTo(true);
    }

}
