package com.twelve.challengeapp.service;

import com.twelve.challengeapp.dto.UserRequestDto;
import com.twelve.challengeapp.dto.UserResponseDto;
import com.twelve.challengeapp.jwt.UserDetailsImpl;

public interface UserService {

	void registerUser(UserRequestDto.Register requestDto);

	//회원 정보 가져오기
	UserResponseDto getUser(UserDetailsImpl userDetails);
	//회원 정보 수정
	UserResponseDto editUser(UserRequestDto.EditInfo requestDto, UserDetailsImpl userDetails);

    //비밀번호 변경
    void userPasswordChange(UserRequestDto.ChangePassword requestDto, UserDetailsImpl userDetails);

    //회원 탈퇴
	void withdraw(UserRequestDto.Withdrawal requestDto, UserDetailsImpl userDetails);
}
