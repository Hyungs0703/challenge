package com.twelve.challengeapp.controller;

import com.twelve.challengeapp.jwt.UserDetailsImpl;
import com.twelve.challengeapp.service.FollowServiceImpl;
import com.twelve.challengeapp.util.SuccessResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<?> addFollow(@PathVariable Long userId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        followService.addFollow(userId, userDetails);
        return SuccessResponseFactory.ok();
    }

}
