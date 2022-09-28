package webrtc.chatservice.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.HashTag;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.ChannelDto.ChannelResponse;
import webrtc.chatservice.exception.ChannelException;
import webrtc.chatservice.exception.ChannelException.NotExistChannelException;
import webrtc.chatservice.exception.HashTagException;
import webrtc.chatservice.exception.HashTagException.NotExistHashTagException;
import webrtc.chatservice.exception.UserException;
import webrtc.chatservice.exception.UserException.NotExistUserException;
import webrtc.chatservice.repository.channel.ChannelCrudRepository;
import webrtc.chatservice.repository.channel.ChannelListRepository;
import webrtc.chatservice.repository.hashtag.HashTagRepository;
import webrtc.chatservice.repository.users.UsersRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChannelFindServiceImpl implements ChannelFindService {

    private final ChannelListRepository channelListRepository;
    private final ChannelCrudRepository channelCrudRepository;
    private final UsersRepository usersRepository;
    private final HashTagRepository hashTagRepository;
    private final ChannelInfoInjectService channelInfoInjectService;



    /*
     * 비즈니스 로직 - 특정 채널 ID로 찾기
     */
    @Transactional(readOnly = true)
    public Channel findOneChannelById(String channelId) {
        Channel channel = channelCrudRepository.findById(channelId)
                .orElseThrow(NotExistChannelException::new);
        return channelInfoInjectService.setChannelTTL(channel);
    }

    /*
     * 비즈니스 로직 - 모든 채널 불러오기
     *
     */
    @Transactional(readOnly = true)
    public List<ChannelResponse> findAnyChannel(String orderType, int idx) {
        switch (orderType) {
            case "partiASC" :
                return channelInfoInjectService.setReturnChannelsTTL(channelListRepository.findAnyChannels(idx, "asc"));
            case "partiDESC" :
                return channelInfoInjectService.setReturnChannelsTTL(channelListRepository.findAnyChannels(idx, "desc"));
        }
        return new ArrayList<>();
    }

    /*
     * 비즈니스 로직 - 입장한 모든 채널 불러오기
     *
     */
    @Transactional(readOnly = true)
    public List<ChannelResponse> findMyChannel(String orderType, String email, int idx) {
        Users user = usersRepository.findUserByEmail(email)
                .orElseThrow(NotExistUserException::new);

        switch (orderType) {
            case "partiASC" :
                return channelInfoInjectService.setReturnChannelsTTL(channelListRepository.findMyChannels(user.getId(), idx, "asc"));
            case "partiDESC" :
                return channelInfoInjectService.setReturnChannelsTTL(channelListRepository.findMyChannels(user.getId(), idx, "desc"));
        }
        return new ArrayList<>();
    }

    /*
     * 비즈니스 로직 - 해시태그로 채널찾기
     */
    @Transactional(readOnly = true)
    public List<ChannelResponse> findChannelByHashName(String tagName, String orderType, int idx) {
        HashTag hashTag = hashTagRepository.findHashTagByName(tagName)
                .orElseThrow(NotExistHashTagException::new);
        switch (orderType) {
            case "partiASC" :
                return channelInfoInjectService.setReturnChannelsTTL(channelListRepository.findChannelsByHashName(hashTag, idx, "asc"));
            case "partiDESC" :
                return channelInfoInjectService.setReturnChannelsTTL(channelListRepository.findChannelsByHashName(hashTag, idx, "desc"));
        }
        return new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public List<ChannelResponse> findChannelsRecentlyTalk(String orderType, int idx) {
        switch (orderType) {
            case "partiASC" :
                return channelInfoInjectService.setReturnChannelsTTL(channelListRepository.findChannelsRecentlyTalk(idx, "asc"));
            case "partiDESC" :
                return channelInfoInjectService.setReturnChannelsTTL(channelListRepository.findChannelsRecentlyTalk(idx, "desc"));
        }
        return new ArrayList<>();
    }
}
