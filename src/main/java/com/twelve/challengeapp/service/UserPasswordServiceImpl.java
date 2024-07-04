package com.twelve.challengeapp.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.twelve.challengeapp.dto.UserRequestDto;
import com.twelve.challengeapp.dto.UserResponseDto;
import com.twelve.challengeapp.entity.User;
import com.twelve.challengeapp.entity.UserPasswordRecord;
import com.twelve.challengeapp.exception.PasswordMismatchException;
import com.twelve.challengeapp.exception.UserNotFoundException;
import com.twelve.challengeapp.jwt.UserDetailsImpl;
import com.twelve.challengeapp.repository.UserPasswordRepository;
import com.twelve.challengeapp.repository.UserRepository;

@Service
public class UserPasswordServiceImpl implements UserPasswordService {

	private final UserPasswordRepository userPasswordRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserPasswordServiceImpl(UserPasswordRepository userPasswordRepository, UserRepository userRepository,
		PasswordEncoder passwordEncoder) {
		this.userPasswordRepository = userPasswordRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	//비밀번호 변경
	@Override
	@Transactional
	public void userPasswordChange(UserRequestDto.ChangePassword requestDto, UserDetailsImpl userDetails) {
		// 사용자 아이디 확인
		User user = userRepository.findByUsername(requestDto.getUsername())
			.orElseThrow(() -> new UserNotFoundException("The user name does not exist"));

		// 현재 비밀번호 일치 여부 확인
		if (!passwordEncoder.matches(requestDto.getPassword(), userDetails.getPassword())) {
			throw new PasswordMismatchException("Passwords do not match");
		}

		//최근 3개가져오기
		List<UserPasswordRecord> userPasswordRecordList = userPasswordRepository.findTop3ByUserIdOrderByCreatedAtDesc(
			userDetails.getUserId());

		// 새 비밀번호 암호화
		String changePasswordEncoded = passwordEncoder.encode(requestDto.getChangePassword());

		// 최근 3개의 비밀번호와 일치하는지 확인
		for (UserPasswordRecord userPassword : userPasswordRecordList) {
			if (passwordEncoder.matches(requestDto.getChangePassword(), userPassword.getUserPassword())) {
				throw new PasswordMismatchException(
					"Your new password is the same as a recently used password. Please choose a different password.");
			}
		}

		// 비밀번호 변경
		user.ChangePassword(changePasswordEncoded);

		// 새로운 비밀번호 기록 저장
		UserPasswordRecord changePasswordRecord = new UserPasswordRecord(changePasswordEncoded);
		user.addPasswordRecord(changePasswordRecord);

		// 가장 오래된 비밀번호 기록 삭제
		if (userPasswordRecordList.size() >= 3) {
			UserPasswordRecord oldPassword = userPasswordRepository.findByUserIdAndCreatedAt(userDetails.getUserId(),
				userPasswordRecordList.get(2).getCreatedAt());
			user.removePasswordRecord(oldPassword);
		}

		userRepository.save(user);
	}
}
