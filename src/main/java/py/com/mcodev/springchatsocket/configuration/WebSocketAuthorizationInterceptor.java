package py.com.mcodev.springchatsocket.configuration;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class WebSocketAuthorizationInterceptor implements ChannelInterceptor {

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor headers = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (headers == null || headers.getCommand() == null) return message;

		String userId = headers.getUser() == null ? null : headers.getUser().getName();
		String destination = headers.getDestination();
		if (userId == null) throw new MessageDeliveryException(message, "Autenticación requerida");

		if (headers.getCommand() == StompCommand.SUBSCRIBE
				&& (destination == null || !(destination.equals("/topic/users/" + userId)
						|| destination.equals("/topic/typing/" + userId)
						|| destination.equals("/topic/presence")))) {
			throw new MessageDeliveryException(message, "No autorizado para suscribirse a este canal");
		}
		if (headers.getCommand() == StompCommand.SEND
				&& (destination == null || !(destination.startsWith("/app/private/" + userId + "/")
						|| destination.startsWith("/app/typing/" + userId + "/")))) {
			throw new MessageDeliveryException(message, "No autorizado para enviar a este canal");
		}
		return message;
	}
}
