package webrtc.chatservice.domain;

//@SpringBootTest
//@Transactional
//public class ChannelTest {
//
//    @Autowired
//    private UsersRepository userRepository;
//
//    @Autowired
//    private UsersService userService;
//
//    @BeforeEach
//    public void clearUserCache() {
//        userService.redisDataEvict();
//    }
//
//
//    @BeforeEach
//    public void createUser() {
//        Users user1 = new Users("user", "user", "email");
//        userRepository.saveUser(user1);
//
//    }
//
//    @Test
//    public void constructorChannel() {
//        //given
//        Channel channel = new Channel("TestChannel", "chat");
//
//        //when
//
//        //then
//        Assertions.assertThat(channel.getChannelName()).isEqualTo("TestChannel");
//    }
//
//    @Test
//    public void addChannelUser() {
//        //given
//        Channel channel = new Channel("TestChannel", "chat");
//        Users user = userRepository.findUserByEmail("email");
//        ChannelUser channelUser = new ChannelUser(user, channel);
//
//        //when
//        channel.enterChannelUser(channelUser);
//
//        //then
//        Assertions.assertThat(channel.getCurrentParticipants()).isEqualTo(1);
//
//        Assertions.assertThat(channelUser.getChannel()).isEqualTo(channel);
//    }
//
//    @Test
//    public void addChannelHashTag() {
//
//        // given
//        Channel channel = new Channel("TestChannel", "chat");
//        HashTag hashTag = new HashTag("TestTag");
//        ChannelHashTag channelHashTag = new ChannelHashTag();
//        channelHashTag.CreateChannelHashTag(channel, hashTag);
//
//        // when
//        channel.addChannelHashTag(channelHashTag);
//
//        //then
//        Assertions.assertThat(channel.getChannelHashTags().size()).isEqualTo(1);
//        Assertions.assertThat(channelHashTag.getChannel()).isEqualTo(channel);
//    }
//
//    @Test
//    public void minusCurrentParticipants() {
//        //given
//        Channel channel = new Channel("TestChannel", "chat");
//
//        //when
////        channel.exitChannelUser();
//
//        //then
//        Assertions.assertThat(channel.getCurrentParticipants()).isEqualTo(0);
//    }
//}
