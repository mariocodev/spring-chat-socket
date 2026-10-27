package py.com.mcodev.springchatsocket.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.security.Principal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import py.com.mcodev.springchatsocket.dto.ChatMessage;
import py.com.mcodev.springchatsocket.repository.ChatUserRepository;

@ExtendWith(MockitoExtension.class)
class WebSocketControllerTest {

	@Mock
	private SimpMessagingTemplate messagingTemplate;

	@Mock
	private ChatUserRepository users;

	@InjectMocks
	private WebSocketController controller;

	@Test
	void sendsMessageToRecipientInboxWithoutRequiringAnOpenConversation() {
		Principal sender = () -> "google-bart";
		ChatMessage message = new ChatMessage("Hola", null, null);
		when(users.existsById("google-lisa")).thenReturn(true);

		controller.chat("google-bart", "google-lisa", message, sender);

		verify(messagingTemplate).convertAndSend(
				"/topic/users/google-lisa",
				new ChatMessage("Hola", "google-bart", "google-bart-google-lisa"));
	}

	@Test
	void rejectsMessagesWithSpoofedSender() {
		Principal sender = () -> "google-bart";
		assertThrows(IllegalArgumentException.class,
				() -> controller.chat("google-marge", "google-lisa", new ChatMessage("Hola", null, null), sender));
	}
}
