package webrtc.v1.repository.channel;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.repository.ChannelCrudRepository;
import webrtc.v1.channel.entity.ChannelHashTag;
import webrtc.v1.hashtag.entity.HashTag;
import webrtc.v1.enums.ChannelType;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static webrtc.v1.enums.ChannelType.TEXT;

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
        return Channel.builder()
                .channelName(name)
                .channelType(type)
                .build();
    }

    private HashTag createHashTag(String name) {
        return HashTag
                .builder()
                .tagName(name)
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
}
