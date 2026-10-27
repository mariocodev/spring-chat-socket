package py.com.mcodev.springchatsocket.model;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_room")
public class ChatRoom {

	@Id
	private UUID id;

	@Enumerated(EnumType.STRING)
	@Column(name = "room_type", nullable = false, length = 16)
	private RoomType type;

	@Column(name = "conversation_key", unique = true, length = 600)
	private String conversationKey;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "chat_room_participant", joinColumns = @JoinColumn(name = "room_id"))
	@Column(name = "user_id", nullable = false)
	private Set<String> participantIds;

	protected ChatRoom() {
	}

	public ChatRoom(String firstUserId, String secondUserId) {
		this.id = UUID.randomUUID();
		this.type = RoomType.PRIVATE;
		this.conversationKey = privateConversationKey(firstUserId, secondUserId);
		this.createdAt = Instant.now();
		this.participantIds = Set.of(firstUserId, secondUserId);
	}

	public UUID getId() {
		return id;
	}

	public RoomType getType() {
		return type;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Set<String> getParticipantIds() {
		return participantIds;
	}

	public static String privateConversationKey(String firstUserId, String secondUserId) {
		return Set.of(firstUserId, secondUserId).stream().sorted().collect(Collectors.joining(":"));
	}
}
