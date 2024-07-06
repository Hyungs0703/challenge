package com.twelve.challengeapp.follow;

import com.twelve.challengeapp.jwt.UserDetailsImpl;
import com.twelve.challengeapp.util.SuccessResponseFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users/{userId}/followers")
public class FollowController {

    private final FollowServiceImpl followService;

    @PostMapping
    public ResponseEntity<?> addFollowing(@PathVariable @Valid Long userId,
        @AuthenticationPrincipal UserDetailsImpl followedUser) {

        followService.follow(userId, followedUser);
        return SuccessResponseFactory.ok();
    }

    @DeleteMapping
    public ResponseEntity<?> unfollow(@PathVariable @Valid Long userId,
        @AuthenticationPrincipal UserDetailsImpl followedUser) {

        followService.unfollow(userId, followedUser);
        return SuccessResponseFactory.ok();
    }
}
