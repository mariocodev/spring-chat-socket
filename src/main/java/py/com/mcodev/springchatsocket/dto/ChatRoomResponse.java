package py.com.mcodev.springchatsocket.dto;

import java.time.Instant;
import java.util.UUID;

import py.com.mcodev.springchatsocket.model.ChatRoom;
import py.com.mcodev.springchatsocket.model.RoomType;

public record ChatRoomResponse(UUID id, RoomType type, Instant createdAt) {
	public static ChatRoomResponse from(ChatRoom room) {
		return new ChatRoomResponse(room.getId(), room.getType(), room.getCreatedAt());
	}
}
