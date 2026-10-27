package py.com.mcodev.springchatsocket.dto;

import py.com.mcodev.springchatsocket.model.ChatUser;

public record ChatUserResponse(String id, String name, String email, String picture) {
	public static ChatUserResponse from(ChatUser user) {
		return new ChatUserResponse(user.getGoogleId(), user.getName(), user.getEmail(), user.getPicture());
	}
}
