package py.com.mcodev.springchatsocket.controller;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import py.com.mcodev.springchatsocket.dto.ChatMessage;

@Controller
public class WebSocketController {

	private static final Set<String> USERS = Set.of("Bart", "Lisa", "Homero", "Marge");
	private final SimpMessagingTemplate messagingTemplate;

	public WebSocketController(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	@MessageMapping("/private/{sender}/{recipient}")
	public void chat(
			@DestinationVariable String sender,
			@DestinationVariable String recipient,
			ChatMessage message) {
		if (!USERS.contains(sender) || !USERS.contains(recipient) || sender.equals(recipient)) {
			throw new IllegalArgumentException("Usuario de chat no válido");
		}
		if (message == null || !sender.equals(message.getUser())
				|| message.getMessage() == null || message.getMessage().isBlank()) {
			throw new IllegalArgumentException("Mensaje no válido");
		}

		String conversationId = Stream.of(sender, recipient)
				.map(user -> user.toLowerCase(Locale.ROOT))
				.sorted()
				.collect(Collectors.joining("-"));
		messagingTemplate.convertAndSend(
				"/topic/users/" + recipient.toLowerCase(Locale.ROOT),
				new ChatMessage(message.getMessage(), sender, conversationId));
	}
}
