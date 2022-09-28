package webrtc.chatservice.repository.channel;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChannelHashTag;
import webrtc.chatservice.domain.HashTag;
import webrtc.chatservice.enums.ChannelType;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static webrtc.chatservice.enums.ChannelType.TEXT;

@DataJpaTest
public class ChannelCrudRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ChannelCrudRepository repository;


    String channelName1 = "channelName1";
    String tag1 = "tag1";
    ChannelType text = TEXT;

    @Test
    void 채널생성성공() {
        // given
        Channel channel = createChannel(channelName1, text);
        HashTag hashTag = createHashTag(tag1);

        ChannelHashTag channelHashTag = createChannelHashTag(channel, hashTag);
        em.persist(hashTag);
        em.persist(channelHashTag);

        // when
        Channel createChannel = repository.save(channel);

        // then
        assertThat(createChannel.getId()).isEqualTo(channel.getId());
        assertThat(createChannel.getChannelHashTags().size()).isEqualTo(1);
    }

    @Test
    void findById성공() {
        // given
        Channel channel = createChannel(channelName1, text);
        repository.save(channel);

        // when
        Optional<Channel> OpChannel = repository.findById(channel.getId());
        Channel findChannel = OpChannel.get();

        // then
        assertThat(channel.getId()).isEqualTo(findChannel.getId());
    }

    @Test
    void findById실패() {
        // given
        Channel channel = createChannel(channelName1, text);

        // when
        Optional<Channel> OpChannel = repository.findById(channel.getId());

        // then
        assertThrows(NoSuchElementException.class, OpChannel::get);
    }

    @Test
    void 채널삭제성공() {
        // given
        Channel channel = createChannel(channelName1, text);
        repository.save(channel);

        // when
        Optional<Channel> OpChannel = repository.findById(channel.getId());
        repository.delete(OpChannel.get());
        Optional<Channel> OpChannel2 = repository.findById(channel.getId());

        // then
        assertThrows(NoSuchElementException.class, OpChannel2::get);
    }

    @Test
    void 채널삭제실패() {
        // given
        Channel channel = createChannel(channelName1, text);

        // when
        Optional<Channel> OpChannel = repository.findById(channel.getId());

        // then
        repository.delete(channel);

    }

    @Test
    void 채널이름으로찾기성공() {
        // given
        Channel channel = createChannel(channelName1, text);
        repository.save(channel);

        // when
        Optional<Channel> OpChannel = repository.findByChannelName(channel.getChannelName());
        Channel findChannel = OpChannel.get();

        // then
        assertThat(channel.getId()).isEqualTo(findChannel.getId());
    }

    @Test
    void 채널이름으로찾기실패() {
        // given
        Channel channel = createChannel(channelName1, text);

        // when
        Optional<Channel> OpChannel = repository.findByChannelName(channel.getChannelName());

        // then
        assertThrows(NoSuchElementException.class, OpChannel::get);
    }

    private Channel createChannel(String name, ChannelType type) {
        return new Channel(name, type);
    }

    private HashTag createHashTag(String name) {
        return new HashTag(name);
    }

    private ChannelHashTag createChannelHashTag(Channel channel, HashTag tag) {
        return new ChannelHashTag(channel, tag);
    }
}
