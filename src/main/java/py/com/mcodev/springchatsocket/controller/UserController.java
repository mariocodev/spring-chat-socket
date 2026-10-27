package py.com.mcodev.springchatsocket.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import py.com.mcodev.springchatsocket.dto.ChatUserResponse;
import py.com.mcodev.springchatsocket.repository.ChatUserRepository;

@RestController
@RequestMapping("/api")
public class UserController {

	private final ChatUserRepository users;

	public UserController(ChatUserRepository users) {
		this.users = users;
	}

	@GetMapping("/auth/me")
	public ChatUserResponse currentUser(Principal principal) {
		return users.findById(principal.getName())
				.map(ChatUserResponse::from)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/users")
	public List<ChatUserResponse> users(Principal principal) {
		return users.findAllByGoogleIdNotOrderByNameAsc(principal.getName()).stream()
				.map(ChatUserResponse::from)
				.toList();
	}
}
