package webrtc.v1.utils.pubsub;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import webrtc.v1.chat.dto.ChattingMessage;
import webrtc.v1.staticgenarator.ChattingMessageGenerator;

@ExtendWith(MockitoExtension.class)
public class RedisSubscriberImplTest {

  final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
  final PrintStream standardOut = System.out;

  @InjectMocks
  private RedisSubscriberImpl redisSubscriber;

  @Mock
  private ObjectMapper objectMapper;
  @Mock
  private SimpMessageSendingOperations messagingTemplate;

  private static final String message = "Message#1";
  private static final String objectMapperErrorMessage = "OBJ Error Message!!";
  private static final String messagingTemplateErrorMessage = "messagingTemplate Error Message!!";

  @BeforeEach
  void setUp() {
    System.setOut(new PrintStream(outputStreamCaptor));
  }

  @AfterEach
  void tearDown() {
    System.setOut(standardOut);
  }


  @Test
  void 메시지전송성공() throws JsonProcessingException {
    // given
    ChattingMessage chattingMessage = ChattingMessageGenerator.createChattingMessage();
    doReturn(chattingMessage)
        .when(objectMapper).readValue(message, ChattingMessage.class);
    doNothing()
        .when(messagingTemplate).convertAndSend(any(String.class), any(ChattingMessage.class));

    // when
    redisSubscriber.sendMessage(message);
    // then

  }

  @Test
  void 메시지전송실패ByObjectMapper() throws JsonProcessingException {
    // given
    doThrow(new RuntimeException(objectMapperErrorMessage))
        .when(objectMapper).readValue(message, ChattingMessage.class);

    // when
    redisSubscriber.sendMessage(message);

    // then
    assertEquals("error in sendMessage = " + objectMapperErrorMessage, outputStreamCaptor.toString().trim());
  }

  @Test
  void 메시지전송실패ByConvertSend() throws JsonProcessingException {
    // given
    ChattingMessage chattingMessage = ChattingMessageGenerator.createChattingMessage();
    doReturn(chattingMessage)
        .when(objectMapper).readValue(message, ChattingMessage.class);
    doThrow(new RuntimeException(messagingTemplateErrorMessage))
        .when(messagingTemplate).convertAndSend(any(String.class), any(ChattingMessage.class));

    // when
    redisSubscriber.sendMessage(message);

    // then
    assertEquals("error in sendMessage = " + messagingTemplateErrorMessage, outputStreamCaptor.toString().trim());
  }
}
