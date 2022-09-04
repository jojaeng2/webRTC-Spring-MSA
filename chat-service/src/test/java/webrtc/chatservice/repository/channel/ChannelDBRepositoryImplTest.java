package webrtc.chatservice.repository.channel;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import webrtc.chatservice.domain.*;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.exception.ChannelException.NotExistChannelException;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static webrtc.chatservice.enums.ChannelType.TEXT;

@DataJpaTest
@Import({
        ChannelDBRepositoryImpl.class
})
public class ChannelDBRepositoryImplTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private ChannelDBRepository channelDBRepository;
    String nickname1 = "nickname1";
    String nickname2 = "nickname2";
    String password = "password";
    String email1 = "email1";
    String email2 = "email2";
    String channelName1 = "channelName1";
    String notExistChannelId = "null";
    String tag1 = "tag1";
    ChannelType text = TEXT;



    @Test
    public void 채널생성_성공() {
        // given
        Channel channel = new Channel(channelName1, text);
        HashTag hashTag1 = new HashTag(tag1);

        List<ChannelHashTag> channelHashTags = new ArrayList<>();
        ChannelHashTag channelHashTag1 = new ChannelHashTag(channel, hashTag1);
        channelHashTags.add(channelHashTag1);

        // when
        Channel createdChannel = channelDBRepository.createChannel(channel, channelHashTags);

        // then
        assertThat(createdChannel).isEqualTo(channel);
    }

    @Test
    public void 채널ID로_채널찾기_성공() {
        // given
        Channel channel = new Channel(channelName1, text);
        HashTag hashTag1 = new HashTag(tag1);

        List<ChannelHashTag> channelHashTags = new ArrayList<>();
        ChannelHashTag channelHashTag1 = new ChannelHashTag(channel, hashTag1);
        channelHashTags.add(channelHashTag1);
        channelDBRepository.createChannel(channel, channelHashTags);

        // when
        Channel findChannel = channelDBRepository.findChannelById(channel.getId());

        // then
        assertThat(channel).isEqualTo(findChannel);
    }

    @Test
    public void 채널ID로_채널찾기_실패() {
        // given

        // when

        // then
        assertThrows(NotExistChannelException.class, () -> {
            Channel findChannel = channelDBRepository.findChannelById(notExistChannelId);
        });
    }

    @Test
    public void 채널삭제_성공() {
        // given
        Channel channel = new Channel(channelName1, text);
        HashTag hashTag1 = new HashTag(tag1);

        List<ChannelHashTag> channelHashTags = new ArrayList<>();
        ChannelHashTag channelHashTag1 = new ChannelHashTag(channel, hashTag1);
        channelHashTags.add(channelHashTag1);
        channelDBRepository.createChannel(channel, channelHashTags);

        // when
        channelDBRepository.deleteChannel(channel);

        // then
    }

    @Test
    public void 채널삭제_실패() {
        // given

        // when

        // then
        assertThrows(NotExistChannelException.class, () -> {
            channelDBRepository.deleteChannel(null);
        });
    }

    @Test
    public void 채널_유저입장_성공() {
        // given
        Channel channel = new Channel(channelName1, text);
        HashTag hashTag1 = new HashTag(tag1);

        List<ChannelHashTag> channelHashTags = new ArrayList<>();
        ChannelHashTag channelHashTag1 = new ChannelHashTag(channel, hashTag1);
        channelHashTags.add(channelHashTag1);
        channelDBRepository.createChannel(channel, channelHashTags);
        Users users = new Users(nickname1, password, email1);
        em.persist(users);

        ChannelUser channelUser = new ChannelUser(users, channel);

        // when

        // then
        assertThat(channel.getCurrentParticipants()).isEqualTo(1);
    }

    @Test
    public void 채널_유저퇴장_성공() {
        // given
        Channel channel = new Channel(channelName1, text);
        HashTag hashTag1 = new HashTag(tag1);

        List<ChannelHashTag> channelHashTags = new ArrayList<>();
        ChannelHashTag channelHashTag1 = new ChannelHashTag(channel, hashTag1);
        channelHashTags.add(channelHashTag1);
        channelDBRepository.createChannel(channel, channelHashTags);
        Users users = new Users(nickname1, password, email1);
        em.persist(users);

        ChannelUser channelUser = new ChannelUser(users, channel);

        // when
        channelDBRepository.exitChannelUserInChannel(channel, channelUser);

        // then
        assertThat(channel.getCurrentParticipants()).isEqualTo(0);
    }

    @Test
    public void 채널이름으로_채널찾기_성공() {
        // given
        Channel channel = new Channel(channelName1, text);
        HashTag hashTag1 = new HashTag(tag1);

        List<ChannelHashTag> channelHashTags = new ArrayList<>();
        ChannelHashTag channelHashTag1 = new ChannelHashTag(channel, hashTag1);
        channelHashTags.add(channelHashTag1);
        channelDBRepository.createChannel(channel, channelHashTags);

        // when
        Channel findChannel = channelDBRepository.findChannelByChannelName(channelName1);

        // then
        assertThat(channel).isEqualTo(findChannel);
    }

    @Test
    public void 채널이름으로_채널찾기_실패() {
        // given

        // when
        ;

        // then
        assertThrows(NotExistChannelException.class, ()-> {
            channelDBRepository.findChannelByChannelName(channelName1);
        });
    }

    @Test
    public void Hashtag로_채널찾기_참가인원순_DESC_20개미만_성공() {
        // given
        int testcase = 19;
        int firstEnteridx = 10;
        HashTag hashTag1 = new HashTag(tag1);


        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            List<ChannelHashTag> channelHashTags = new ArrayList<>();
            ChannelHashTag channelHashTag1 = new ChannelHashTag(channel, hashTag1);
            channelHashTags.add(channelHashTag1);
            channelDBRepository.createChannel(channel, channelHashTags);
            if(i == firstEnteridx) {
                Users users = new Users(nickname1, password, email1);
                em.persist(users);
                ChannelUser channelUser = new ChannelUser(users, channel);
            }
        }


        // when
        List<Channel> findChannels = channelDBRepository.findChannelsByHashName(hashTag1, 0, "desc");

        // then
        assertThat(findChannels.get(0).getCurrentParticipants()).isEqualTo(1);
        assertThat(findChannels.get(0).getChannelName()).isEqualTo(firstEnteridx + " channel");
        assertThat(findChannels.size()).isEqualTo(testcase);
    }

    @Test
    public void Hashtag로_채널찾기_참가인원순_DESC_20개초과_성공() {
        // given
        int testcase = 30;
        int firstEnteridx = 10;
        HashTag hashTag1 = new HashTag(tag1);

        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            List<ChannelHashTag> channelHashTags = new ArrayList<>();
            ChannelHashTag channelHashTag1 = new ChannelHashTag(channel, hashTag1);
            channelHashTags.add(channelHashTag1);
            channelDBRepository.createChannel(channel, channelHashTags);

            if(i == firstEnteridx) {
                Users users = new Users(nickname1, password, email1);
                em.persist(users);
                ChannelUser channelUser = new ChannelUser(users, channel);
            }
        }

        // when
        List<Channel> findChannels0 = channelDBRepository.findChannelsByHashName(hashTag1, 0, "desc");
        List<Channel> findChannels1 = channelDBRepository.findChannelsByHashName(hashTag1, 1, "desc");

        // then
        assertThat(findChannels0.get(0).getCurrentParticipants()).isEqualTo(1);
        assertThat(findChannels0.get(0).getChannelName()).isEqualTo(firstEnteridx + " channel");
        assertThat(findChannels0.size()).isEqualTo(20);
        assertThat(findChannels1.size()).isEqualTo(10);
    }

    @Test
    public void Hashtag로_채널찾기_참가인원순_ASC_20개미만_성공() {
        // given
        int testcase = 19;
        int firstEnteridx = 10;
        HashTag hashTag1 = new HashTag(tag1);

        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            List<ChannelHashTag> channelHashTags = new ArrayList<>();
            ChannelHashTag channelHashTag1 = new ChannelHashTag(channel, hashTag1);
            channelHashTags.add(channelHashTag1);
            channelDBRepository.createChannel(channel, channelHashTags);

            if(i == firstEnteridx) {
                Users users = new Users(nickname1, password, email1);
                em.persist(users);
                ChannelUser channelUser = new ChannelUser(users, channel);
            }
        }


        // when
        List<Channel> findChannels = channelDBRepository.findChannelsByHashName(hashTag1, 0, "asc");

        // then
        assertThat(findChannels.get(testcase-1).getCurrentParticipants()).isEqualTo(1);
        assertThat(findChannels.get(testcase-1).getChannelName()).isEqualTo(firstEnteridx + " channel");
        assertThat(findChannels.size()).isEqualTo(testcase);
    }

    @Test
    public void Hashtag로_채널찾기_참가인원순_ASC_20개초과_성공() {
        // given
        int testcase = 30;
        int firstEnteridx = 10;
        HashTag hashTag1 = new HashTag(tag1);

        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            List<ChannelHashTag> channelHashTags = new ArrayList<>();
            ChannelHashTag channelHashTag1 = new ChannelHashTag(channel, hashTag1);
            channelHashTags.add(channelHashTag1);
            channelDBRepository.createChannel(channel, channelHashTags);

            if(i == firstEnteridx) {
                Users users = new Users(nickname1, password, email1);
                em.persist(users);
                ChannelUser channelUser = new ChannelUser(users, channel);
            }
        }

        // when
        List<Channel> findChannels0 = channelDBRepository.findChannelsByHashName(hashTag1, 0, "asc");
        List<Channel> findChannels1 = channelDBRepository.findChannelsByHashName(hashTag1, 1, "asc");

        // then
        assertThat(findChannels1.get((testcase-1)%20).getCurrentParticipants()).isEqualTo(1);
        assertThat(findChannels1.get((testcase-1)%20).getChannelName()).isEqualTo(firstEnteridx + " channel");
        assertThat(findChannels0.size()).isEqualTo(20);
        assertThat(findChannels1.size()).isEqualTo(10);
    }



    @Test
    public void 전체채널_참가인원순_DESC_0개_성공() {
        // given

        // when
        List<Channel> findChannels = channelDBRepository.findAnyChannels(0, "desc");

        // then
        assertThat(findChannels.size()).isEqualTo(0);
    }

    @Test
    public void 전체채널_참가인원순_ASC_0개_성공() {
        // given

        // when
        List<Channel> findChannels = channelDBRepository.findAnyChannels(0, "asc");

        // then
        assertThat(findChannels.size()).isEqualTo(0);
    }

    @Test
    public void 전체채널_참가인원순_DESC_20개미만_성공() {
        // given
        int testcase = 19;
        int firstEnteridx = 10;
        HashTag hashTag1 = new HashTag(tag1);

        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            List<ChannelHashTag> channelHashTags = new ArrayList<>();
            ChannelHashTag channelHashTag1 = new ChannelHashTag(channel, hashTag1);
            channelHashTags.add(channelHashTag1);
            channelDBRepository.createChannel(channel, channelHashTags);

            if(i == firstEnteridx) {
                Users users = new Users(nickname1, password, email1);
                em.persist(users);
                ChannelUser channelUser = new ChannelUser(users, channel);
            }
        }

        // when
        List<Channel> findChannels = channelDBRepository.findAnyChannels(0, "desc");

        // then
        assertThat(findChannels.get(0).getCurrentParticipants()).isEqualTo(1);
        assertThat(findChannels.get(0).getChannelName()).isEqualTo(firstEnteridx + " channel");
        assertThat(findChannels.size()).isEqualTo(testcase);
    }

    @Test
    public void 전체채널_참가인원순_ASC_20개미만_성공() {
        // given
        int testcase = 19;
        int firstEnteridx = 10;
        HashTag hashTag1 = new HashTag(tag1);

        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            List<ChannelHashTag> channelHashTags = new ArrayList<>();
            ChannelHashTag channelHashTag1 = new ChannelHashTag(channel, hashTag1);
            channelHashTags.add(channelHashTag1);
            channelDBRepository.createChannel(channel, channelHashTags);

            if(i == firstEnteridx) {
                Users users = new Users(nickname1, password, email1);
                em.persist(users);
                ChannelUser channelUser = new ChannelUser(users, channel);
            }
        }

        // when
        List<Channel> findChannels = channelDBRepository.findAnyChannels(0, "asc");

        // then
        assertThat(findChannels.get(testcase-1).getCurrentParticipants()).isEqualTo(1);
        assertThat(findChannels.get(testcase-1).getChannelName()).isEqualTo(firstEnteridx + " channel");
        assertThat(findChannels.size()).isEqualTo(testcase);
    }


    @Test
    public void 전체채널_참가인원순_DESC_20개초과_성공() {
        // given
        int testcase = 30;
        int firstEnteridx = 10;
        HashTag hashTag1 = new HashTag(tag1);

        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            List<ChannelHashTag> channelHashTags = new ArrayList<>();
            ChannelHashTag channelHashTag1 = new ChannelHashTag(channel, hashTag1);
            channelHashTags.add(channelHashTag1);
            channelDBRepository.createChannel(channel, channelHashTags);

            if(i == firstEnteridx) {
                Users users = new Users(nickname1, password, email1);
                em.persist(users);
                ChannelUser channelUser = new ChannelUser(users, channel);
            }
        }
        // when
        List<Channel> findChannels0 = channelDBRepository.findAnyChannels(0, "desc");
        List<Channel> findChannels1 = channelDBRepository.findAnyChannels(1, "desc");


        // then
        assertThat(findChannels0.get(0).getCurrentParticipants()).isEqualTo(1);
        assertThat(findChannels0.get(0).getChannelName()).isEqualTo(firstEnteridx + " channel");
        assertThat(findChannels0.size()).isEqualTo(20);
        assertThat(findChannels1.size()).isEqualTo(10);
    }

    @Test
    public void 전체채널_참가인원순_ASC_20개초과_성공() {
        // given
        int testcase = 30;
        int firstEnteridx = 10;
        HashTag hashTag1 = new HashTag(tag1);

        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            List<ChannelHashTag> channelHashTags = new ArrayList<>();
            ChannelHashTag channelHashTag1 = new ChannelHashTag(channel, hashTag1);
            channelHashTags.add(channelHashTag1);
            channelDBRepository.createChannel(channel, channelHashTags);

            if(i == firstEnteridx) {
                Users users = new Users(nickname1, password, email1);
                em.persist(users);
                ChannelUser channelUser = new ChannelUser(users, channel);
            }
        }

        // when
        List<Channel> findChannels0 = channelDBRepository.findAnyChannels(0, "asc");
        List<Channel> findChannels1 = channelDBRepository.findAnyChannels(1, "asc");


        // then
        assertThat(findChannels1.get((testcase-1)%20).getCurrentParticipants()).isEqualTo(1);
        assertThat(findChannels1.get((testcase-1)%20).getChannelName()).isEqualTo(firstEnteridx + " channel");
        assertThat(findChannels0.size()).isEqualTo(20);
        assertThat(findChannels1.size()).isEqualTo(10);
    }

    @Test
    public void 나의채널_참가인원순_DESC_0개_성공() {
        // given
        int testcase = 0;
        HashTag hashTag1 = new HashTag(tag1);

        Users users = new Users(nickname1, password, email1);
        em.persist(users);

        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            List<ChannelHashTag> channelHashTags = new ArrayList<>();
            ChannelHashTag channelHashTag1 = new ChannelHashTag(channel, hashTag1);
            channelHashTags.add(channelHashTag1);
            channelDBRepository.createChannel(channel, channelHashTags);

            ChannelUser channelUser = new ChannelUser(users, channel);
        }

        // when
        List<Channel> findChannels0 = channelDBRepository.findMyChannels(users.getId(), 0, "desc");


        // then
        assertThat(findChannels0.size()).isEqualTo(testcase);
    }

    @Test
    public void 나의채널_참가인원순_ASC_0개_성공() {
        // given
        int testcase = 0;
        HashTag hashTag1 = new HashTag(tag1);

        Users users = new Users(nickname1, password, email1);
        em.persist(users);

        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            List<ChannelHashTag> channelHashTags = new ArrayList<>();
            ChannelHashTag channelHashTag1 = new ChannelHashTag(channel, hashTag1);
            channelHashTags.add(channelHashTag1);
            channelDBRepository.createChannel(channel, channelHashTags);

            ChannelUser channelUser = new ChannelUser(users, channel);
        }

        // when
        List<Channel> findChannels0 = channelDBRepository.findMyChannels(users.getId(), 0, "asc");


        // then
        assertThat(findChannels0.size()).isEqualTo(testcase);
    }

    @Test
    public void 나의채널_참가인원순_DESC_20개미만_성공() {
        // given
        int testcase = 19;
        int firstEnteridx = 10;
        HashTag hashTag1 = new HashTag(tag1);

        Users users1 = new Users(nickname1, password, email1);
        em.persist(users1);

        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            List<ChannelHashTag> channelHashTags = new ArrayList<>();
            ChannelHashTag channelHashTag1 = new ChannelHashTag(channel, hashTag1);
            channelHashTags.add(channelHashTag1);
            channelDBRepository.createChannel(channel, channelHashTags);

            new ChannelUser(users1, channel);
            if(i == firstEnteridx) {
                Users users2 = new Users(nickname2, password, email2);
                em.persist(users2);
                new ChannelUser(users2, channel);
            }
        }

        // when
        List<Channel> findChannels0 = channelDBRepository.findMyChannels(users1.getId(), 0, "desc");


        // then
        assertThat(findChannels0.get(0).getChannelName()).isEqualTo(firstEnteridx + " channel");
        assertThat(findChannels0.get(0).getCurrentParticipants()).isEqualTo(2);
        assertThat(findChannels0.size()).isEqualTo(testcase);
    }

    @Test
    public void 나의채널_참가인원순_ASC_20개미만_성공() {
        // given
        int testcase = 19;
        int firstEnteridx = 10;
        HashTag hashTag1 = new HashTag(tag1);

        Users users1 = new Users(nickname1, password, email1);
        em.persist(users1);

        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            List<ChannelHashTag> channelHashTags = new ArrayList<>();
            ChannelHashTag channelHashTag1 = new ChannelHashTag(channel, hashTag1);
            channelHashTags.add(channelHashTag1);
            channelDBRepository.createChannel(channel, channelHashTags);

            new ChannelUser(users1, channel);
            if(i == firstEnteridx) {
                Users users2 = new Users(nickname2, password, email2);
                em.persist(users2);
                new ChannelUser(users2, channel);
            }
        }

        // when
        List<Channel> findChannels0 = channelDBRepository.findMyChannels(users1.getId(), 0, "asc");

        // then
        assertThat(findChannels0.get((testcase-1)%20).getChannelName()).isEqualTo(firstEnteridx + " channel");
        assertThat(findChannels0.get((testcase-1)%20).getCurrentParticipants()).isEqualTo(2);
        assertThat(findChannels0.size()).isEqualTo(testcase);
    }

    @Test
    public void 나의채널_참가인원순_DESC_20개초과_성공() {
        // given
        int testcase = 30;
        int firstEnteridx = 10;
        HashTag hashTag1 = new HashTag(tag1);

        Users users1 = new Users(nickname1, password, email1);
        em.persist(users1);

        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            List<ChannelHashTag> channelHashTags = new ArrayList<>();
            ChannelHashTag channelHashTag1 = new ChannelHashTag(channel, hashTag1);
            channelHashTags.add(channelHashTag1);
            channelDBRepository.createChannel(channel, channelHashTags);

            new ChannelUser(users1, channel);
            if(i == firstEnteridx) {
                Users users2 = new Users(nickname2, password, email2);
                em.persist(users2);
                new ChannelUser(users2, channel);
            }
        }

        // when
        List<Channel> findChannels0 = channelDBRepository.findMyChannels(users1.getId(), 0, "desc");
        List<Channel> findChannels1 = channelDBRepository.findMyChannels(users1.getId(), 1, "desc");


        // then
        assertThat(findChannels0.get(0).getChannelName()).isEqualTo(firstEnteridx + " channel");
        assertThat(findChannels0.get(0).getCurrentParticipants()).isEqualTo(2);
        assertThat(findChannels0.size()).isEqualTo(20);
        assertThat(findChannels1.size()).isEqualTo(10);
    }

    @Test
    public void 나의채널_참가인원순_ASC_20개초과_성공() {
        // given
        int testcase = 30;
        int firstEnteridx = 10;
        HashTag hashTag1 = new HashTag(tag1);

        Users users1 = new Users(nickname1, password, email1);
        em.persist(users1);

        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            List<ChannelHashTag> channelHashTags = new ArrayList<>();
            ChannelHashTag channelHashTag1 = new ChannelHashTag(channel, hashTag1);
            channelHashTags.add(channelHashTag1);
            channelDBRepository.createChannel(channel, channelHashTags);

            new ChannelUser(users1, channel);
            if(i == firstEnteridx) {
                Users users2 = new Users(nickname2, password, email2);
                em.persist(users2);
                new ChannelUser(users2, channel);
            }
        }

        // when
        List<Channel> findChannels0 = channelDBRepository.findMyChannels(users1.getId(), 0, "asc");
        List<Channel> findChannels1 = channelDBRepository.findMyChannels(users1.getId(), 1, "asc");


        // then
        assertThat(findChannels1.get((testcase-1)%20).getChannelName()).isEqualTo(firstEnteridx + " channel");
        assertThat(findChannels1.get((testcase-1)%20).getCurrentParticipants()).isEqualTo(2);
        assertThat(findChannels0.size()).isEqualTo(20);
        assertThat(findChannels1.size()).isEqualTo(10);
    }

    @Test
    public void channelId와_UserId로_채널찾기_성공() {
        // given
        HashTag hashTag1 = new HashTag(tag1);
        Channel channel = new Channel(channelName1, text);
        List<ChannelHashTag> channelHashTags = new ArrayList<>();
        ChannelHashTag channelHashTag1 = new ChannelHashTag(channel, hashTag1);
        channelHashTags.add(channelHashTag1);
        channelDBRepository.createChannel(channel, channelHashTags);
        Users users = new Users(nickname1, password, email1);
        em.persist(users);
        new ChannelUser(users, channel);

        // when
        List<Channel> findChannels = channelDBRepository.findChannelsByChannelIdAndUserId(channel.getId(), users.getId());

        // then
        assertThat(findChannels.get(0)).isEqualTo(channel);
    }

    @Test
    public void channelId와_UserId로_채널찾기_실패() {
        // given
        HashTag hashTag1 = new HashTag(tag1);
        Channel channel = new Channel(channelName1, text);
        List<ChannelHashTag> channelHashTags = new ArrayList<>();
        ChannelHashTag channelHashTag1 = new ChannelHashTag(channel, hashTag1);
        channelHashTags.add(channelHashTag1);
        channelDBRepository.createChannel(channel, channelHashTags);
        Users users = new Users(nickname1, password, email1);
        em.persist(users);

        // when

        // then
        assertThrows(NotExistChannelException.class, ()-> {
            channelDBRepository.findChannelsByChannelIdAndUserId(channel.getId(), users.getId());
        });
    }
}
