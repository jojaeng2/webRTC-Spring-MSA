package webrtc.chatservice.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChannelUser;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.exception.ChannelException.AlreadyExistUserInChannelException;
import webrtc.chatservice.exception.ChannelException.ChannelParticipantsFullException;
import webrtc.chatservice.exception.ChannelException.NotExistChannelException;
import webrtc.chatservice.exception.ChannelUserException.NotExistChannelUserException;
import webrtc.chatservice.exception.UserException.NotExistUserException;
import webrtc.chatservice.repository.channel.ChannelCrudRepository;
import webrtc.chatservice.repository.users.ChannelUserRepository;
import webrtc.chatservice.repository.users.UsersRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChannelIOServiceImpl implements ChannelIOService {

    private final ChannelCrudRepository channelCrudRepository;
    private final UsersRepository usersRepository;
    private final ChannelUserRepository channelUserRepository;

    /*
     * 비즈니스 로직 - 채널에 회원 입장
     * 비즈니스 로직 순서 :
     * 1) 입장할 회원 검색 -> 존재하지 않는 회원이라면 Exception 발생
     * 2) 입장할 채널 검색 -> 존재하지 않는 채널이라면 Exception 발생
     * 3) 이미 입장한 적이 있는 회원인지 검색 -> 이전에 입장한 적이 있다면 Exception 발생 -> Client에서 재입장 요청을 보내야 함
     *    첫 입장과 재입장을 구별한 이유 = '..님이 입장했다는 메시지를 보내지 않기 위해' and '나가있는 동안 보지 못한 채팅 로그를 보기 위해 마지막으로 전송된 채팅 로그가 무엇인지 알려줘야 함'
     * 4) 첫 입장일 경우 채널의 제한인원이 가득 찼는지 확인 -> 만약 15명이 입장한 상태라면 Exception 발생
     * 5) 채널과 유저 관계 생성
     */
    @Override
    @Transactional
    public void enterChannel(String channelId, String email) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(NotExistUserException::new);
        Channel channel = channelCrudRepository.findById(channelId)
                .orElseThrow(NotExistChannelException::new);
        createChannelUser(user, channel);
    }

    /*
     * 비즈니스 로직 - 채널에서 회원 퇴장
     * 비즈니스 로직 순서 :
     * 1) 퇴장할 회원 검색 -> 존재하지 않는 회원이라면 Exception 발생
     * 2) 퇴장할 채널 검색 -> 존재하지 않는 채널이라면 Exception 발생
     * 3) 해당 회원이 채널에 입장했는지 검색 -> 만약 입장한 적이 없다면 Exception 발생
     * 4) 채널의 현재 인원 -1 감소
     * 5) 채널과 회원 관계 삭제
     */
    @Override
    @Transactional
    public void exitChannel(String channelId, UUID userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(NotExistUserException::new);
        Channel channel = channelCrudRepository.findById(channelId)
                .orElseThrow(NotExistChannelException::new);
        deleteChannelUser(channel, user);
    }

    /*
     * 비즈니스 로직 - 채널과 회원 관계 생성
     * 비즈니스 로직 순서 :
     * 1) 채널 - 회원 관계 검색 -> 있다면 채널에 입장한 적이 있음을 의미 -> Exception 발생
     * 2) 처음 입장하는 경우라면 남은 자리 확인 -> 자리가 없다면 Exception 발생
     * 3) 채널의 참가 인원 수 +1 증가
     * 4) 채널과 회원 관계 생성 후 저장
     */
    private void createChannelUser(Users user, Channel channel) {
        isAlreadyExistChannelUser(user, channel);
        if (channel.isFull()) throw new ChannelParticipantsFullException();
        ChannelUser channelUser = ChannelUser.builder()
                .user(user)
                .channel(channel)
                .build();
        channel.enterChannelUser(channelUser);
        channelUserRepository.save(channelUser);
    }

    void isAlreadyExistChannelUser(Users user, Channel channel) {
        channelUserRepository.findByChannelAndUser(channel, user).ifPresent(
                cu -> {
                    throw new AlreadyExistUserInChannelException();
                }
        );
    }

    /*
     * 비즈니스 로직 - 채널과 회원 관계 삭제
     * 비즈니스 로직 순서 :
     * 1) 채널 - 회원 관계 검색 -> 없다면 입장한 적이 없음을 의미 Exception 발생
     * 2) 채널의 참가 인원 수 -1 감소
     * 3) 채널과 회원 관계 삭제 후 저장
     */
    private void deleteChannelUser(Channel channel, Users user) {
        ChannelUser channelUser = channelUserRepository.findByChannelAndUser(channel, user)
                .orElseThrow(NotExistChannelUserException::new);
        channel.exitChannelUser(channelUser);
        channelUserRepository.delete(channelUser);
    }
}
