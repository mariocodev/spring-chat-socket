package py.com.mcodev.springchatsocket.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import py.com.mcodev.springchatsocket.model.ChatMessageEntity;
import py.com.mcodev.springchatsocket.model.MessageStatus;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, UUID> {

	Page<ChatMessageEntity> findByRoomIdOrderByCreatedAtDescIdDesc(UUID roomId, Pageable pageable);

	@Modifying
	@Query("delete from ChatMessageEntity m where m.room.id = :roomId")
	void deleteByRoomId(@Param("roomId") UUID roomId);

	@Modifying(clearAutomatically = true)
	@Query("update ChatMessageEntity m set m.status = :status "
			+ "where m.room.id = :roomId and m.sender.googleId <> :userId and m.status <> :readStatus")
	int markIncomingAsRead(
			@Param("roomId") UUID roomId,
			@Param("userId") String userId,
			@Param("status") MessageStatus status,
			@Param("readStatus") MessageStatus readStatus);

	@Modifying(clearAutomatically = true)
	@Query("update ChatMessageEntity m set m.status = :delivered "
			+ "where m.id = :messageId and m.sender.googleId <> :userId and m.status = :sent "
			+ "and :userId member of m.room.participantIds")
	int markDelivered(
			@Param("messageId") UUID messageId,
			@Param("userId") String userId,
			@Param("sent") MessageStatus sent,
			@Param("delivered") MessageStatus delivered);

	@Query("select m.sender.googleId as senderId, count(m.id) as messageCount "
			+ "from ChatMessageEntity m where :userId member of m.room.participantIds "
			+ "and m.sender.googleId <> :userId and m.status <> :readStatus "
			+ "group by m.sender.googleId")
	List<UnreadMessageCount> countUnreadBySender(@Param("userId") String userId, @Param("readStatus") MessageStatus readStatus);

	interface UnreadMessageCount {
		String getSenderId();

		long getMessageCount();
	}
}
