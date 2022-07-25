package webrtc.chatservice.service.channel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChatLog;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.dto.ChannelDto;
import webrtc.chatservice.dto.ChannelDto.ChannelResponse;
import webrtc.chatservice.dto.ChannelDto.CreateChannelRequest;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.exception.ChannelException.AlreadyExistChannelException;
import webrtc.chatservice.exception.ChannelException.AlreadyExistUserInChannelException;
import webrtc.chatservice.exception.ChannelException.ChannelParticipantsFullException;
import webrtc.chatservice.exception.ChannelException.NotExistChannelException;
import webrtc.chatservice.exception.ChannelUserException;
import webrtc.chatservice.exception.ChannelUserException.NotExistChannelUserException;
import webrtc.chatservice.repository.channel.ChannelRepository;
import webrtc.chatservice.repository.user.UserRepository;
import webrtc.chatservice.service.chat.ChatService;
import webrtc.chatservice.service.user.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static webrtc.chatservice.enums.ChannelType.TEXT;
import static webrtc.chatservice.enums.ChannelType.VOIP;
import static webrtc.chatservice.enums.ClientMessageType.CHAT;
import static webrtc.chatservice.enums.ClientMessageType.CREATE;

@SpringBootTest
public class ChannelServiceImplTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;
    @Autowired
    private ChannelService channelService;



    String nickname1 = "nickname1";
    String nickname2 = "nickname2";
    String password = "password";
    String email1 = "email1";
    String email2 = "email2";
    String channelName1 = "channelName1";
    String channelName2 = "channelName2";
    String tag1 = "tag1";
    String tag2 = "tag2";
    String tag3 = "tag3";
    ChannelType text = TEXT;
    ChannelType voip = VOIP;

    @BeforeEach
    public void clearUserCache() {
        userService.redisDataEvict();
    }

    @BeforeEach
    public void 테스트용_유저생성() {
        User user1 = new User(nickname1, password, email1);
        userRepository.saveUser(user1);

        User user2 = new User(nickname2, password, email2);
        userRepository.saveUser(user2);
    }

    @BeforeEach
    public void 테스트용_채널생성() {
        Channel channel = new Channel(channelName1, text);
        channelRepository.createChannel(channel, returnHashTags());
    }

    @Test
    @Transactional
    public void 채널생성_성공() {
        // given
        CreateChannelRequest request = new CreateChannelRequest(channelName2, returnHashTags(), TEXT);

        // when
        Channel createChannel = channelService.createChannel(request, email1);

        // then
        assertThat(createChannel.getChannelName()).isEqualTo(channelName2);
        assertThat(createChannel.getChannelHashTags().size()).isEqualTo(3);

    }

    @Test
    @Transactional
    public void 채널이름중복으로_채널생성_실패() {
        // given
        CreateChannelRequest request = new CreateChannelRequest(channelName1, returnHashTags(), TEXT);

        // when

        // then
        assertThrows(AlreadyExistChannelException.class,
                () -> {
                    channelService.createChannel(request, email1);
                });
    }


    @Test
    @Transactional
    public void 채널_첫번째메시지_반환성공() {
        // given
        CreateChannelRequest request = new CreateChannelRequest(channelName2, returnHashTags(), TEXT);
        Channel channel = channelService.createChannel(request, email1);

        // when
        ChatLog chatLog = chatService.findLastChatLogsByChannelId(channel.getId());

        // then
        assertThat(chatLog.getIdx()).isEqualTo(1L);
        assertThat(chatLog.getType()).isEqualTo(CREATE);
    }

    @Test
    @Transactional
    public void 채널_N번째메시지_반환성공() {
        // given
        int testCase = 10;
        CreateChannelRequest request = new CreateChannelRequest(channelName2, returnHashTags(), TEXT);
        Channel channel = channelService.createChannel(request, email1);
        for(int i=1; i<=testCase; i++) {
            chatService.saveChatLog(CHAT, i + " message", nickname1, channel, email1);
        }

        // when
        ChatLog chatLog = chatService.findLastChatLogsByChannelId(channel.getId());

        // then
        assertThat(chatLog.getIdx()).isEqualTo(testCase + 1L);
        assertThat(chatLog.getMessage()).isEqualTo(testCase + " message");
    }

    @Test
    @Transactional
    public void 유저_채널입장성공() {
        // given
        Channel channel = new Channel(channelName1, text);
        channelRepository.createChannel(channel, returnHashTags());

        // when
        channelService.enterChannel(channel, email1);

        // then
        assertThat(channel.getCurrentParticipants()).isEqualTo(1);
    }

    @Test
    @Transactional
    public void 유저_채널입장실패_By이미입장() {
        // given
        Channel channel = new Channel(channelName1, text);
        channelRepository.createChannel(channel, returnHashTags());
        channelService.enterChannel(channel, email1);

        // when

        // then
        Assertions.assertThrows(AlreadyExistUserInChannelException.class, ()->{
            channelService.enterChannel(channel, email1);
        });
    }

    @Test
    @Transactional
    public void 유저_채널입장실패_By인원제한() {
        // given
        Channel channel = new Channel(channelName1, text);
        channelRepository.createChannel(channel, returnHashTags());

        // when
        for(int i=1; i<=15; i++) {
            User user = new User(nickname1, password, i + email1);
            userRepository.saveUser(user);            userRepository.saveUser(user);
            channelService.enterChannel(channel, user.getEmail());
        }

        // then
        User user = userRepository.findUserByEmail(email1);
        assertThat(channel.getCurrentParticipants()).isEqualTo(15);
        assertThrows(ChannelParticipantsFullException.class,
                () -> channelService.enterChannel(channel, user.getEmail()));
    }

    @Test
    @Transactional
    public void 채널퇴장성공() {
        // given
        Channel channel = new Channel(channelName1, text);
        channelRepository.createChannel(channel, returnHashTags());
        channelService.enterChannel(channel, email1);

        User user = userRepository.findUserByEmail(email1);

        // when
        channelService.exitChannel(channel.getId(), user.getId());
        Channel findChannel = channelRepository.findChannelByChannelName(channelName1);

        // then
        assertThat(findChannel.getCurrentParticipants()).isEqualTo(0);
    }

    @Test
    @Transactional
    public void 채널퇴장실패_By존재하지않는_채널() {
        // given
        Channel channel = new Channel(channelName1, text);

        User user = userRepository.findUserByEmail(email1);

        // when

        // then
        assertThrows(NotExistChannelException.class, ()->{
            channelService.exitChannel(channel.getId(), user.getId());
        });

    }

    @Test
    @Transactional
    public void 채널퇴장실패_By존재하지않는_채널유저() {
        // given
        Channel channel = new Channel(channelName1, text);
        channelRepository.createChannel(channel, returnHashTags());
        User user = userRepository.findUserByEmail(email1);

        // when

        // then
        assertThrows(NotExistChannelUserException.class, ()->{
            channelService.exitChannel(channel.getId(), user.getId());
        });

    }

    @Test
    @Transactional
    public void 채널삭제성공() {
        // given
        Channel channel = new Channel(channelName1, text);
        channelRepository.createChannel(channel, returnHashTags());

        // when
        channelService.deleteChannel(channel.getId());

        // then
    }

    @Test
    @Transactional
    public void 채널삭제실패_By존재하지않는채널() {
        // given
        Channel channel = new Channel(channelName1, text);

        // when


        // then
        assertThrows(NotExistChannelException.class, () -> {
            channelService.deleteChannel(channel.getId());
        });
    }

    @Test
    @Transactional
    public void 모든채널불러오기_20개미만_참가자내림차순() {
        // given
        int testCase = 10;
        int firstIndex = 1;
        for(int i=1; i<=testCase; i++) {
            Channel channel = new Channel(i + channelName1, text);
            channelRepository.createChannel(channel, returnHashTags());
            if(i == firstIndex) {
                channelService.enterChannel(channel, email1);
            }
        }

        // when
        List<ChannelResponse> channels = channelService.findAnyChannel("partiDESC", 0);

        // then
        assertThat(channels.get(0).getChannelName()).isEqualTo(firstIndex + channelName1);
    }

    @Test
    @Transactional
    public void 모든채널불러오기_20개미만_참가자오름차순() {
        // given
        int testCase = 10;
        int firstIndex = 1;
        for(int i=1; i<=testCase; i++) {
            Channel channel = new Channel(i + channelName1, text);
            channelRepository.createChannel(channel, returnHashTags());
            if(i == firstIndex) {
                channelService.enterChannel(channel, email1);
            }
        }

        // when
        List<ChannelResponse> channels = channelService.findAnyChannel("partiASC", 0);

        // then
        assertThat(channels.get(testCase).getChannelName()).isEqualTo(firstIndex + channelName1);
    }

    @Test
    @Transactional
    public void 모든채널불러오기_20개초과_참가자내림차순() {
        // given
        int testCase = 30;
        int firstIndex = 1;
        for(int i=1; i<=testCase; i++) {
            Channel channel = new Channel(i + channelName1, text);
            channelRepository.createChannel(channel, returnHashTags());
            if(i == firstIndex) {
                channelService.enterChannel(channel, email1);
            }
        }

        // when
        List<ChannelResponse> channels0 = channelService.findAnyChannel("partiDESC", 0);

        // then
        assertThat(channels0.get(0).getChannelName()).isEqualTo(firstIndex + channelName1);
    }

    @Test
    @Transactional
    public void 모든채널불러오기_20개초과_참가자오름차순() {
        // given
        int testCase = 30;
        int firstIndex = 1;
        for(int i=1; i<=testCase; i++) {
            Channel channel = new Channel(i + channelName1, text);
            channelRepository.createChannel(channel, returnHashTags());
            if(i == firstIndex) {
                channelService.enterChannel(channel, email1);
            }
        }

        // when
        List<ChannelResponse> channels1 = channelService.findAnyChannel("partiASC", 1);

        // then
        assertThat(channels1.get((testCase)%20).getChannelName()).isEqualTo(firstIndex + channelName1);
    }

    @Test
    @Transactional
    public void 나의채널불러오기_20개미만_참가자내림차순() {
        // given
        int testCase = 10;
        int firstIndex = 1;
        for(int i=1; i<=testCase; i++) {
            Channel channel = new Channel(i + channelName1, text);
            channelRepository.createChannel(channel, returnHashTags());
            channelService.enterChannel(channel, email1);
            if(i == firstIndex) {
                channelService.enterChannel(channel, email2);
            }
        }

        // when
        List<ChannelResponse> channels = channelService.findMyChannel("partiDESC", email1,0);

        // then
        assertThat(channels.get(0).getChannelName()).isEqualTo(firstIndex + channelName1);
    }

    @Test
    @Transactional
    public void 나의채널불러오기_20개미만_참가자오름차순() {
        // given
        int testCase = 10;
        int firstIndex = 1;
        for(int i=1; i<=testCase; i++) {
            Channel channel = new Channel(i + channelName1, text);
            channelRepository.createChannel(channel, returnHashTags());
            channelService.enterChannel(channel, email1);
            if(i == firstIndex) {
                channelService.enterChannel(channel, email2);
            }
        }

        // when
        List<ChannelResponse> channels = channelService.findMyChannel("partiASC", email1,0);

        // then
        assertThat(channels.get(testCase-1).getChannelName()).isEqualTo(firstIndex + channelName1);
    }

    @Test
    @Transactional
    public void 나의채널불러오기_20개초과_참가자내림차순() {
        // given
        int testCase = 30;
        int firstIndex = 1;
        for(int i=1; i<=testCase; i++) {
            Channel channel = new Channel(i + channelName1, text);
            channelRepository.createChannel(channel, returnHashTags());
            channelService.enterChannel(channel, email1);
            if(i == firstIndex) {
                channelService.enterChannel(channel, email2);
            }
        }

        // when
        List<ChannelResponse> channels0 = channelService.findMyChannel("partiDESC", email1, 0);

        // then
        assertThat(channels0.get(0).getChannelName()).isEqualTo(firstIndex + channelName1);
    }

    @Test
    @Transactional
    public void 나의채널불러오기_20개초과_참가자오름차순() {
        // given
        int testCase = 30;
        int firstIndex = 1;
        for(int i=1; i<=testCase; i++) {
            Channel channel = new Channel(i + channelName1, text);
            channelRepository.createChannel(channel, returnHashTags());
            channelService.enterChannel(channel, email1);
            if(i == firstIndex) {
                channelService.enterChannel(channel, email2);
            }
        }

        // when
        List<ChannelResponse> channels1 = channelService.findMyChannel("partiASC", email1,1);

        // then
        assertThat(channels1.get((testCase-1)%20).getChannelName()).isEqualTo(firstIndex + channelName1);
    }

    @Test
    @Transactional
    public void 해시태그로_채널불러오기_20개미만_참가자내림차순() {
        // given
        int testCase = 10;
        int firstIndex = 1;
        for(int i=1; i<=testCase; i++) {
            Channel channel = new Channel(i + channelName1, text);
            channelRepository.createChannel(channel, returnHashTags());
            if(i == firstIndex) {
                channelService.enterChannel(channel, email1);
            }
        }

        // when
        List<ChannelResponse> channels = channelService.findChannelByHashName(tag1, "partiDESC",0);

        // then
        assertThat(channels.get(0).getChannelName()).isEqualTo(firstIndex + channelName1);
    }

    @Test
    @Transactional
    public void 해시태그로_채널불러오기_20개미만_참가자오름차순() {
        // given
        int testCase = 10;
        int firstIndex = 1;
        for(int i=1; i<=testCase; i++) {
            Channel channel = new Channel(i + channelName1, text);
            channelRepository.createChannel(channel, returnHashTags());
            if(i == firstIndex) {
                channelService.enterChannel(channel, email1);
            }
        }

        // when
        List<ChannelResponse> channels = channelService.findChannelByHashName(tag1, "partiASC",0);

        // then
        assertThat(channels.get(testCase).getChannelName()).isEqualTo(firstIndex + channelName1);
    }

    @Test
    @Transactional
    public void 해시태그로_채널불러오기_20개초과_참가자내림차순() {
        // given
        int testCase = 30;
        int firstIndex = 1;
        for(int i=1; i<=testCase; i++) {
            Channel channel = new Channel(i + channelName1, text);
            channelRepository.createChannel(channel, returnHashTags());
            if(i == firstIndex) {
                channelService.enterChannel(channel, email1);
            }
        }

        // when
        List<ChannelResponse> channels0 = channelService.findChannelByHashName(tag1, "partiDESC",0);

        // then
        assertThat(channels0.get(0).getChannelName()).isEqualTo(firstIndex + channelName1);
    }

    @Test
    @Transactional
    public void 해시태그로_채널불러오기_20개초과_참가자오름차순() {
        // given
        int testCase = 30;
        int firstIndex = 1;
        for(int i=1; i<=testCase; i++) {
            Channel channel = new Channel(i + channelName1, text);
            channelRepository.createChannel(channel, returnHashTags());
            if(i == firstIndex) {
                channelService.enterChannel(channel, email1);
            }
        }

        // when
        List<ChannelResponse> channels1 = channelService.findChannelByHashName(tag1, "partiASC",1);

        // then
        assertThat(channels1.get((testCase)%20).getChannelName()).isEqualTo(firstIndex + channelName1);
    }

    @Test
    @Transactional
    public void 채널ID로_채널찾기성공() {
        // given
        Channel channel = new Channel(channelName1, text);
        channelRepository.createChannel(channel, returnHashTags());

        // when
        Channel findChannel = channelService.findOneChannelById(channel.getId());

        // then
        assertThat(findChannel).isEqualTo(channel);
    }

    @Test
    @Transactional
    public void 채널ID로_채널찾기실패() {
        // given
        Channel channel = new Channel(channelName1, text);

        // when


        // then
        assertThrows(NotExistChannelException.class, ()-> {
            channelService.findOneChannelById(channel.getId());
        });
    }


//    @Test
//    @Transactional
//    public void 채널수명연장성공() {
//        // given
//        Channel channel = new Channel(channelName1, text);
//        channelRepository.createChannel(channel, returnHashTags());
//
//        // when
//        channelService.extensionChannelTTL(channel.getId(), email1, 100L);
//
//        // then
//
//    }
//
//    @Test
//    @DisplayName("Extension ChannelTTL 실패 by InsufficientPointException")
//    public void extensionChannelTTLFailByInsufficientPointException() {
//        // given
//        Channel channel = createChannelTemp();
//
//        // when
//
//        // then
//
//        assertThrows(InsufficientPointException.class,
//                () -> channelService.extensionChannelTTL(channel.getId(), "email", 1000000L));
//    }

    public List<String> returnHashTags() {
        List<String> hashTags = new ArrayList<>();
        hashTags.add(tag1);
        hashTags.add(tag2);
        hashTags.add(tag3);
        return hashTags;
    }
}
