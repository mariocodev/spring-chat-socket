package py.com.mcodev.springchatsocket.service;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import py.com.mcodev.springchatsocket.model.ChatRoom;
import py.com.mcodev.springchatsocket.repository.ChatRoomRepository;
import py.com.mcodev.springchatsocket.repository.ChatUserRepository;

@Service
public class ChatRoomService {

	private final ChatRoomRepository rooms;
	private final ChatUserRepository users;

	public ChatRoomService(ChatRoomRepository rooms, ChatUserRepository users) {
		this.rooms = rooms;
		this.users = users;
	}

	@Transactional
	public ChatRoom findOrCreatePrivateRoom(String userId, String contactId) {
		if (contactId == null || contactId.isBlank() || userId.equals(contactId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contacto no válido");
		}
		if (!users.existsById(contactId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
		}

		String key = ChatRoom.privateConversationKey(userId, contactId);
		return rooms.findByConversationKey(key).orElseGet(() -> {
			ChatRoom room = new ChatRoom(userId, contactId);
			rooms.save(room);
			return room;
		});
	}

	@Transactional(readOnly = true)
	public ChatRoom requireParticipantRoom(UUID roomId, String userId) {
		return rooms.findAccessibleRoom(roomId, userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversación no encontrada"));
	}
}
