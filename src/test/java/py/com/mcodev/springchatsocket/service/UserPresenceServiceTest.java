package py.com.mcodev.springchatsocket.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.security.Principal;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import py.com.mcodev.springchatsocket.dto.UserPresence;

class UserPresenceServiceTest {

	@Test
	void remainsOnlineUntilLastSessionDisconnects() {
		SimpMessagingTemplate messaging = mock(SimpMessagingTemplate.class);
		UserPresenceService presence = new UserPresenceService(messaging);
		Principal user = () -> "google-bart";

		presence.connected(new SessionConnectedEvent(this, session("one"), user));
		presence.connected(new SessionConnectedEvent(this, session("two"), user));
		assertTrue(presence.isOnline(user.getName()));

		presence.disconnected(new SessionDisconnectEvent(this, session("one"), "one", CloseStatus.NORMAL, user));
		assertTrue(presence.isOnline(user.getName()));
		presence.disconnected(new SessionDisconnectEvent(this, session("two"), "two", CloseStatus.NORMAL, user));
		assertFalse(presence.isOnline(user.getName()));
		presence.disconnected(new SessionDisconnectEvent(this, session("two"), "two", CloseStatus.NORMAL, user));

		verify(messaging).convertAndSend("/topic/presence", new UserPresence(user.getName(), true));
		verify(messaging, times(1)).convertAndSend("/topic/presence", new UserPresence(user.getName(), false));
	}

	private Message<byte[]> session(String id) {
		return MessageBuilder.withPayload(new byte[0])
				.setHeader(SimpMessageHeaderAccessor.SESSION_ID_HEADER, id)
				.build();
	}
}
