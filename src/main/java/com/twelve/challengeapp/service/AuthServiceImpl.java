package com.twelve.challengeapp.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.twelve.challengeapp.dto.UserRequestDto;
import com.twelve.challengeapp.entity.RefreshToken;
import com.twelve.challengeapp.entity.User;
import com.twelve.challengeapp.entity.UserRole;
import com.twelve.challengeapp.exception.AlreadyException;
import com.twelve.challengeapp.exception.MismatchException;
import com.twelve.challengeapp.repository.UserRepository;

@Service
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;

	private final JwtServiceImpl jwtService;

	private final PasswordEncoder passwordEncoder;

	private final RefreshTokenServiceImpl refreshTokenService;

	public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtServiceImpl jwtService,
		RefreshTokenServiceImpl refreshTokenService) {

		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.refreshTokenService = refreshTokenService;
	}

	@Override
	public String login(UserRequestDto.Login requestDto) {

		User user = userRepository.findByUsername(requestDto.getUsername())
			.orElseThrow(() -> new UsernameNotFoundException("Invalid username"));

		// 탈퇴한 계정처리
		if (UserRole.WITHDRAWAL.equals(user.getRole())) {
			throw new AlreadyException("Already Withdrawal user");
		}

		// 삭제된 계정 처리
		if (UserRole.DELETED.equals(user.getRole())) {
			throw  new AlreadyException("Already Deleted user");
		}

		if (passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
			String accessToken = jwtService.generateAccessToken(user.getUsername(), user.getRole());
			RefreshToken refreshToken = refreshTokenService.updateRefreshToken(user);

			jwtService.setRefreshTokenAtCookie(refreshToken);
			return accessToken;
		} else {
			throw new MismatchException("Invalid password");
		}
	}

	@Override
	public void logout(String header) {

		String token = jwtService.getAccessTokenFromHeader(header);
		String username = jwtService.extractUsername(token);

		refreshTokenService.deleteRefreshToken(username);

		jwtService.deleteRefreshTokenAtCookie();
	}
}
