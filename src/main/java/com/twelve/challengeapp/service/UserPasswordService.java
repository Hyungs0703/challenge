package com.twelve.challengeapp.service;

import com.twelve.challengeapp.dto.UserRequestDto;
import com.twelve.challengeapp.dto.UserResponseDto;
import com.twelve.challengeapp.jwt.UserDetailsImpl;

public interface UserPasswordService {
	//비밀번호 변경
	void userPasswordChange(UserRequestDto.ChangePassword requestDto, UserDetailsImpl userDetails);
}
