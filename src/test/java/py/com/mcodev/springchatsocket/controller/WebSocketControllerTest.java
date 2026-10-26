package py.com.mcodev.springchatsocket.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import py.com.mcodev.springchatsocket.dto.ChatMessage;

@ExtendWith(MockitoExtension.class)
class WebSocketControllerTest {

	@Mock
	private SimpMessagingTemplate messagingTemplate;

	@InjectMocks
	private WebSocketController controller;

	@Test
	void sendsMessageToRecipientInboxWithoutRequiringAnOpenConversation() {
		ChatMessage message = new ChatMessage("Hola", "Bart", null);

		controller.chat("Bart", "Lisa", message);

		verify(messagingTemplate).convertAndSend(
				"/topic/users/lisa",
				new ChatMessage("Hola", "Bart", "bart-lisa"));
	}

	@Test
	void rejectsMessagesWithSpoofedSender() {
		assertThrows(IllegalArgumentException.class,
				() -> controller.chat("Bart", "Lisa", new ChatMessage("Hola", "Marge", null)));
	}
}
