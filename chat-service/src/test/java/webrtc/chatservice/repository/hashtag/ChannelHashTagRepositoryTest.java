package webrtc.chatservice.repository.hashtag;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
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
public class ChannelHashTagRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private ChannelHashTagRepository repository;

    String nickname1 = "nickname1";
    String password = "password";
    String email1 = "email1";
    String desc = "DESC";
    String channelName1 = "channelName1";
    String tag1 = "tag1";
    ChannelType text = TEXT;

    @Test
    void 채널해시태그저장() {
        // given
        Channel channel = createChannel(channelName1, text);
        HashTag hashTag = createHashTag(tag1);
        ChannelHashTag channelHashTag = createChannelHashTag(channel, hashTag);
        em.persist(channel);
        em.persist(hashTag);

        // when
        ChannelHashTag channelHashTag2 = repository.save(channelHashTag);

        // then
        assertThat(channelHashTag2.getId()).isEqualTo(channelHashTag.getId());
    }

    @Test
    void findById성공() {
        // given
        Channel channel = createChannel(channelName1, text);
        HashTag hashTag = createHashTag(tag1);
        ChannelHashTag channelHashTag = createChannelHashTag(channel, hashTag);
        em.persist(channel);
        em.persist(hashTag);
        ChannelHashTag channelHashTag2 = repository.save(channelHashTag);

        // when
        Optional<ChannelHashTag> OpCH = repository.findById(channelHashTag.getId());

        // then
        assertThat(OpCH.isPresent()).isTrue();
    }

    @Test
    void findById실패() {
        // given
        Channel channel = createChannel(channelName1, text);
        HashTag hashTag = createHashTag(tag1);
        ChannelHashTag channelHashTag = createChannelHashTag(channel, hashTag);
        em.persist(channel);
        em.persist(hashTag);

        // when
        Optional<ChannelHashTag> OpCH = repository.findById(channelHashTag.getId());

        // then
        assertThat(OpCH.isPresent()).isFalse();
    }

    @Test
    void 채널해시태그삭제성공() {
        // given
        Channel channel = createChannel(channelName1, text);
        HashTag hashTag = createHashTag(tag1);
        ChannelHashTag channelHashTag = createChannelHashTag(channel, hashTag);
        em.persist(channel);
        em.persist(hashTag);
        repository.save(channelHashTag);

        // when

        Optional<ChannelHashTag> OpCH1 = repository.findById(channelHashTag.getId());
        repository.delete(OpCH1.get());
        Optional<ChannelHashTag> OpCH2 = repository.findById(channelHashTag.getId());

        // then
        assertThrows(NoSuchElementException.class, OpCH2::get);
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