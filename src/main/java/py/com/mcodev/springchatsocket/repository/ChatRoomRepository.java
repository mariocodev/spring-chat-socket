package py.com.mcodev.springchatsocket.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import py.com.mcodev.springchatsocket.model.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {

	Optional<ChatRoom> findByConversationKey(String conversationKey);

	@Query("select r from ChatRoom r join r.participantIds p where r.id = :roomId and p = :userId")
	Optional<ChatRoom> findAccessibleRoom(@Param("roomId") UUID roomId, @Param("userId") String userId);
}
