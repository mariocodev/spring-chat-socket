package py.com.mcodev.springchatsocket.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_user")
public class ChatUser {

	@Id
	@Column(name = "google_id", nullable = false)
	private String googleId;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, unique = true, length = 320)
	private String email;

	private String picture;

	@Column(name = "last_login", nullable = false)
	private Instant lastLogin;

	protected ChatUser() {
	}

	public ChatUser(String googleId, String name, String email, String picture, Instant lastLogin) {
		this.googleId = googleId;
		this.name = name;
		this.email = email;
		this.picture = picture;
		this.lastLogin = lastLogin;
	}

	public String getGoogleId() {
		return googleId;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPicture() {
		return picture;
	}

	public Instant getLastLogin() {
		return lastLogin;
	}

	public void updateProfile(String name, String email, String picture, Instant lastLogin) {
		this.name = name;
		this.email = email;
		this.picture = picture;
		this.lastLogin = lastLogin;
	}
}
