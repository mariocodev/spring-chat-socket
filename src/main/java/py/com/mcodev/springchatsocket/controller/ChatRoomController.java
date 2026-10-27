package py.com.mcodev.springchatsocket.controller;

import java.security.Principal;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import py.com.mcodev.springchatsocket.dto.ChatMessage;
import py.com.mcodev.springchatsocket.dto.ChatRoomResponse;
import py.com.mcodev.springchatsocket.dto.PrivateRoomRequest;
import py.com.mcodev.springchatsocket.service.ChatMessageService;
import py.com.mcodev.springchatsocket.service.ChatRoomService;

@RestController
@RequestMapping("/api")
public class ChatRoomController {

	private final ChatRoomService rooms;
	private final ChatMessageService messages;

	public ChatRoomController(ChatRoomService rooms, ChatMessageService messages) {
		this.rooms = rooms;
		this.messages = messages;
	}

	@PostMapping("/rooms/private")
	public ChatRoomResponse privateRoom(@RequestBody PrivateRoomRequest request, Principal principal) {
		return ChatRoomResponse.from(rooms.findOrCreatePrivateRoom(principal.getName(), request.contactId()));
	}

	@GetMapping("/rooms/{roomId}/messages")
	public Page<ChatMessage> history(
			@PathVariable UUID roomId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "50") int size,
			Principal principal) {
		if (page < 0 || size < 1) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paginación no válida");
		}
		return messages.history(roomId, principal.getName(), page, size);
	}

	@DeleteMapping("/rooms/{roomId}/messages")
	public void clear(@PathVariable UUID roomId, Principal principal) {
		messages.clear(roomId, principal.getName());
	}

	@PostMapping("/rooms/{roomId}/read")
	public void read(@PathVariable UUID roomId, Principal principal) {
		messages.markRead(roomId, principal.getName());
	}

	@PostMapping("/messages/{messageId}/delivered")
	public void delivered(@PathVariable UUID messageId, Principal principal) {
		messages.markDelivered(messageId, principal.getName());
	}
}
