package com.twelve.challengeapp.controller;

import com.twelve.challengeapp.jwt.UserDetailsImpl;
import com.twelve.challengeapp.service.like.LikeServiceImpl;
import com.twelve.challengeapp.util.SuccessResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts/{post_id}/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeServiceImpl likeService;

    @PostMapping
    public ResponseEntity<?> postAddLikeCount(@PathVariable Long post_id,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        likeService.postAddLikeCount(post_id, userDetails);
        return SuccessResponseFactory.ok();

    }

}
