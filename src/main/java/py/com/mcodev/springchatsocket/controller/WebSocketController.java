package py.com.mcodev.springchatsocket.controller;

import java.security.Principal;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import py.com.mcodev.springchatsocket.dto.ChatMessage;
import py.com.mcodev.springchatsocket.repository.ChatUserRepository;

@Controller
public class WebSocketController {

	private final SimpMessagingTemplate messagingTemplate;
	private final ChatUserRepository users;

	public WebSocketController(SimpMessagingTemplate messagingTemplate, ChatUserRepository users) {
		this.messagingTemplate = messagingTemplate;
		this.users = users;
	}

	@MessageMapping("/private/{sender}/{recipient}")
	public void chat(
			@DestinationVariable String sender,
			@DestinationVariable String recipient,
			ChatMessage message,
			Principal principal) {
		if (principal == null || !principal.getName().equals(sender)
				|| sender.equals(recipient) || !users.existsById(recipient)) {
			throw new IllegalArgumentException("Usuario de chat no válido");
		}
		if (message == null
				|| message.getMessage() == null || message.getMessage().isBlank()) {
			throw new IllegalArgumentException("Mensaje no válido");
		}

		String conversationId = Stream.of(sender, recipient)
				.sorted()
				.collect(Collectors.joining("-"));
		messagingTemplate.convertAndSend(
				"/topic/users/" + recipient,
				new ChatMessage(message.getMessage(), sender, conversationId));
	}
}
