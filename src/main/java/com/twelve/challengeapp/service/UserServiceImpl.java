package com.twelve.challengeapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.twelve.challengeapp.dto.UserRequestDto;
import com.twelve.challengeapp.dto.UserResponseDto;
import com.twelve.challengeapp.entity.User;
import com.twelve.challengeapp.entity.UserPasswordRecord;
import com.twelve.challengeapp.entity.UserRole;
import com.twelve.challengeapp.exception.DuplicateUsernameException;
import com.twelve.challengeapp.exception.PasswordMismatchException;
import com.twelve.challengeapp.exception.UsernameMismatchException;
import com.twelve.challengeapp.jwt.UserDetailsImpl;
import com.twelve.challengeapp.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void registerUser(UserRequestDto.Register requestDto) {
		if (userRepository.existsByUsername(requestDto.getUsername())) {
			throw new DuplicateUsernameException("Duplicate username.");
		}

		User user = User.builder()
			.username(requestDto.getUsername())
			.password(passwordEncoder.encode(requestDto.getPassword()))
			.nickname(requestDto.getNickname())
			.introduce(requestDto.getIntroduce())
			.email(requestDto.getEmail())
			.role(UserRole.USER)
			.build();

		UserPasswordRecord userPasswordRecord = new UserPasswordRecord(
			passwordEncoder.encode(requestDto.getPassword()));
		user.addPasswordRecord(userPasswordRecord);

		userRepository.save(user); // 사용자 정보 저장
	}

	//회원 정보
	@Override
	public UserResponseDto getUser(UserDetailsImpl userDetails) {

		return new UserResponseDto(userDetails.getUser());
	}

	@Override
	public UserResponseDto editUser(UserRequestDto.EditInfo requestDto, UserDetailsImpl userDetails) {
		if (!passwordEncoder.matches(requestDto.getPassword(), userDetails.getPassword())) {
			throw new PasswordMismatchException("Passwords do not match");
		}

		User user = userDetails.getUser();
		user.editUserInfo(requestDto.getNickname(), requestDto.getIntroduce());
		return new UserResponseDto(userRepository.save(user));
	}

	@Override
	public void withdraw(UserRequestDto.Withdrawal requestDto, UserDetailsImpl userDetails) {
		if (!requestDto.getUsername().equals(userDetails.getUsername())) {
			throw new UsernameMismatchException("Login ID does not match");
		}

		if (!passwordEncoder.matches(requestDto.getPassword(), userDetails.getPassword())) {
			throw new PasswordMismatchException("Passwords do not match");
		}

		User user = userDetails.getUser();
		user.withdrawal(UserRole.WITHDRAWAL);
		userRepository.save(user);
	}
}
