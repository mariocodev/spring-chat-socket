package py.com.mcodev.springchatsocket.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_message")
public class ChatMessageEntity {

	@Id
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "room_id", nullable = false)
	private ChatRoom room;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "sender_id", nullable = false)
	private ChatUser sender;

	@Column(nullable = false, columnDefinition = "text")
	private String content;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 16)
	private MessageStatus status;

	protected ChatMessageEntity() {
	}

	public ChatMessageEntity(ChatRoom room, ChatUser sender, String content) {
		this.id = UUID.randomUUID();
		this.room = room;
		this.sender = sender;
		this.content = content;
		this.createdAt = Instant.now();
		this.status = MessageStatus.SENT;
	}

	public UUID getId() {
		return id;
	}

	public ChatRoom getRoom() {
		return room;
	}

	public ChatUser getSender() {
		return sender;
	}

	public String getContent() {
		return content;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public MessageStatus getStatus() {
		return status;
	}
}
