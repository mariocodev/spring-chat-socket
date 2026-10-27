package py.com.mcodev.springchatsocket.service;

import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import py.com.mcodev.springchatsocket.dto.UserPresence;

@Service
public class UserPresenceService {
	private final SimpMessagingTemplate messaging;
	private final Map<String, Set<String>> sessionsByUser = new HashMap<>();
	private final Map<String, String> userBySession = new HashMap<>();

	public UserPresenceService(SimpMessagingTemplate messaging) {
		this.messaging = messaging;
	}

	@EventListener
	public synchronized void connected(SessionConnectedEvent event) {
		Principal user = event.getUser();
		String sessionId = StompHeaderAccessor.wrap(event.getMessage()).getSessionId();
		if (user == null || sessionId == null || userBySession.containsKey(sessionId)) return;

		Set<String> sessions = sessionsByUser.computeIfAbsent(user.getName(), ignored -> new HashSet<>());
		boolean becameOnline = sessions.isEmpty();
		sessions.add(sessionId);
		userBySession.put(sessionId, user.getName());
		if (becameOnline) broadcast(user.getName(), true);
	}

	@EventListener
	public synchronized void disconnected(SessionDisconnectEvent event) {
		String sessionId = event.getSessionId();
		String userId = userBySession.remove(sessionId);
		if (userId == null) return;

		Set<String> sessions = sessionsByUser.get(userId);
		sessions.remove(sessionId);
		if (sessions.isEmpty()) {
			sessionsByUser.remove(userId);
			broadcast(userId, false);
		}
	}

	public synchronized boolean isOnline(String userId) {
		return sessionsByUser.containsKey(userId);
	}

	private void broadcast(String userId, boolean online) {
		messaging.convertAndSend("/topic/presence", new UserPresence(userId, online));
	}
}
