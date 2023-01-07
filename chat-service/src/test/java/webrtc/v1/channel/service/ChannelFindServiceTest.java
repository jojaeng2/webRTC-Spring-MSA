package webrtc.v1.channel.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static webrtc.v1.staticgenarator.HashTagGenerator.createHashTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import webrtc.v1.channel.dto.ChannelDto.FindChannelByHashTagDto;
import webrtc.v1.channel.dto.ChannelDto.FindChannelDto;
import webrtc.v1.channel.dto.ChannelDto.FindMyChannelDto;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.exception.ChannelException.NotExistChannelException;
import webrtc.v1.channel.repository.ChannelCrudRepository;
import webrtc.v1.channel.repository.ChannelListRepository;
import webrtc.v1.hashtag.entity.HashTag;
import webrtc.v1.hashtag.exception.HashTagException.NotExistHashTagException;
import webrtc.v1.hashtag.repository.HashTagRepository;
import webrtc.v1.staticgenarator.ChannelGenerator;
import webrtc.v1.staticgenarator.UserGenerator;
import webrtc.v1.user.exception.UserException.NotExistUserException;
import webrtc.v1.user.repository.UsersRepository;

@ExtendWith(MockitoExtension.class)
public class ChannelFindServiceTest {

  @InjectMocks
  private ChannelFindServiceImpl channelFindService;

  @Mock
  private ChannelListRepository channelListRepository;
  @Mock
  private ChannelCrudRepository channelCrudRepository;
  @Mock
  private UsersRepository usersRepository;
  @Mock
  private HashTagRepository hashTagRepository;

  @Mock
  private ChannelInfoInjectService channelInfoInjectService;
  private static final int mini = 15;
  private static final int maxi = 40;
  private static final String asc = "partiASC";
  private static final String desc = "partiDESC";
  private static final String userId = "userId";
  private static final String tagName = "해시태그#1";

  private static final Integer idx = 0;

  @Test
  void ID로채널찾기성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();

    doReturn(Optional.of(channel))
        .when(channelCrudRepository).findById(any(String.class));
    doReturn(channel)
        .when(channelInfoInjectService).setTtl(any(Channel.class));

    // when
    Channel findChannel = channelFindService.findById(channel.getId());

    // then
    assertThat(channel.getId()).isEqualTo(findChannel.getId());
  }

  @Test
  void ID로채널찾기실패채널없음() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();

    doReturn(Optional.empty())
        .when(channelCrudRepository).findById(any(String.class));

    // when

    // then
    assertThrows(NotExistChannelException.class,
        () -> channelFindService.findById(channel.getId()));
  }

  @Test
  void 모든채널목록성공20개미만내림차순() {
    // given
    doReturn(channelListUnder())
        .when(channelListRepository).findAnyChannels(any(Integer.class), any(String.class));

    // when
    List<Channel> result = channelFindService.findAnyChannel(createFindChannelDtoASC());

    // then
    assertThat(result.size()).isEqualTo(mini);
  }

  @Test
  void 모든채널목록성공20개미만오름차순() {
    // given
    doReturn(channelListUnder())
        .when(channelListRepository).findAnyChannels(any(Integer.class), any(String.class));

    // when
    List<Channel> result = channelFindService.findAnyChannel(createFindChannelDtoASC());

    // then
    assertThat(result.size()).isEqualTo(mini);
  }

  @Test
  void 모든채널목록성공20개이상내림차순() {
    // given
    doReturn(channelListOver())
        .when(channelListRepository).findAnyChannels(any(Integer.class), any(String.class));

    // when
    List<Channel> result = channelFindService.findAnyChannel(createFindChannelDtoASC());

    // then
    assertThat(result.size()).isEqualTo(maxi);

  }

  @Test
  void 모든채널목록성공20개이상오름차순() {
    // given
    doReturn(channelListOver())
        .when(channelListRepository).findAnyChannels(any(Integer.class), any(String.class));

    // when
    List<Channel> result = channelFindService.findAnyChannel(createFindChannelDtoDESC());

    // then
    assertThat(result.size()).isEqualTo(maxi);

  }


  @Test
  void 나의채널목록성공20개미만오름차순() {
    // given
    doReturn(Optional.of(UserGenerator.createUsers()))
        .when(usersRepository).findById(any(String.class));

    doReturn(channelListUnder())
        .when(channelListRepository)
        .findMyChannels(any(String.class), any(Integer.class), any(String.class));

    // when
    List<Channel> result = channelFindService.findMyChannel(createFindMyChannelDtoASC());

    // then
    assertThat(result.size()).isEqualTo(mini);
  }

  @Test
  void 나의채널목록성공20개미만내림차순() {
    // given
    doReturn(Optional.of(UserGenerator.createUsers()))
        .when(usersRepository).findById(any(String.class));

    doReturn(channelListUnder())
        .when(channelListRepository)
        .findMyChannels(any(String.class), any(Integer.class), any(String.class));

    // when
    List<Channel> result = channelFindService.findMyChannel(createFindMyChannelDtoDESC());

    // then
    assertThat(result.size()).isEqualTo(mini);
  }


  @Test
  void 나의채널목록성공20개이상오름차순() {
    // given
    doReturn(Optional.of(UserGenerator.createUsers()))
        .when(usersRepository).findById(any(String.class));
    doReturn(channelListOver())
        .when(channelListRepository)
        .findMyChannels(any(String.class), any(Integer.class), any(String.class));

    // when
    List<Channel> result = channelFindService.findMyChannel(createFindMyChannelDtoASC());

    // then
    assertThat(result.size()).isEqualTo(maxi);
  }

  @Test
  void 나의채널목록성공20개이상내림차순() {
    // given
    doReturn(Optional.of(UserGenerator.createUsers()))
        .when(usersRepository).findById(any(String.class));
    doReturn(channelListOver())
        .when(channelListRepository)
        .findMyChannels(any(String.class), any(Integer.class), any(String.class));

    // when
    List<Channel> result = channelFindService.findMyChannel(createFindMyChannelDtoDESC());

    // then
    assertThat(result.size()).isEqualTo(maxi);
  }

  @Test
  void 나의채널목록실패유저없음() {
    // given
    doReturn(Optional.empty())
        .when(usersRepository).findById(any(String.class));

    // when

    // then
    assertThrows(NotExistUserException.class,
        () -> channelFindService.findMyChannel(new FindMyChannelDto("partiASC", "1", 0)));
  }

  @Test
  void 최근대화방20개미만오름차순() {
    // given
    doReturn(channelListUnder())
        .when(channelListRepository)
        .findChannelsRecentlyTalk(any(Integer.class), any(String.class));

    // when
    List<Channel> result = channelFindService.findChannelsRecentlyTalk(createFindChannelDtoASC());

    // then
    assertThat(result.size()).isEqualTo(mini);
  }


  @Test
  void 최근대화방20개미만내림차순() {
    // given
    doReturn(channelListUnder())
        .when(channelListRepository)
        .findChannelsRecentlyTalk(any(Integer.class), any(String.class));

    // when
    List<Channel> result = channelFindService.findChannelsRecentlyTalk(createFindChannelDtoDESC());

    // then
    assertThat(result.size()).isEqualTo(mini);
  }

  @Test
  void 최근대화방20개이상오름차순() {
    // given
    doReturn(channelListOver())
        .when(channelListRepository)
        .findChannelsRecentlyTalk(any(Integer.class), any(String.class));

    // when
    List<Channel> result = channelFindService.findChannelsRecentlyTalk(createFindChannelDtoASC());

    // then
    assertThat(result.size()).isEqualTo(maxi);
  }


  @Test
  void 최근대화방20개이상내림차순() {
    // given
    doReturn(channelListOver())
        .when(channelListRepository)
        .findChannelsRecentlyTalk(any(Integer.class), any(String.class));

    // when
    List<Channel> result = channelFindService.findChannelsRecentlyTalk(createFindChannelDtoDESC());

    // then
    assertThat(result.size()).isEqualTo(maxi);
  }

  @Test
  void 해시태그채널목록성공20개미만오름차순() {
    // given
    doReturn(Optional.of(createHashTag()))
        .when(hashTagRepository).findByName(any(String.class));
    doReturn(channelListUnder())
        .when(channelListRepository)
        .findChannelsByHashName(any(HashTag.class), any(Integer.class), any(String.class));
    // when
    List<Channel> result = channelFindService.findByHashName(createFindChannelByHashTagDtoASC());

    // then
    assertThat(result.size()).isEqualTo(mini);
  }

  @Test
  void 해시태그채널목록성공20개미만내림차순() {
    // given
    doReturn(Optional.of(createHashTag()))
        .when(hashTagRepository).findByName(any(String.class));
    doReturn(channelListUnder())
        .when(channelListRepository)
        .findChannelsByHashName(any(HashTag.class), any(Integer.class), any(String.class));
    // when
    List<Channel> result = channelFindService.findByHashName(createFindChannelByHashTagDtoDESC());

    // then
    assertThat(result.size()).isEqualTo(mini);
  }

  @Test
  void 해시태그채널목록성공20개이상오름차순() {
    // given
    doReturn(Optional.of(createHashTag()))
        .when(hashTagRepository).findByName(any(String.class));
    doReturn(channelListOver())
        .when(channelListRepository)
        .findChannelsByHashName(any(HashTag.class), any(Integer.class), any(String.class));

    // when
    List<Channel> result = channelFindService.findByHashName(createFindChannelByHashTagDtoASC());

    // then
    assertThat(result.size()).isEqualTo(maxi);
  }

  @Test
  void 해시태그채널목록성공20개이상내림차순() {
    // given
    doReturn(Optional.of(createHashTag()))
        .when(hashTagRepository).findByName(any(String.class));
    doReturn(channelListOver())
        .when(channelListRepository)
        .findChannelsByHashName(any(HashTag.class), any(Integer.class), any(String.class));

    // when
    List<Channel> result = channelFindService.findByHashName(createFindChannelByHashTagDtoDESC());

    // then
    assertThat(result.size()).isEqualTo(maxi);
  }

  @Test
  void 해시태그채널목록실패태그없음() {
    // given
    doThrow(NotExistHashTagException.class)
        .when(hashTagRepository).findByName(any(String.class));

    // when

    // then
    assertThrows(NotExistHashTagException.class,
        () -> channelFindService.findByHashName(createFindChannelByHashTagDtoASC()));

  }


  private List<Channel> channelListUnder() {
    List<Channel> channels = new ArrayList<>();
    for (int i = 0; i < mini; i++) {
      Channel channel = ChannelGenerator.createTextChannel();
      channels.add(channel);
    }
    return channels;
  }

  private List<Channel> channelListOver() {
    List<Channel> channels = new ArrayList<>();
    for (int i = 0; i < maxi; i++) {
      Channel channel = ChannelGenerator.createTextChannel();
      channels.add(channel);
    }
    return channels;
  }

  private FindChannelDto createFindChannelDtoASC() {
    return new FindChannelDto(asc, idx);
  }

  private FindChannelDto createFindChannelDtoDESC() {
    return new FindChannelDto(desc, idx);
  }

  private FindMyChannelDto createFindMyChannelDtoASC() {
    return new FindMyChannelDto(asc, userId, idx);
  }


  private FindMyChannelDto createFindMyChannelDtoDESC() {
    return new FindMyChannelDto(desc, userId, idx);
  }

  private FindChannelByHashTagDto createFindChannelByHashTagDtoASC() {
    return new FindChannelByHashTagDto(tagName, asc, idx);
  }

  private FindChannelByHashTagDto createFindChannelByHashTagDtoDESC() {
    return new FindChannelByHashTagDto(tagName, desc, idx);
  }
}
