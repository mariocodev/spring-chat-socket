package py.com.mcodev.springchatsocket.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import py.com.mcodev.springchatsocket.model.ChatUser;

public interface ChatUserRepository extends JpaRepository<ChatUser, String> {
	List<ChatUser> findAllByGoogleIdNotOrderByNameAsc(String googleId);

	Optional<ChatUser> findByEmail(String email);
}
