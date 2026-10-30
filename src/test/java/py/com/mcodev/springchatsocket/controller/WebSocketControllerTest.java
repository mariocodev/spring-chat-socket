package py.com.mcodev.springchatsocket.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import py.com.mcodev.springchatsocket.dto.ChatMessage;
import py.com.mcodev.springchatsocket.dto.ChatMessageRequest;
import py.com.mcodev.springchatsocket.dto.TypingEvent;
import py.com.mcodev.springchatsocket.model.MessageStatus;
import py.com.mcodev.springchatsocket.service.ChatMessageService;
import py.com.mcodev.springchatsocket.service.ChatRoomService;

@ExtendWith(MockitoExtension.class)
class WebSocketControllerTest {

	@Mock
	private SimpMessagingTemplate messagingTemplate;

	@Mock
	private ChatMessageService messages;

	@Mock
	private ChatRoomService rooms;

	@InjectMocks
	private WebSocketController controller;

	@Test
	void sendsMessageToRecipientInboxWithoutRequiringAnOpenConversation() {
		Principal sender = () -> "google-bart";
		UUID roomId = UUID.randomUUID();
		ChatMessage saved = new ChatMessage(UUID.randomUUID(), "Hola", "google-bart", roomId,
				Instant.now(), MessageStatus.SENT);
		when(messages.sendPrivate("google-bart", "google-lisa", "Hola")).thenReturn(saved);

		controller.chat("google-bart", "google-lisa", new ChatMessageRequest("Hola"), sender);

		verify(messagingTemplate).convertAndSend(
				"/topic/users/google-lisa", saved);
		verify(messagingTemplate).convertAndSend("/topic/users/google-bart", saved);
	}

	@Test
	void rejectsMessagesWithSpoofedSender() {
		Principal sender = () -> "google-bart";
		assertThrows(IllegalArgumentException.class,
				() -> controller.chat("google-marge", "google-lisa", new ChatMessageRequest("Hola"), sender));
		verify(messages, never()).sendPrivate("google-marge", "google-lisa", "Hola");
	}

	@Test
	void sendsTypingEventToRecipientWhenRoomExists() {
		Principal sender = () -> "google-bart";
		when(rooms.privateRoomExists("google-bart", "google-lisa")).thenReturn(true);

		controller.typing("google-bart", "google-lisa", sender);

		verify(messagingTemplate).convertAndSend("/topic/typing/google-lisa", new TypingEvent("google-bart"));
	}

	@Test
	void ignoresTypingWhenNoPrivateRoomExists() {
		Principal sender = () -> "google-bart";

		controller.typing("google-bart", "google-lisa", sender);

		verify(messagingTemplate, never()).convertAndSend(
				"/topic/typing/google-lisa", new TypingEvent("google-bart"));
	}

	@Test
	void rejectsTypingWithSpoofedSender() {
		Principal sender = () -> "google-bart";
		assertThrows(IllegalArgumentException.class,
				() -> controller.typing("google-marge", "google-lisa", sender));
		verify(rooms, never()).privateRoomExists("google-marge", "google-lisa");
	}
}
