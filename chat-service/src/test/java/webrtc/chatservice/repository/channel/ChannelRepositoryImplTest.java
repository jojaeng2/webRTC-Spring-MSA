package webrtc.chatservice.repository.channel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChannelUser;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.exception.ChannelException.NotExistChannelException;
import webrtc.chatservice.repository.hashtag.HashTagRepository;
import webrtc.chatservice.repository.user.UserRepository;
import webrtc.chatservice.service.user.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static webrtc.chatservice.enums.ChannelType.TEXT;
import static webrtc.chatservice.enums.ChannelType.VOIP;

@SpringBootTest
public class ChannelRepositoryImplTest {

    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChannelUserRepository channelUserRepository;
    @Autowired
    private HashTagRepository hashTagRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void clearUserCache() {
        userService.redisDataEvict();
    }

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



    @BeforeEach
    public void 테스트용_유저생성() {
        User user1 = new User(nickname1, password, email1);
        userRepository.saveUser(user1);

        User user2 = new User(nickname2, password, email2);
        userRepository.saveUser(user2);
    }

    @Test
    @Transactional
    public void 채널생성_성공() {
        // given
        Channel channel = new Channel(channelName1, text);


        // when
        Channel createdChannel = channelRepository.createChannel(channel, returnHashTags());

        // then
        assertThat(createdChannel).isEqualTo(channel);
    }

    @Test
    @Transactional
    public void 채널ID로_채널찾기_성공() {
        // given
        Channel channel = new Channel(channelName1, text);
        channelRepository.createChannel(channel, returnHashTags());

        // when
        Channel findChannel = channelRepository.findChannelsById(channel.getId()).get(0);

        // then
        assertThat(channel).isEqualTo(findChannel);
    }

    @Test
    @Transactional
    public void 채널ID로_채널찾기_실패() {
        // given

        // when

        // then
        assertThrows(NotExistChannelException.class, () -> {
            Channel findChannel = channelRepository.findChannelsById(notExistChannelId).get(0);
        });
    }

    @Test
    @Transactional
    public void 채널삭제_성공() {
        // given
        Channel channel = new Channel(channelName1, text);
        channelRepository.createChannel(channel, returnHashTags());

        // when
        channelRepository.deleteChannel(channel);

        // then
    }

    @Test
    @Transactional
    public void 채널삭제_실패() {
        // given

        // when

        // then
        assertThrows(NotExistChannelException.class, () -> {
            channelRepository.deleteChannel(null);
        });
    }

    @Test
    @Transactional
    public void 채널_유저입장_성공() {
        // given
        Channel channel = new Channel(channelName1, text);
        channelRepository.createChannel(channel, returnHashTags());
        User user = userRepository.findUserByEmail(email1);
        ChannelUser channelUser = new ChannelUser(user, channel);

        // when
        channelRepository.enterChannelUserInChannel(channel, channelUser);

        // then
        assertThat(channel.getCurrentParticipants()).isEqualTo(1);
    }

    @Test
    @Transactional
    public void 채널_유저퇴장_성공() {
        // given
        Channel channel = new Channel(channelName1, text);
        channelRepository.createChannel(channel, returnHashTags());
        User user = userRepository.findUserByEmail(email1);
        ChannelUser channelUser = new ChannelUser(user, channel);
        channelRepository.enterChannelUserInChannel(channel, channelUser);

        // when
        channelRepository.exitChannelUserInChannel(channel, channelUser);

        // then
        assertThat(channel.getCurrentParticipants()).isEqualTo(0);
    }


    @Test
    @Transactional
    public void 채널이름으로_채널찾기_성공() {
        // given
        Channel channel = new Channel(channelName1, text);
        channelRepository.createChannel(channel, returnHashTags());

        // when
        Channel findChannel = channelRepository.findChannelByChannelName(channelName1);

        // then
        assertThat(channel).isEqualTo(findChannel);
    }

    @Test
    @Transactional
    public void 채널이름으로_채널찾기_실패() {
        // given

        // when
        ;

        // then
        assertThrows(NotExistChannelException.class, ()-> {
            channelRepository.findChannelByChannelName(channelName1);
        });
    }

    @Test
    @Transactional
    public void Hashtag로_채널찾기_참가인원순_DESC_20개미만_성공() {
        // given
        int testcase = 19;
        int firstEnteridx = 10;
        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            channelRepository.createChannel(channel, returnHashTags());
            if(i == firstEnteridx) {
                User user = userRepository.findUserByEmail(email1);
                ChannelUser channelUser = new ChannelUser(user, channel);
                channelRepository.enterChannelUserInChannel(channel, channelUser);
            }
        }


        // when
        List<Channel> findChannels = channelRepository.findChannelsByHashNameAndPartiDESC(tag1, 0);

        // then
        assertThat(findChannels.get(0).getCurrentParticipants()).isEqualTo(1);
        assertThat(findChannels.get(0).getChannelName()).isEqualTo(firstEnteridx + " channel");
        assertThat(findChannels.size()).isEqualTo(testcase);
    }

    @Test
    @Transactional
    public void Hashtag로_채널찾기_참가인원순_DESC_20개초과_성공() {
        // given
        int testcase = 30;
        int firstEnteridx = 10;
        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            channelRepository.createChannel(channel, returnHashTags());
            if(i == firstEnteridx) {
                User user = userRepository.findUserByEmail(email1);
                ChannelUser channelUser = new ChannelUser(user, channel);
                channelRepository.enterChannelUserInChannel(channel, channelUser);
            }
        }

        // when
        List<Channel> findChannels0 = channelRepository.findChannelsByHashNameAndPartiDESC(tag1, 0);
        List<Channel> findChannels1 = channelRepository.findChannelsByHashNameAndPartiDESC(tag1, 1);

        // then
        assertThat(findChannels0.get(0).getCurrentParticipants()).isEqualTo(1);
        assertThat(findChannels0.get(0).getChannelName()).isEqualTo(firstEnteridx + " channel");
        assertThat(findChannels0.size()).isEqualTo(20);
        assertThat(findChannels1.size()).isEqualTo(10);
    }

    @Test
    @Transactional
    public void Hashtag로_채널찾기_참가인원순_ASC_20개미만_성공() {
        // given
        int testcase = 19;
        int firstEnteridx = 10;
        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            channelRepository.createChannel(channel, returnHashTags());
            if(i == firstEnteridx) {
                User user = userRepository.findUserByEmail(email1);
                ChannelUser channelUser = new ChannelUser(user, channel);
                channelRepository.enterChannelUserInChannel(channel, channelUser);
            }
        }


        // when
        List<Channel> findChannels = channelRepository.findChannelsByHashNameAndPartiASC(tag1, 0);

        // then
        assertThat(findChannels.get(testcase-1).getCurrentParticipants()).isEqualTo(1);
        assertThat(findChannels.get(testcase-1).getChannelName()).isEqualTo(firstEnteridx + " channel");
        assertThat(findChannels.size()).isEqualTo(testcase);
    }

    @Test
    @Transactional
    public void Hashtag로_채널찾기_참가인원순_ASC_20개초과_성공() {
        // given
        int testcase = 30;
        int firstEnteridx = 10;
        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            channelRepository.createChannel(channel, returnHashTags());
            if(i == firstEnteridx) {
                User user = userRepository.findUserByEmail(email1);
                ChannelUser channelUser = new ChannelUser(user, channel);
                channelRepository.enterChannelUserInChannel(channel, channelUser);
            }
        }

        // when
        List<Channel> findChannels0 = channelRepository.findChannelsByHashNameAndPartiASC(tag1, 0);
        List<Channel> findChannels1 = channelRepository.findChannelsByHashNameAndPartiASC(tag1, 1);

        // then
        assertThat(findChannels1.get((testcase-1)%20).getCurrentParticipants()).isEqualTo(1);
        assertThat(findChannels1.get((testcase-1)%20).getChannelName()).isEqualTo(firstEnteridx + " channel");
        assertThat(findChannels0.size()).isEqualTo(20);
        assertThat(findChannels1.size()).isEqualTo(10);
    }

//
//
////    @Test
////    @Transactional
////    public void 존재하지않는_Hashtag로_채널조회() {
////        // given
////
////        // when
////        List<Channel> notExisttagFindChannels = channelRepository.findChannelsByHashName("no", 0);
////
////        // then
////        assertThat(notExisttagFindChannels.size()).isEqualTo(0);
////    }
//
//


    @Test
    @Transactional
    public void 전체채널_참가인원순_DESC_0개_성공() {
        // given

        // when
        List<Channel> findChannels = channelRepository.findAnyChannelByPartiDESC(0);

        // then
        assertThat(findChannels.size()).isEqualTo(0);
    }

    @Test
    @Transactional
    public void 전체채널_참가인원순_ASC_0개_성공() {
        // given

        // when
        List<Channel> findChannels = channelRepository.findAnyChannelByPartiASC(0);

        // then
        assertThat(findChannels.size()).isEqualTo(0);
    }

    @Test
    @Transactional
    public void 전체채널_참가인원순_DESC_20개미만_성공() {
        // given
        int testcase = 19;
        int firstEnteridx = 10;

        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            channelRepository.createChannel(channel, returnHashTags());
            if(i == firstEnteridx) {
                User user = userRepository.findUserByEmail(email1);
                ChannelUser channelUser = new ChannelUser(user, channel);
                channelRepository.enterChannelUserInChannel(channel, channelUser);
            }
        }

        // when
        List<Channel> findChannels = channelRepository.findAnyChannelByPartiDESC(0);

        // then
        assertThat(findChannels.get(0).getCurrentParticipants()).isEqualTo(1);
        assertThat(findChannels.get(0).getChannelName()).isEqualTo(firstEnteridx + " channel");
        assertThat(findChannels.size()).isEqualTo(testcase);
    }

    @Test
    @Transactional
    public void 전체채널_참가인원순_ASC_20개미만_성공() {
        // given
        int testcase = 19;
        int firstEnteridx = 10;

        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            channelRepository.createChannel(channel, returnHashTags());
            if(i == firstEnteridx) {
                User user = userRepository.findUserByEmail(email1);
                ChannelUser channelUser = new ChannelUser(user, channel);
                channelRepository.enterChannelUserInChannel(channel, channelUser);
            }
        }

        // when
        List<Channel> findChannels = channelRepository.findAnyChannelByPartiASC(0);

        // then
        assertThat(findChannels.get(testcase-1).getCurrentParticipants()).isEqualTo(1);
        assertThat(findChannels.get(testcase-1).getChannelName()).isEqualTo(firstEnteridx + " channel");
        assertThat(findChannels.size()).isEqualTo(testcase);
    }


    @Test
    @Transactional
    public void 전체채널_참가인원순_DESC_20개초과_성공() {
        // given
        int testcase = 30;
        int firstEnteridx = 10;

        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            channelRepository.createChannel(channel, returnHashTags());
            if(i == firstEnteridx) {
                User user = userRepository.findUserByEmail(email1);
                ChannelUser channelUser = new ChannelUser(user, channel);
                channelRepository.enterChannelUserInChannel(channel, channelUser);
            }
        }

        // when
        List<Channel> findChannels0 = channelRepository.findAnyChannelByPartiDESC(0);
        List<Channel> findChannels1 = channelRepository.findAnyChannelByPartiDESC(1);


        // then
        assertThat(findChannels0.get(0).getCurrentParticipants()).isEqualTo(1);
        assertThat(findChannels0.get(0).getChannelName()).isEqualTo(firstEnteridx + " channel");
        assertThat(findChannels0.size()).isEqualTo(20);
        assertThat(findChannels1.size()).isEqualTo(10);
    }

    @Test
    @Transactional
    public void 전체채널_참가인원순_ASC_20개초과_성공() {
        // given
        int testcase = 30;
        int firstEnteridx = 10;

        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            channelRepository.createChannel(channel, returnHashTags());
            if(i == firstEnteridx) {
                User user = userRepository.findUserByEmail(email1);
                ChannelUser channelUser = new ChannelUser(user, channel);
                channelRepository.enterChannelUserInChannel(channel, channelUser);
            }
        }

        // when
        List<Channel> findChannels0 = channelRepository.findAnyChannelByPartiASC(0);
        List<Channel> findChannels1 = channelRepository.findAnyChannelByPartiASC(1);


        // then
        assertThat(findChannels1.get((testcase-1)%20).getCurrentParticipants()).isEqualTo(1);
        assertThat(findChannels1.get((testcase-1)%20).getChannelName()).isEqualTo(firstEnteridx + " channel");
        assertThat(findChannels0.size()).isEqualTo(20);
        assertThat(findChannels1.size()).isEqualTo(10);
    }

    @Test
    @Transactional
    public void 나의채널_참가인원순_DESC_0개_성공() {
        // given
        int testcase = 0;
        User user = userRepository.findUserByEmail(email1);
        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            channelRepository.createChannel(channel, returnHashTags());

            ChannelUser channelUser = new ChannelUser(user, channel);
            channelRepository.enterChannelUserInChannel(channel, channelUser);
        }

        // when
        List<Channel> findChannels0 = channelRepository.findMyChannelByPartiDESC(user.getId(), 0);


        // then
        assertThat(findChannels0.size()).isEqualTo(testcase);
    }

    @Test
    @Transactional
    public void 나의채널_참가인원순_ASC_0개_성공() {
        // given
        int testcase = 0;
        User user = userRepository.findUserByEmail(email1);
        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            channelRepository.createChannel(channel, returnHashTags());

            ChannelUser channelUser = new ChannelUser(user, channel);
            channelRepository.enterChannelUserInChannel(channel, channelUser);
        }

        // when
        List<Channel> findChannels0 = channelRepository.findMyChannelByPartiASC(user.getId(), 0);


        // then
        assertThat(findChannels0.size()).isEqualTo(testcase);
    }

    @Test
    @Transactional
    public void 나의채널_참가인원순_DESC_20개미만_성공() {
        // given
        int testcase = 19;
        int firstEnteridx = 10;
        User user1 = userRepository.findUserByEmail(email1);
        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            channelRepository.createChannel(channel, returnHashTags());

            ChannelUser channelUser1 = new ChannelUser(user1, channel);
            channelRepository.enterChannelUserInChannel(channel, channelUser1);
            if(i == firstEnteridx) {
                User user2 = userRepository.findUserByEmail(email2);
                ChannelUser channelUser2 = new ChannelUser(user2, channel);
                channelRepository.enterChannelUserInChannel(channel, channelUser2);
            }
        }

        // when
        List<Channel> findChannels0 = channelRepository.findMyChannelByPartiDESC(user1.getId(), 0);

        
        // then
        assertThat(findChannels0.get(0).getChannelName()).isEqualTo(firstEnteridx + " channel");
        assertThat(findChannels0.get(0).getCurrentParticipants()).isEqualTo(2);
        assertThat(findChannels0.size()).isEqualTo(testcase);
    }

    @Test
    @Transactional
    public void 나의채널_참가인원순_ASC_20개미만_성공() {
        // given
        int testcase = 19;
        int firstEnteridx = 10;
        User user1 = userRepository.findUserByEmail(email1);
        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            channelRepository.createChannel(channel, returnHashTags());

            ChannelUser channelUser1 = new ChannelUser(user1, channel);
            channelRepository.enterChannelUserInChannel(channel, channelUser1);
            if(i == firstEnteridx) {
                User user2 = userRepository.findUserByEmail(email2);
                ChannelUser channelUser2 = new ChannelUser(user2, channel);
                channelRepository.enterChannelUserInChannel(channel, channelUser2);
            }
        }

        // when
        List<Channel> findChannels0 = channelRepository.findMyChannelByPartiASC(user1.getId(), 0);

        // then
        assertThat(findChannels0.get((testcase-1)%20).getChannelName()).isEqualTo(firstEnteridx + " channel");
        assertThat(findChannels0.get((testcase-1)%20).getCurrentParticipants()).isEqualTo(2);
        assertThat(findChannels0.size()).isEqualTo(testcase);
    }

    @Test
    @Transactional
    public void 나의채널_참가인원순_DESC_20개초과_성공() {
        // given
        int testcase = 30;
        int firstEnteridx = 10;
        User user1 = userRepository.findUserByEmail(email1);
        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            channelRepository.createChannel(channel, returnHashTags());

            ChannelUser channelUser1 = new ChannelUser(user1, channel);
            channelRepository.enterChannelUserInChannel(channel, channelUser1);
            if(i == firstEnteridx) {
                User user2 = userRepository.findUserByEmail(email2);
                ChannelUser channelUser2 = new ChannelUser(user2, channel);
                channelRepository.enterChannelUserInChannel(channel, channelUser2);
            }
        }

        // when
        List<Channel> findChannels0 = channelRepository.findMyChannelByPartiDESC(user1.getId(), 0);
        List<Channel> findChannels1 = channelRepository.findMyChannelByPartiDESC(user1.getId(), 1);


        // then
        assertThat(findChannels0.get(0).getChannelName()).isEqualTo(firstEnteridx + " channel");
        assertThat(findChannels0.get(0).getCurrentParticipants()).isEqualTo(2);
        assertThat(findChannels0.size()).isEqualTo(20);
        assertThat(findChannels1.size()).isEqualTo(10);
    }

    @Test
    @Transactional
    public void 나의채널_참가인원순_ASC_20개초과_성공() {
        // given
        int testcase = 30;
        int firstEnteridx = 10;
        User user1 = userRepository.findUserByEmail(email1);
        for(int i=0; i<testcase; i++) {
            Channel channel = new Channel(i+" channel", text);
            channelRepository.createChannel(channel, returnHashTags());

            ChannelUser channelUser1 = new ChannelUser(user1, channel);
            channelRepository.enterChannelUserInChannel(channel, channelUser1);
            if(i == firstEnteridx) {
                User user2 = userRepository.findUserByEmail(email2);
                ChannelUser channelUser2 = new ChannelUser(user2, channel);
                channelRepository.enterChannelUserInChannel(channel, channelUser2);
            }
        }

        // when
        List<Channel> findChannels0 = channelRepository.findMyChannelByPartiASC(user1.getId(), 0);
        List<Channel> findChannels1 = channelRepository.findMyChannelByPartiASC(user1.getId(), 1);


        // then
        assertThat(findChannels1.get((testcase-1)%20).getChannelName()).isEqualTo(firstEnteridx + " channel");
        assertThat(findChannels1.get((testcase-1)%20).getCurrentParticipants()).isEqualTo(2);
        assertThat(findChannels0.size()).isEqualTo(20);
        assertThat(findChannels1.size()).isEqualTo(10);
    }

//
    @Test
    @Transactional
    public void channelId와_UserId로_채널찾기_성공() {
        // given
        Channel channel = new Channel(channelName1, text);
        channelRepository.createChannel(channel, returnHashTags());
        User user = userRepository.findUserByEmail(email1);
        ChannelUser channelUser = new ChannelUser(user, channel);
        channelRepository.enterChannelUserInChannel(channel, channelUser);

        // when
        List<Channel> findChannels = channelRepository.findChannelsByChannelIdAndUserId(channel.getId(), user.getId());

        // then
        assertThat(findChannels.get(0)).isEqualTo(channel);
    }

    @Test
    @Transactional
    public void channelId와_UserId로_채널찾기_실패() {
        // given
        Channel channel = new Channel(channelName1, text);
        channelRepository.createChannel(channel, returnHashTags());
        User user = userRepository.findUserByEmail(email1);

        // when

        // then
        assertThrows(NotExistChannelException.class, ()-> {
            channelRepository.findChannelsByChannelIdAndUserId(channel.getId(), user.getId());
        });
    }

    public List<String> returnHashTags() {
        List<String> hashTags = new ArrayList<>();
        hashTags.add(tag1);
        hashTags.add(tag2);
        hashTags.add(tag3);
        return hashTags;
    }
}
