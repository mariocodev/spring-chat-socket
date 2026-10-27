package py.com.mcodev.springchatsocket.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import py.com.mcodev.springchatsocket.dto.ChatUserResponse;
import py.com.mcodev.springchatsocket.repository.ChatMessageRepository;
import py.com.mcodev.springchatsocket.repository.ChatUserRepository;
import py.com.mcodev.springchatsocket.service.ChatMessageService;
import py.com.mcodev.springchatsocket.service.UserPresenceService;

@RestController
@RequestMapping("/api")
public class UserController {

	private final ChatUserRepository users;
	private final ChatMessageService messages;
	private final UserPresenceService presence;

	public UserController(ChatUserRepository users, ChatMessageService messages, UserPresenceService presence) {
		this.users = users;
		this.messages = messages;
		this.presence = presence;
	}

	@GetMapping("/auth/me")
	public ChatUserResponse currentUser(Principal principal) {
		return users.findById(principal.getName())
				.map(user -> new ChatUserResponse(user.getGoogleId(), user.getName(), user.getEmail(),
						user.getPicture(), 0, presence.isOnline(user.getGoogleId())))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/auth/csrf")
	public String csrf(CsrfToken token) {
		return token.getToken();
	}

	@GetMapping("/users")
	public List<ChatUserResponse> users(Principal principal) {
		Map<String, Long> unreadBySender = messages.unreadCounts(principal.getName()).stream()
				.collect(Collectors.toMap(
						ChatMessageRepository.UnreadMessageCount::getSenderId,
						ChatMessageRepository.UnreadMessageCount::getMessageCount));
		return users.findAllByGoogleIdNotOrderByNameAsc(principal.getName()).stream()
				.map(user -> new ChatUserResponse(user.getGoogleId(), user.getName(), user.getEmail(),
						user.getPicture(), unreadBySender.getOrDefault(user.getGoogleId(), 0L),
						presence.isOnline(user.getGoogleId())))
				.toList();
	}
}
