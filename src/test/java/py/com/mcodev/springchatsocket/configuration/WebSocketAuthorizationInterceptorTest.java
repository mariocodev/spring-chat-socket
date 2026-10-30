package py.com.mcodev.springchatsocket.configuration;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.Principal;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

class WebSocketAuthorizationInterceptorTest {

	private final WebSocketAuthorizationInterceptor interceptor = new WebSocketAuthorizationInterceptor();

	@Test
	void rejectsSubscriptionsToAnotherUsersInbox() {
		StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
		headers.setUser((Principal) () -> "google-bart");
		headers.setDestination("/topic/users/google-lisa");
		Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());

		assertThrows(MessageDeliveryException.class, () -> interceptor.preSend(message, null));
	}

	@Test
	void allowsSubscriptionToOwnInbox() {
		StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
		headers.setUser((Principal) () -> "google-bart");
		headers.setDestination("/topic/users/google-bart");
		Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());

		interceptor.preSend(message, null);
	}

	@Test
	void allowsSubscriptionToPresenceUpdates() {
		StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
		headers.setUser((Principal) () -> "google-bart");
		headers.setDestination("/topic/presence");
		Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());

		interceptor.preSend(message, null);
	}

	@Test
	void allowsSubscriptionToOwnTypingChannel() {
		StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
		headers.setUser((Principal) () -> "google-bart");
		headers.setDestination("/topic/typing/google-bart");
		Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());

		interceptor.preSend(message, null);
	}

	@Test
	void rejectsSubscriptionToAnotherUsersTypingChannel() {
		StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
		headers.setUser((Principal) () -> "google-bart");
		headers.setDestination("/topic/typing/google-lisa");
		Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());

		assertThrows(MessageDeliveryException.class, () -> interceptor.preSend(message, null));
	}

	@Test
	void allowsTypingEventsFromOwnSenderPath() {
		StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SEND);
		headers.setUser((Principal) () -> "google-bart");
		headers.setDestination("/app/typing/google-bart/google-lisa");
		Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());

		interceptor.preSend(message, null);
	}

	@Test
	void rejectsTypingEventsWithSpoofedSender() {
		StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SEND);
		headers.setUser((Principal) () -> "google-bart");
		headers.setDestination("/app/typing/google-lisa/google-bart");
		Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());

		assertThrows(MessageDeliveryException.class, () -> interceptor.preSend(message, null));
	}
}
