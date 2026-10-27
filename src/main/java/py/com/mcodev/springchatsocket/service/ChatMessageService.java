package py.com.mcodev.springchatsocket.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import py.com.mcodev.springchatsocket.dto.ChatMessage;
import py.com.mcodev.springchatsocket.model.ChatMessageEntity;
import py.com.mcodev.springchatsocket.model.ChatRoom;
import py.com.mcodev.springchatsocket.model.ChatUser;
import py.com.mcodev.springchatsocket.model.MessageStatus;
import py.com.mcodev.springchatsocket.repository.ChatMessageRepository;
import py.com.mcodev.springchatsocket.repository.ChatUserRepository;

@Service
public class ChatMessageService {

	private final ChatMessageRepository messages;
	private final ChatRoomService rooms;
	private final ChatUserRepository users;

	public ChatMessageService(ChatMessageRepository messages, ChatRoomService rooms, ChatUserRepository users) {
		this.messages = messages;
		this.rooms = rooms;
		this.users = users;
	}

	@Transactional
	public ChatMessage send(UUID roomId, String senderId, String content) {
		if (content == null || content.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El mensaje no puede estar vacío");
		}

		ChatRoom room = rooms.requireParticipantRoom(roomId, senderId);
		ChatUser sender = users.findById(senderId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		return ChatMessage.from(messages.save(new ChatMessageEntity(room, sender, content.trim())));
	}

	@Transactional
	public ChatMessage sendPrivate(String senderId, String recipientId, String content) {
		ChatRoom room = rooms.findOrCreatePrivateRoom(senderId, recipientId);
		return send(room.getId(), senderId, content);
	}

	@Transactional
	public Page<ChatMessage> history(UUID roomId, String userId, int page, int size) {
		rooms.requireParticipantRoom(roomId, userId);
		messages.markIncomingAsRead(roomId, userId, MessageStatus.READ, MessageStatus.READ);
		PageRequest request = PageRequest.of(page, Math.min(size, 100),
				Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by(Sort.Direction.DESC, "id")));
		return messages.findByRoomIdOrderByCreatedAtDescIdDesc(roomId, request)
				.map(ChatMessage::from);
	}

	@Transactional
	public void markDelivered(UUID messageId, String userId) {
		messages.markDelivered(messageId, userId, MessageStatus.SENT, MessageStatus.DELIVERED);
	}

	@Transactional
	public void markRead(UUID roomId, String userId) {
		rooms.requireParticipantRoom(roomId, userId);
		messages.markIncomingAsRead(roomId, userId, MessageStatus.READ, MessageStatus.READ);
	}

	@Transactional
	public void clear(UUID roomId, String userId) {
		rooms.requireParticipantRoom(roomId, userId);
		messages.deleteByRoomId(roomId);
	}

	@Transactional(readOnly = true)
	public List<ChatMessageRepository.UnreadMessageCount> unreadCounts(String userId) {
		return messages.countUnreadBySender(userId, MessageStatus.READ);
	}
}
