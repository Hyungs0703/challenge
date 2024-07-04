package com.twelve.challengeapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.twelve.challengeapp.dto.UserRequestDto;
import com.twelve.challengeapp.dto.UserResponseDto;
import com.twelve.challengeapp.entity.User;
import com.twelve.challengeapp.entity.UserPasswordRecord;
import com.twelve.challengeapp.entity.UserRole;
import com.twelve.challengeapp.exception.DuplicateUsernameException;
import com.twelve.challengeapp.exception.PasswordMismatchException;
import com.twelve.challengeapp.exception.UserNotFoundException;
import com.twelve.challengeapp.exception.UsernameMismatchException;
import com.twelve.challengeapp.jwt.UserDetailsImpl;
import com.twelve.challengeapp.repository.UserPasswordRepository;
import com.twelve.challengeapp.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserPasswordRepository userPasswordRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private User user;
    private UserDetailsImpl userDetails;
    private UserRequestDto.Register registerDto;
    private UserRequestDto.EditInfo editDto;
    private UserRequestDto.Withdrawal withdrawalDto;
    private UserRequestDto.ChangePassword changePasswordDto;

    private final Long USER_ID = 1L;
    private final String USERNAME = "testUser";
    private final String USER_PASSWORD = "encodedPassword";
    private final String USER_NICKNAME = "testNickname";
    private final String USER_INTRODUCE = "testIntroduce";
    private final String USER_EMAIL = "test123@email.com";
    private final UserRole userRole = UserRole.USER;
    private final String NEW_PASSWORD = "NewPassword";
    private final String NEW_NICKNAME = "NewNickName";
    private final String New_INTRODUCE = "NewIntroduce";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        user = User.builder()
            .id(USER_ID)
            .username(USERNAME)
            .password(USER_PASSWORD)
            .nickname(USER_NICKNAME)
            .introduce(USER_INTRODUCE)
            .email(USER_EMAIL)
            .role(userRole)
            .build();

        userDetails = new UserDetailsImpl(user);

        registerDto = UserRequestDto.Register.builder()
            .username(USERNAME)
            .password(USER_PASSWORD)
            .nickname(USER_NICKNAME)
            .introduce(USER_INTRODUCE)
            .email(USER_EMAIL)
            .build();

        editDto = UserRequestDto.EditInfo.builder()
            .password(USER_PASSWORD)
            .nickname(NEW_NICKNAME)
            .introduce(New_INTRODUCE)
            .build();

        withdrawalDto = UserRequestDto.Withdrawal.builder()
            .username(USERNAME)
            .password(USER_PASSWORD)
            .build();

        changePasswordDto = UserRequestDto.ChangePassword.builder()
            .username(USERNAME)
            .password(USER_PASSWORD)
            .changePassword(NEW_PASSWORD)
            .build();
    }

    @Test
    @DisplayName("성공적인 사용자 등록")
    public void registerUser_Success() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn(USER_PASSWORD);

        userServiceImpl.registerUser(registerDto);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("성공적인 사용자 조회")
    public void getUser_Success() {
        UserResponseDto response = userServiceImpl.getUser(userDetails);

        assertEquals(USERNAME, response.getUsername());
        assertEquals(USER_NICKNAME, response.getNickname());
        assertEquals(USER_INTRODUCE, response.getIntroduce());
        assertEquals(USER_EMAIL, response.getEmail());
    }

    @Test
    @DisplayName("성공적인 사용자 수정")
    public void editUser_Success() {
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDto response = userServiceImpl.editUser(editDto, userDetails);

        verify(userRepository, times(1)).save(any(User.class));
        assertEquals(NEW_NICKNAME, response.getNickname());
        assertEquals(New_INTRODUCE, response.getIntroduce());
    }

    @Test
    @DisplayName("비밀번호가 정상적으로 변경되는 테스트")
    void userPasswordChange_ValidChange() {
        when(userRepository.findByUsername(changePasswordDto.getUsername()))
            .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(changePasswordDto.getPassword(), userDetails.getPassword()))
            .thenReturn(true);
        when(passwordEncoder.encode(changePasswordDto.getChangePassword()))
            .thenReturn(NEW_PASSWORD);

        when(userPasswordRepository.findTop3ByUserIdOrderByCreatedAtDesc(userDetails.getUserId()))
            .thenReturn(List.of(new UserPasswordRecord("someOldPassword")));

        userServiceImpl.userPasswordChange(changePasswordDto, userDetails);

        verify(userRepository, times(1)).save(user);
        assertEquals(NEW_PASSWORD, user.getPassword());
    }


    @Test
    @DisplayName("성공적인 계정 탈퇴")
    @Transactional
    public void withdraw_Success() {
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        userServiceImpl.withdraw(withdrawalDto, userDetails);

        verify(userRepository, times(1)).save(any(User.class));
        assertEquals(UserRole.WITHDRAWAL, user.getRole()); // 추가 검증
    }

    @Test
    @DisplayName("중복된 사용자명으로 인한 등록 실패")
    public void validateDuplicateUsername_ThrowsException() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        DuplicateUsernameException thrown = assertThrows(
            DuplicateUsernameException.class,
            () -> userServiceImpl.registerUser(registerDto),
            "Expected registerUser() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Duplicate username"));
    }

    @Test
    @DisplayName("비밀번호 불일치로 인한 수정 실패")
    public void validatePasswordMatch_ThrowsException() {
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        PasswordMismatchException thrown = assertThrows(
            PasswordMismatchException.class,
            () -> userServiceImpl.editUser(editDto, userDetails),
            "Expected editUser() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Passwords do not match"));
    }

    @Test
    @DisplayName("잘못된 사용자명으로 인한 비밀번호 변경 실패")
    @Transactional
    public void userPasswordChange_UserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        UserNotFoundException thrown = assertThrows(
            UserNotFoundException.class,
            () -> userServiceImpl.userPasswordChange(changePasswordDto, userDetails),
            "Expected userPasswordChange() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("The user name does not exist"));
    }

    @Test
    @DisplayName("최근 사용된 비밀번호와 일치로 인한 비밀번호 변경 실패")
    @Transactional
    public void userPasswordChange_RecentPasswordMatch() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(changePasswordDto.getPassword(), user.getPassword())).thenReturn(true);

        List<UserPasswordRecord> recentPasswords = List.of(
            new UserPasswordRecord("encodedPassword1"),
            new UserPasswordRecord("encodedPassword2"),
            new UserPasswordRecord("encodedPassword3")
        );
        when(userPasswordRepository.findTop3ByUserIdOrderByCreatedAtDesc(anyLong())).thenReturn(recentPasswords);

        // 일치하는 새 비밀번호를 두 번째 matches 호출에서 반환하도록 처리
        when(passwordEncoder.matches(changePasswordDto.getChangePassword(), "encodedPassword1")).thenReturn(false);
        when(passwordEncoder.matches(changePasswordDto.getChangePassword(), "encodedPassword2")).thenReturn(false);
        when(passwordEncoder.matches(changePasswordDto.getChangePassword(), "encodedPassword3")).thenReturn(true);

        PasswordMismatchException thrown = assertThrows(
            PasswordMismatchException.class,
            () -> userServiceImpl.userPasswordChange(changePasswordDto, userDetails),
            "Expected userPasswordChange() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("The new password must not be the same as any of the recent passwords."));
    }

    @Test
    @DisplayName("잘못된 사용자명으로 인한 계정 탈퇴 실패")
    public void withdraw_UsernameMismatch() {
        UserRequestDto.Withdrawal wrongWithdrawalDto = UserRequestDto.Withdrawal.builder()
            .username("wronguser")
            .password("password")
            .build();

        UsernameMismatchException thrown = assertThrows(
            UsernameMismatchException.class,
            () -> userServiceImpl.withdraw(wrongWithdrawalDto, userDetails),
            "Expected withdraw() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Login ID does not match"));
    }
}
