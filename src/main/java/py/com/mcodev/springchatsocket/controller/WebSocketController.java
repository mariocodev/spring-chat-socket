package py.com.mcodev.springchatsocket.controller;

import java.security.Principal;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import py.com.mcodev.springchatsocket.dto.ChatMessageRequest;
import py.com.mcodev.springchatsocket.dto.ChatMessage;
import py.com.mcodev.springchatsocket.service.ChatMessageService;

@Controller
public class WebSocketController {

	private final SimpMessagingTemplate messagingTemplate;
	private final ChatMessageService messages;

	public WebSocketController(
			SimpMessagingTemplate messagingTemplate,
			ChatMessageService messages) {
		this.messagingTemplate = messagingTemplate;
		this.messages = messages;
	}

	@MessageMapping("/private/{sender}/{recipient}")
	public void chat(
			@DestinationVariable String sender,
			@DestinationVariable String recipient,
			ChatMessageRequest message,
			Principal principal) {
		if (principal == null || !principal.getName().equals(sender)) {
			throw new IllegalArgumentException("Usuario de chat no válido");
		}
		ChatMessage savedMessage = messages.sendPrivate(sender, recipient, message == null ? null : message.message());
		messagingTemplate.convertAndSend("/topic/users/" + recipient, savedMessage);
		messagingTemplate.convertAndSend("/topic/users/" + sender, savedMessage);
	}
}
