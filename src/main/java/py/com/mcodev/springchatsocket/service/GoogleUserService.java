package py.com.mcodev.springchatsocket.service;

import java.time.Instant;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.com.mcodev.springchatsocket.model.ChatUser;
import py.com.mcodev.springchatsocket.repository.ChatUserRepository;

@Service
public class GoogleUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

	private final OidcUserService delegate = new OidcUserService();
	private final ChatUserRepository users;

	public GoogleUserService(ChatUserRepository users) {
		this.users = users;
	}

	@Override
	@Transactional
	public OidcUser loadUser(OidcUserRequest request) throws OAuth2AuthenticationException {
		OidcUser principal = delegate.loadUser(request);
		String id = principal.getSubject();
		String name = principal.getFullName();
		String email = principal.getEmail();
		String picture = principal.getPicture();
		if (id == null || name == null || email == null) {
			throw new OAuth2AuthenticationException(new OAuth2Error("invalid_user_info"),
					"Google no devolvió un perfil válido");
		}

		ChatUser user = users.findById(id).orElse(null);
		if (user == null) {
			if (users.findByEmail(email).isPresent()) {
				throw new OAuth2AuthenticationException(new OAuth2Error("email_already_registered"),
						"El correo ya está asociado a otra cuenta");
			}
			user = new ChatUser(id, name, email, picture, Instant.now());
		}
		user.updateProfile(name, email, picture, Instant.now());
		users.save(user);
		return principal;
	}
}
