package py.com.mcodev.springchatsocket.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import py.com.mcodev.springchatsocket.dto.ChatMessage;
import py.com.mcodev.springchatsocket.model.ChatUser;
import py.com.mcodev.springchatsocket.model.MessageStatus;
import py.com.mcodev.springchatsocket.repository.ChatMessageRepository;
import py.com.mcodev.springchatsocket.repository.ChatUserRepository;

@SpringBootTest
@ActiveProfiles("test")
class ChatMessagePersistenceTest {

	@Autowired
	private ChatMessageService messages;

	@Autowired
	private ChatRoomService rooms;

	@Autowired
	private ChatMessageRepository messageRepository;

	@Autowired
	private ChatUserRepository userRepository;

	@Test
	void persistsPrivateMessagesAndUpdatesDeliveryAndReadStatus() {
		String senderId = "sender-" + UUID.randomUUID();
		String recipientId = "recipient-" + UUID.randomUUID();
		userRepository.save(new ChatUser(senderId, "Sender", senderId + "@example.test", null, Instant.now()));
		userRepository.save(new ChatUser(recipientId, "Recipient", recipientId + "@example.test", null, Instant.now()));

		var room = rooms.findOrCreatePrivateRoom(senderId, recipientId);
		var sameRoom = rooms.findOrCreatePrivateRoom(recipientId, senderId);
		assertEquals(room.getId(), sameRoom.getId());

		ChatMessage first = messages.sendPrivate(senderId, recipientId, "first");
		messages.sendPrivate(senderId, recipientId, "second");
		assertEquals(MessageStatus.SENT, messageRepository.findById(first.id()).orElseThrow().getStatus());

		var unread = messages.unreadCounts(recipientId);
		assertEquals(2, unread.get(0).getMessageCount());
		messages.markDelivered(first.id(), recipientId);
		assertEquals(MessageStatus.DELIVERED, messageRepository.findById(first.id()).orElseThrow().getStatus());

		Page<ChatMessage> latestPage = messages.history(room.getId(), recipientId, 0, 1);
		assertTrue(latestPage.hasNext());
		assertEquals("second", latestPage.getContent().get(0).message());
		assertEquals(MessageStatus.READ, latestPage.getContent().get(0).status());
		assertFalse(messages.unreadCounts(recipientId).stream()
				.anyMatch(count -> count.getSenderId().equals(senderId)));
	}
}
