package webrtc.v1.channel.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.entity.ChannelHashTag;
import webrtc.v1.channel.entity.ChannelUser;
import webrtc.v1.channel.repository.ChannelListRepository;
import webrtc.v1.channel.repository.ChannelListRepositoryImpl;
import webrtc.v1.channel.enums.ChannelType;
import webrtc.v1.hashtag.entity.HashTag;
import webrtc.v1.staticgenarator.ChannelGenerator;
import webrtc.v1.staticgenarator.ChannelHashTagGenerator;
import webrtc.v1.staticgenarator.ChannelUserGenerator;
import webrtc.v1.staticgenarator.HashTagGenerator;
import webrtc.v1.staticgenarator.UserGenerator;
import webrtc.v1.user.entity.Users;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static webrtc.v1.channel.enums.ChannelType.TEXT;

@DataJpaTest
@Import({
        ChannelListRepositoryImpl.class
})
public class ChannelListRepositoryImplTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private ChannelListRepository repository;
    String nickname1 = "nickname1";
    String password = "password";
    String email1 = "email1";
    String desc = "DESC";
    String channelName1 = "channelName1";
    String tag1 = "tag1";
    ChannelType text = TEXT;

    private static final int idx = 0;

    @Test
    void 전채채널목록불러오기_20개미만() {
        // given
        int testcase = 19;
        for(int i=1; i<=testcase; i++) {
            Channel channel = ChannelGenerator.createTextChannel();
            em.persist(channel);
        }

        // when
        List<Channel> anyChannels = repository.findAnyChannels(idx, desc);

        // then
        assertThat(anyChannels.size()).isEqualTo(testcase);
    }

    @Test
    void 전채채널목록불러오기_20개초과() {
        // given
        int testcase = 30;
        for(int i=1; i<=testcase; i++) {
            Channel channel = ChannelGenerator.createTextChannel();
            em.persist(channel);
        }


        // when
        List<Channel> anyChannels = repository.findAnyChannels(idx, desc);

        // then
        assertThat(anyChannels.size()).isEqualTo(20);
    }

    @Test
    void 나의채널목록불러오기_20개미만() {
        // given
        Users user = Users.builder()
            .nickname(nickname1)
            .password(password)
            .email(email1)
            .build();

        int testcase = 19;
        for(int i=1; i<=testcase; i++) {
            Channel channel = createChannel("channel" + i, text);
            ChannelUser channelUser = createChannelUser(user, channel);
            em.persist(channelUser);
            em.persist(channel);
        }
        em.persist(user);



        // when
        List<Channel> myChannels = repository.findMyChannels(user.getId(), 0, desc);

        // then
        assertThat(myChannels.size()).isEqualTo(testcase);
    }


    @Test
    void 나의채널목록불러오기_20개초과() {
        // given
        Users user = Users.builder()
            .nickname(nickname1)
            .password(password)
            .email(email1)
            .build();

        int testcase = 30;
        for(int i=1; i<=testcase; i++) {
            Channel channel = createChannel("channel" + i, text);
            ChannelUser channelUser = createChannelUser(user, channel);
            em.persist(channelUser);
            em.persist(channel);
        }
        em.persist(user);



        // when
        List<Channel> myChannels = repository.findMyChannels(user.getId(), 0, desc);

        // then
        assertThat(myChannels.size()).isEqualTo(20);
    }

    @Test
    void 해시태그채널목록불러오기_20개미만() {
        // given
        int testcase = 19;
        HashTag hashTag = createHashTag(tag1);
        em.persist(hashTag);

        for(int i=1; i<=testcase; i++) {
            Channel channel = createChannel("channel" + i, text);
            ChannelHashTag channelHashTag = createChannelHashTag(channel, hashTag);
            em.persist(channel);
            em.persist(channelHashTag);
        }

        // when
        List<Channel> channels = repository.findChannelsByHashName(hashTag, 0, desc);

        // then
        assertThat(channels.size()).isEqualTo(testcase);
    }

    @Test
    void 해시태그채널목록불러오기_20개초과() {
        // given
        int testcase = 30;
        HashTag hashTag = createHashTag(tag1);

        for(int i=1; i<=testcase; i++) {
            Channel channel = createChannel("channel" + i, text);
            ChannelHashTag channelHashTag = createChannelHashTag(channel, hashTag);
            em.persist(channel);
            em.persist(channelHashTag);
        }
        em.persist(hashTag);

        // when
        List<Channel> channels = repository.findChannelsByHashName(hashTag, 0, desc);

        // then
        assertThat(channels.size()).isEqualTo(20);
    }

    @Test
    void 최근대화방불러오기20개미만() {
        // given
        int testcase = 10;

        for(int i=1; i<=testcase; i++) {
            Channel channel = ChannelGenerator.createTextChannel();
            em.persist(channel);
        }

        // when
        List<Channel> channels = repository.findChannelsRecentlyTalk(idx, desc);

        // then
        assertThat(channels.size()).isEqualTo(testcase);
        // then

    }

    @Test
    void 최근대화방불러오기20개초과() {
        // given
        int testcase = 30;

        for(int i=1; i<=testcase; i++) {
            Channel channel = ChannelGenerator.createTextChannel();
            em.persist(channel);
        }

        // when
        List<Channel> channels = repository.findChannelsRecentlyTalk(idx, desc);

        // then
        assertThat(channels.size()).isEqualTo(20);

    }


    private Channel createChannel(String name, ChannelType type) {
        return Channel.builder()
            .channelName(name)
            .channelType(type)
            .build();
    }

    private HashTag createHashTag(String name) {
        return HashTag.builder()
            .name(name)
            .build();
    }

    private ChannelHashTag createChannelHashTag(Channel channel, HashTag tag) {
        ChannelHashTag channelHashTag = ChannelHashTag.builder()
            .channel(channel)
            .hashTag(tag)
            .build();

        channel.addChannelHashTag(channelHashTag);
        tag.addChannelHashTag(channelHashTag);
        return channelHashTag;
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
