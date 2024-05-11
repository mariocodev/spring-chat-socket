package py.com.mcodev.springchatsocket.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import py.com.mcodev.springchatsocket.dto.ChatMessage;

@Controller
public class WebSocketController {

	@MessageMapping("/chat/{roomId}")
	@SendTo("/topic/{roomId}")
	public ChatMessage chat(@DestinationVariable String roomId, ChatMessage message){
		return new ChatMessage(message.getMessage(), message.getUser());
	}
}
