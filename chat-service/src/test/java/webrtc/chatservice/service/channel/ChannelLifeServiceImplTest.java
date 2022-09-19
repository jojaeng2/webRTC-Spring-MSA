package webrtc.chatservice.service.channel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.controller.HttpApiController;
import webrtc.chatservice.domain.*;
import webrtc.chatservice.dto.ChannelDto.CreateChannelRequest;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.enums.ClientMessageType;
import webrtc.chatservice.exception.ChannelException.AlreadyExistChannelException;
import webrtc.chatservice.exception.ChannelException.NotExistChannelException;
import webrtc.chatservice.exception.HashTagException.NotExistHashTagException;
import webrtc.chatservice.exception.PointException;
import webrtc.chatservice.exception.PointException.InsufficientPointException;
import webrtc.chatservice.exception.UserException.NotExistUserException;
import webrtc.chatservice.repository.channel.*;
import webrtc.chatservice.repository.chat.ChatLogRepository;
import webrtc.chatservice.repository.hashtag.HashTagRepository;
import webrtc.chatservice.repository.users.UsersRepository;
import webrtc.chatservice.service.chat.factory.ChattingMessageFactory;
import webrtc.chatservice.service.rabbit.RabbitPublish;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static webrtc.chatservice.enums.ChannelType.TEXT;
import static webrtc.chatservice.enums.ChannelType.VOIP;

@ExtendWith(MockitoExtension.class)
public class ChannelLifeServiceImplTest {

    @InjectMocks
    private ChannelLifeServiceImpl channelService;


    @Mock
    private ChannelCrudRepository crudRepository;
    @Mock
    private ChannelListRepository channelListRepository;
    @Mock
    private ChannelRedisRepository channelRedisRepository;

    @Mock
    private RabbitPublish rabbitPublish;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private HashTagRepository hashTagRepository;
    @Mock
    private ChattingMessageFactory chattingMessageFactory;
    @Mock
    private HttpApiController httpApiController;


    String nickname1 = "nickname1";
    String nickname2 = "nickname2";
    String password = "password";
    String email1 = "email1";
    String email2 = "email2";
    String email = "email";

    String channelName1 = "channelName1";
    String channelName2 = "channelName2";
    String tag1 = "tag1";
    String tag2 = "tag2";
    String tag3 = "tag3";
    ChannelType text = TEXT;
    ChannelType voip = VOIP;

    @Test
    void 채널생성성공_회원존재_태그존재_포인트존재() {
        // given
        doReturn(new ArrayList<>())
                .when(channelListRepository).findChannelByChannelName(any(String.class));

        doReturn(Optional.of(createUser()))
                .when(usersRepository).findUserByEmail(any(String.class));
        doReturn(Optional.of(createTag(tag1)))
                .when(hashTagRepository).findHashTagByName(any(String.class));

        // when
        Channel channel = channelService.createChannel(createChannelRequest(), email);

        //then
        assertThat(channel.getChannelHashTags().size()).isEqualTo(3);
        assertThat(channel.getChannelUsers().size()).isEqualTo(1);
        assertThat(channel.getChatLogs().size()).isEqualTo(1);
    }




    @Test
    void 채널생성성공_회원존재_태그없음_포인트존재() {
        // given
        doReturn(new ArrayList<>())
                .when(channelListRepository).findChannelByChannelName(channelName1);

        doReturn(Optional.of(createUser()))
                .when(usersRepository).findUserByEmail(email1);

        doReturn(Optional.ofNullable(null))
                .when(hashTagRepository).findHashTagByName(any(String.class));

        // when
        Channel channel = channelService.createChannel(createChannelRequest(), email1);

        // then
        assertThat(channel.getChannelHashTags().size()).isEqualTo(3);
        assertThat(channel.getChannelUsers().size()).isEqualTo(1);
    }



    @Test
    void 채널생성성공_회원통신성공_태그존재_포인트존재() {
        // given

        doReturn(new ArrayList<>())
                .when(channelListRepository).findChannelByChannelName(channelName1);

        doReturn(Optional.ofNullable(null))
                .when(usersRepository).findUserByEmail(any(String.class));

        doReturn(createUser())
                .when(httpApiController).postFindUserByEmail(any(String.class));

        doReturn(Optional.of(createTag(tag1)))
                .when(hashTagRepository).findHashTagByName(any(String.class));


        // when
        Channel channel = channelService.createChannel(createChannelRequest(), email1);

        // then
        assertThat(channel.getChannelName()).isEqualTo(channelName1);
        assertThat(channel.getChannelHashTags().size()).isEqualTo(3);
    }


    @Test
    void 채널생성실패_채널이름중복() {
        // given
        doReturn(List.of(createChannel(channelName1, text)))
                .when(channelListRepository).findChannelByChannelName(any(String.class));

        // when

        // then
        assertThrows(AlreadyExistChannelException.class, () -> {
            channelService.createChannel(createChannelRequest(), email1);
        });
    }

    @Test
    void 채널생성실패_포인트부족() {
        // given
//        doThrow(InsufficientPointException::new)
//                .when(httpApiController).postDecreaseUserPoint(any(), any());
        // when
        channelService.createChannel(createChannelRequest(), email);

        // then

    }

//
//    @Test
//    @Transactional
//    public void 채널생성실패_채널이름_중복() {
//        // given
//        List<String> hashTags = new ArrayList<>();
//        hashTags.add(tag1);
//        hashTags.add(tag2);
//        hashTags.add(tag3);
//
//        CreateChannelRequest request = new CreateChannelRequest(channelName1, hashTags, text);
//        doReturn(new Channel(channelName1, text))
//                .when(channelListRepository).findChannelByChannelName(channelName1);
//
//
//        // when
//
//        // then
//        assertThrows(AlreadyExistChannelException.class, ()-> {
//            channelService.createChannel(request, email1);
//        });
//    }
//
//
////    @Test
////    @Transactional
////    public void 채널입장성공_유저이미존재_처음입장() {
////        // given
////        Channel channel = new Channel(channelName1, text);
////
////        doReturn(new Users(nickname1, password, email1))
////                .when(usersRepository).findUserByEmail(email1);
////
////        doThrow(new NotExistChannelException())
////                .when(channelListRepository).findChannelsByChannelIdAndUserId(any(String.class), any(String.class));
////
////        // when
////
////        // then
////        Channel enterChannel = channelService.enterChannel(channel.getId(), email1);
////        assertThat(enterChannel.getCurrentParticipants()).isEqualTo(1);
////    }
//
////    @Test
////    @Transactional
////    public void 채널입장실패_유저이미존재_이전에입장() {
////        // given
////        Channel channel = new Channel(channelName1, text);
////
////        doReturn(new Users(nickname1, password, email1))
////                .when(usersRepository).findUserByEmail(email1);
////
////        doReturn(new ArrayList<>())
////                .when(channelListRepository).findChannelsByChannelIdAndUserId(any(String.class), any(String.class));
////
////        // when
////
////        // then
////        assertThat(channel.getCurrentParticipants()).isEqualTo(0);
////        assertThrows(AlreadyExistUserInChannelException.class, ()->{
////            channelService.enterChannel(channel.getId(), email1);
////        });
////    }
//
////    @Test
////    @Transactional
////    public void 채널입장실패_유저이미존재_인원제한() {
////        // given
////        Channel channel = new Channel(channelName1, text);
////        doReturn(new Users(nickname1, password, email1))
////                .when(usersRepository).findUserByEmail(any(String.class));
////
////        doReturn(111L)
////                .when(channelRedisRepository).findChannelTTL(channel.getId());
////
////        doThrow(new NotExistChannelException())
////                .when(channelListRepository).findChannelsByChannelIdAndUserId(any(String.class), any(String.class));
////
////        for(int i=0; i<14; i++) {
////            channelService.enterChannel(channel.getId(), email1+ i);
////
////        }
////        // when
////
////        // then
////        assertThrows(ChannelParticipantsFullException.class, ()->{
////            channelService.enterChannel(channel.getId(), email1);
////        });
////    }
//
////    @Test
////    @Transactional
////    public void 채널입장성공_유저통신성공() {
////        // given
////        Channel channel = new Channel(channelName1, text);
////
////        doThrow(new NotExistUserException())
////                .when(usersRepository).findUserByEmail(any(String.class));
////
////        doReturn(new Users(nickname1, password, email1))
////                .when(httpApiController).postFindUserByEmail(email1);
////        doNothing()
////                .when(usersRepository).saveUser(any(Users.class));
////
////        doThrow(new NotExistChannelException())
////                .when(channelListRepository).findChannelsByChannelIdAndUserId(any(String.class), any(String.class));
////
////        // when
////
////        // then
////        Channel enterChannel = channelService.enterChannel(channel.getId(), email1);
////
////        assertThat(enterChannel.getCurrentParticipants()).isEqualTo(1);
////    }
//
//    @Test
//    @Transactional
//    public void 채널삭제성공() {
//        // given
//        Channel channel = new Channel(channelName1, text);
//
//        doReturn(Optional.of(channel))
//                .when(crudRepository).findById(any(String.class));
//
//        doNothing()
//                .when(crudRepository).delete(any(Channel.class));
//
//        // when
//        channelService.deleteChannel(channel.getId());
//
//        // then
//    }
//
//    @Test
//    @Transactional
//    public void 채널삭제실패_채널없음() {
//        // given
//        Channel channel = new Channel(channelName1, text);
//
//        doThrow(new NotExistChannelException())
//                .when(crudRepository).findById(any(String.class));
//
//        // when
//
//
//        // then
//        assertThrows(NotExistChannelException.class, () -> {
//            channelService.deleteChannel(channel.getId());
//        });
//    }
////
////    @Test
////    @Transactional
////    public void 모든채널불러오기_20개미만_참가자내림차순() {
////        // given
////        int testCase = 10;
////        int firstIndex = 1;
////        for(int i=1; i<=testCase; i++) {
////            Channel channel = new Channel(i + channelName1, text);
////            channelListRepository.createChannel(channel, returnHashTags());
////            if(i == firstIndex) {
////                channelService.enterChannel(channel, email1);
////            }
////        }
////
////        // when
////        List<ChannelResponse> channels = channelService.findAnyChannel("partiDESC", 0);
////
////        // then
////        assertThat(channels.get(0).getChannelName()).isEqualTo(firstIndex + channelName1);
////    }
////
////    @Test
////    @Transactional
////    public void 모든채널불러오기_20개미만_참가자오름차순() {
////        // given
////        int testCase = 10;
////        int firstIndex = 1;
////        for(int i=1; i<=testCase; i++) {
////            Channel channel = new Channel(i + channelName1, text);
////            channelListRepository.createChannel(channel, returnHashTags());
////            if(i == firstIndex) {
////                channelService.enterChannel(channel, email1);
////            }
////        }
////
////        // when
////        List<ChannelResponse> channels = channelService.findAnyChannel("partiASC", 0);
////
////        // then
////        assertThat(channels.get(testCase).getChannelName()).isEqualTo(firstIndex + channelName1);
////    }
////
////    @Test
////    @Transactional
////    public void 모든채널불러오기_20개초과_참가자내림차순() {
////        // given
////        int testCase = 30;
////        int firstIndex = 1;
////        for(int i=1; i<=testCase; i++) {
////            Channel channel = new Channel(i + channelName1, text);
////            channelListRepository.createChannel(channel, returnHashTags());
////            if(i == firstIndex) {
////                channelService.enterChannel(channel, email1);
////            }
////        }
////
////        // when
////        List<ChannelResponse> channels0 = channelService.findAnyChannel("partiDESC", 0);
////
////        // then
////        assertThat(channels0.get(0).getChannelName()).isEqualTo(firstIndex + channelName1);
////    }
////
////    @Test
////    @Transactional
////    public void 모든채널불러오기_20개초과_참가자오름차순() {
////        // given
////        int testCase = 30;
////        int firstIndex = 1;
////        for(int i=1; i<=testCase; i++) {
////            Channel channel = new Channel(i + channelName1, text);
////            channelListRepository.createChannel(channel, returnHashTags());
////            if(i == firstIndex) {
////                channelService.enterChannel(channel, email1);
////            }
////        }
////
////        // when
////        List<ChannelResponse> channels1 = channelService.findAnyChannel("partiASC", 1);
////
////        // then
////        assertThat(channels1.get((testCase)%20).getChannelName()).isEqualTo(firstIndex + channelName1);
////    }
////
////    @Test
////    @Transactional
////    public void 나의채널불러오기_20개미만_참가자내림차순() {
////        // given
////        int testCase = 10;
////        int firstIndex = 1;
////        for(int i=1; i<=testCase; i++) {
////            Channel channel = new Channel(i + channelName1, text);
////            channelListRepository.createChannel(channel, returnHashTags());
////            channelService.enterChannel(channel, email1);
////            if(i == firstIndex) {
////                channelService.enterChannel(channel, email2);
////            }
////        }
////
////        // when
////        List<ChannelResponse> channels = channelService.findMyChannel("partiDESC", email1,0);
////
////        // then
////        assertThat(channels.get(0).getChannelName()).isEqualTo(firstIndex + channelName1);
////    }
////
////    @Test
////    @Transactional
////    public void 나의채널불러오기_20개미만_참가자오름차순() {
////        // given
////        int testCase = 10;
////        int firstIndex = 1;
////        for(int i=1; i<=testCase; i++) {
////            Channel channel = new Channel(i + channelName1, text);
////            channelListRepository.createChannel(channel, returnHashTags());
////            channelService.enterChannel(channel, email1);
////            if(i == firstIndex) {
////                channelService.enterChannel(channel, email2);
////            }
////        }
////
////        // when
////        List<ChannelResponse> channels = channelService.findMyChannel("partiASC", email1,0);
////
////        // then
////        assertThat(channels.get(testCase-1).getChannelName()).isEqualTo(firstIndex + channelName1);
////    }
////
////    @Test
////    @Transactional
////    public void 나의채널불러오기_20개초과_참가자내림차순() {
////        // given
////        int testCase = 30;
////        int firstIndex = 1;
////        for(int i=1; i<=testCase; i++) {
////            Channel channel = new Channel(i + channelName1, text);
////            channelListRepository.createChannel(channel, returnHashTags());
////            channelService.enterChannel(channel, email1);
////            if(i == firstIndex) {
////                channelService.enterChannel(channel, email2);
////            }
////        }
////
////        // when
////        List<ChannelResponse> channels0 = channelService.findMyChannel("partiDESC", email1, 0);
////
////        // then
////        assertThat(channels0.get(0).getChannelName()).isEqualTo(firstIndex + channelName1);
////    }
////
////    @Test
////    @Transactional
////    public void 나의채널불러오기_20개초과_참가자오름차순() {
////        // given
////        int testCase = 30;
////        int firstIndex = 1;
////        for(int i=1; i<=testCase; i++) {
////            Channel channel = new Channel(i + channelName1, text);
////            channelListRepository.createChannel(channel, returnHashTags());
////            channelService.enterChannel(channel, email1);
////            if(i == firstIndex) {
////                channelService.enterChannel(channel, email2);
////            }
////        }
////
////        // when
////        List<ChannelResponse> channels1 = channelService.findMyChannel("partiASC", email1,1);
////
////        // then
////        assertThat(channels1.get((testCase-1)%20).getChannelName()).isEqualTo(firstIndex + channelName1);
////    }
////
////    @Test
////    @Transactional
////    public void 해시태그로_채널불러오기_20개미만_참가자내림차순() {
////        // given
////        int testCase = 10;
////        int firstIndex = 1;
////        for(int i=1; i<=testCase; i++) {
////            Channel channel = new Channel(i + channelName1, text);
////            channelListRepository.createChannel(channel, returnHashTags());
////            if(i == firstIndex) {
////                channelService.enterChannel(channel, email1);
////            }
////        }
////
////        // when
////        List<ChannelResponse> channels = channelService.findChannelByHashName(tag1, "partiDESC",0);
////
////        // then
////        assertThat(channels.get(0).getChannelName()).isEqualTo(firstIndex + channelName1);
////    }
////
////    @Test
////    @Transactional
////    public void 해시태그로_채널불러오기_20개미만_참가자오름차순() {
////        // given
////        int testCase = 10;
////        int firstIndex = 1;
////        for(int i=1; i<=testCase; i++) {
////            Channel channel = new Channel(i + channelName1, text);
////            channelListRepository.createChannel(channel, returnHashTags());
////            if(i == firstIndex) {
////                channelService.enterChannel(channel, email1);
////            }
////        }
////
////        // when
////        List<ChannelResponse> channels = channelService.findChannelByHashName(tag1, "partiASC",0);
////
////        // then
////        assertThat(channels.get(testCase).getChannelName()).isEqualTo(firstIndex + channelName1);
////    }
////
////    @Test
////    @Transactional
////    public void 해시태그로_채널불러오기_20개초과_참가자내림차순() {
////        // given
////        int testCase = 30;
////        int firstIndex = 1;
////        for(int i=1; i<=testCase; i++) {
////            Channel channel = new Channel(i + channelName1, text);
////            channelListRepository.createChannel(channel, returnHashTags());
////            if(i == firstIndex) {
////                channelService.enterChannel(channel, email1);
////            }
////        }
////
////        // when
////        List<ChannelResponse> channels0 = channelService.findChannelByHashName(tag1, "partiDESC",0);
////
////        // then
////        assertThat(channels0.get(0).getChannelName()).isEqualTo(firstIndex + channelName1);
////    }
////
////    @Test
////    @Transactional
////    public void 해시태그로_채널불러오기_20개초과_참가자오름차순() {
////        // given
////        int testCase = 30;
////        int firstIndex = 1;
////        for(int i=1; i<=testCase; i++) {
////            Channel channel = new Channel(i + channelName1, text);
////            channelListRepository.createChannel(channel, returnHashTags());
////            if(i == firstIndex) {
////                channelService.enterChannel(channel, email1);
////            }
////        }
////
////        // when
////        List<ChannelResponse> channels1 = channelService.findChannelByHashName(tag1, "partiASC",1);
////
////        // then
////        assertThat(channels1.get((testCase)%20).getChannelName()).isEqualTo(firstIndex + channelName1);
////    }
////
////    @Test
////    @Transactional
////    public void 채널ID로_채널찾기성공() {
////        // given
////        Channel channel = new Channel(channelName1, text);
////        channelListRepository.createChannel(channel, returnHashTags());
////
////        // when
////        Channel findChannel = channelService.findOneChannelById(channel.getId());
////
////        // then
////        assertThat(findChannel).isEqualTo(channel);
////    }
////
////    @Test
////    @Transactional
////    public void 채널ID로_채널찾기실패() {
////        // given
////        Channel channel = new Channel(channelName1, text);
////
////        // when
////
////
////        // then
////        assertThrows(NotExistChannelException.class, ()-> {
////            channelService.findOneChannelById(channel.getId());
////        });
////    }
//
//
////    @Test
////    @Transactional
////    public void 채널수명연장성공() {
////        // given
////        Channel channel = new Channel(channelName1, text);
////        channelRepository.createChannel(channel, returnHashTags());
////
////        // when
////        channelService.extensionChannelTTL(channel.getId(), email1, 100L);
////
////        // then
////
////    }
////
////    @Test
////    @DisplayName("Extension ChannelTTL 실패 by InsufficientPointException")
////    public void extensionChannelTTLFailByInsufficientPointException() {
////        // given
////        Channel channel = createChannelTemp();
////
////        // when
////
////        // then
////
////        assertThrows(InsufficientPointException.class,
////                () -> channelService.extensionChannelTTL(channel.getId(), "email", 1000000L));
////    }

    private CreateChannelRequest createChannelRequest() {
        List<String> hashTags = new ArrayList<>();
        hashTags.add(tag1);
        hashTags.add(tag2);
        hashTags.add(tag3);

        return new CreateChannelRequest(channelName1, hashTags, text);
    }

    private Users createUser() {
        return new Users(nickname1, password, email1);
    }

    private HashTag createTag(String name) {
        return new HashTag(name);
    }

    private Channel createChannel(String name, ChannelType type) {
        return new Channel(name, type);
    }
}
