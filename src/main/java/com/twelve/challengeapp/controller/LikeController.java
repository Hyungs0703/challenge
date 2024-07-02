package com.twelve.challengeapp.controller;

import com.twelve.challengeapp.dto.UserResponseDto;
import com.twelve.challengeapp.entity.User;
import com.twelve.challengeapp.jwt.UserDetailsImpl;
import com.twelve.challengeapp.service.like.LikeService;
import com.twelve.challengeapp.util.SuccessResponseFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts/{postId}/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<?> postAddLikeCount(@PathVariable Long postId,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        likeService.postAddLikeCount(postId, userDetails);
        return SuccessResponseFactory.ok();
    }

    @GetMapping("/users")
    public ResponseEntity<?> postLikeUserList(@PathVariable Long postId) {
        List<User> userList = likeService.postLikeUserList(postId);
        List<UserResponseDto> userResponseDtoList = userList.stream()
                                                            .map(UserResponseDto::new)
                                                            .toList();
        return SuccessResponseFactory.ok(userResponseDtoList);
    }

    @DeleteMapping
    public ResponseEntity<?> deletePostLike(@PathVariable Long postId,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        likeService.deletePostLike(postId, userDetails);
        return SuccessResponseFactory.ok();
    }
}
