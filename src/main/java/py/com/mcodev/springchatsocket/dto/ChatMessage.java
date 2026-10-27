package py.com.mcodev.springchatsocket.dto;

import java.time.Instant;
import java.util.UUID;

import py.com.mcodev.springchatsocket.model.ChatMessageEntity;
import py.com.mcodev.springchatsocket.model.MessageStatus;

public record ChatMessage(
		UUID id,
		String message,
		String user,
		UUID roomId,
		Instant timestamp,
		MessageStatus status) {

	public static ChatMessage from(ChatMessageEntity message) {
		return new ChatMessage(
				message.getId(),
				message.getContent(),
				message.getSender().getGoogleId(),
				message.getRoom().getId(),
				message.getCreatedAt(),
				message.getStatus());
	}
}
