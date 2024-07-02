package com.twelve.challengeapp.dto;

import com.twelve.challengeapp.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
	private String username;
	private String nickname;
	private String introduce;
	private String email;

	public UserResponseDto(User user) {
		this.username = user.getUsername();
		this.nickname = user.getNickname();
		this.introduce = user.getIntroduce();
		this.email = user.getEmail();
	}

}
