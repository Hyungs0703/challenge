package com.twelve.challengeapp.service;

import com.twelve.challengeapp.dto.UserRequestDto;
import com.twelve.challengeapp.dto.UserResponseDto;
import com.twelve.challengeapp.entity.User;
import com.twelve.challengeapp.entity.UserPasswordRecord;
import com.twelve.challengeapp.entity.UserRole;
import com.twelve.challengeapp.exception.DuplicateException;
import com.twelve.challengeapp.exception.MismatchException;
import com.twelve.challengeapp.exception.NotFoundException;
import com.twelve.challengeapp.jwt.UserDetailsImpl;
import com.twelve.challengeapp.repository.UserPasswordRepository;
import com.twelve.challengeapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserPasswordRepository userPasswordRepository;

	@Override
	@Transactional
	public void registerUser(UserRequestDto.Register requestDto) {
		validateDuplicateUsername(requestDto.getUsername());
		User user = createUserFromRequest(requestDto);
		user.addPasswordRecord(new UserPasswordRecord(passwordEncoder.encode(requestDto.getPassword())));

		userRepository.save(user);
		log.info("User {} registered successfully", user.getUsername());
	}

	@Override
	public UserResponseDto getUser(UserDetailsImpl userDetails) {
		return new UserResponseDto(userDetails.getUser());
	}


	@Override
	@Transactional
	public UserResponseDto editUser(UserRequestDto.EditInfo requestDto, UserDetailsImpl userDetails) {
		validatePasswordMatch(requestDto.getPassword(), userDetails.getPassword());

		User user = userDetails.getUser();
		user.editUserInfo(requestDto.getNickname(), requestDto.getIntroduce());

		User updatedUser = userRepository.save(user);
		log.info("User {} updated successfully", user.getUsername());
		return new UserResponseDto(updatedUser);
	}


	@Override
	@Transactional
	public void userPasswordChange(UserRequestDto.ChangePassword requestDto, UserDetailsImpl userDetails) {
		User user = userRepository.findByUsername(requestDto.getUsername())
			.orElseThrow(() -> new NotFoundException("The user name does not exist"));

		validatePasswordMatch(requestDto.getPassword(), userDetails.getPassword());
		validateNewPassword(userDetails.getUserId(), requestDto.getChangePassword());

		String changePasswordEncoded = passwordEncoder.encode(requestDto.getChangePassword());
		user.ChangePassword(changePasswordEncoded);
		log.info("User {}'s password changed successfully", user.getUsername());

		UserPasswordRecord changePasswordRecord = new UserPasswordRecord(changePasswordEncoded);
		user.addPasswordRecord(changePasswordRecord);
		removeOldPasswordRecordIfNecessary(userDetails.getUserId());

		userRepository.save(user);
	}

	@Override
	@Transactional
	public void withdraw(UserRequestDto.Withdrawal requestDto, UserDetailsImpl userDetails) {
		validateUsernameMatch(requestDto.getUsername(), userDetails.getUsername());
		validatePasswordMatch(requestDto.getPassword(), userDetails.getPassword());

		User user = userDetails.getUser();
		user.withdrawal(UserRole.WITHDRAWAL);
		userRepository.save(user);
		log.info("User {} withdrew their account successfully", user.getUsername());
	}

	private void validateUsernameMatch(String requestUsername, String username) {
		if (!requestUsername.equals(username)) {
			throw new MismatchException("Login ID does not match");
		}
	}

	private void validateDuplicateUsername(String username) {
		if (userRepository.existsByUsername(username)) {
			throw new DuplicateException("Duplicate username");
		}
	}

	private void validatePasswordMatch(String inputPassword, String storedPassword) {
		if (!passwordEncoder.matches(inputPassword, storedPassword)) {
			throw new MismatchException("Passwords do not match");
		}
	}

	private void validateNewPassword(Long userId, String newPassword) {
		List<UserPasswordRecord> recentPasswords = userPasswordRepository.findTop3ByUserIdOrderByCreatedAtDesc(userId);
		for (UserPasswordRecord record : recentPasswords) {
			if (passwordEncoder.matches(newPassword, record.getUserPassword())) {
				throw new MismatchException("The new password must not be the same as any of the recent passwords.");
			}
		}
	}

	private void removeOldPasswordRecordIfNecessary(Long userId) {
		List<UserPasswordRecord> passwordRecords = userPasswordRepository.findTop3ByUserIdOrderByCreatedAtDesc(userId);
		if (passwordRecords.size() >= 3) {
			UserPasswordRecord oldestPasswordRecord = passwordRecords.get(2);
			userPasswordRepository.deleteById(oldestPasswordRecord.getId());
		}
	}

	private User createUserFromRequest(UserRequestDto.Register requestDto) {
		return User.builder()
			.username(requestDto.getUsername())
			.password(passwordEncoder.encode(requestDto.getPassword()))
			.nickname(requestDto.getNickname())
			.introduce(requestDto.getIntroduce())
			.email(requestDto.getEmail())
			.role(UserRole.USER)
			.build();
	}

}
