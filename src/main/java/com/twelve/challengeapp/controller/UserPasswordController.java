package com.twelve.challengeapp.controller;

import com.twelve.challengeapp.dto.UserRequestDto;
import com.twelve.challengeapp.dto.UserResponseDto;
import com.twelve.challengeapp.jwt.UserDetailsImpl;
import com.twelve.challengeapp.service.UserPasswordService;
import com.twelve.challengeapp.util.SuccessResponseFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserPasswordController {

	private final UserPasswordService userPasswordService;

	public UserPasswordController(UserPasswordService userPasswordService) {
		this.userPasswordService = userPasswordService;
	}

	@PutMapping("/password")
	public ResponseEntity<?> userPasswordChange(@RequestBody UserRequestDto.ChangePassword requestDto,
												@AuthenticationPrincipal UserDetailsImpl userDetails) {
		userPasswordService.userPasswordChange(requestDto, userDetails);
		return SuccessResponseFactory.ok();
	}
}
