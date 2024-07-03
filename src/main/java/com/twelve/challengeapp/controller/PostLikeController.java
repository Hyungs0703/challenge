package com.twelve.challengeapp.controller;

import com.twelve.challengeapp.jwt.UserDetailsImpl;
import com.twelve.challengeapp.service.like.LikeService;
import com.twelve.challengeapp.util.SuccessResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts/{postId}/like")
@RequiredArgsConstructor
public class PostLikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<?> postAddLikeCount(@PathVariable Long postId,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        likeService.addLikeToPost(postId, userDetails);
        return SuccessResponseFactory.ok();
    }

    @DeleteMapping
    public ResponseEntity<?> deletePostLike(@PathVariable Long postId,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        likeService.deleteLikeFromPost(postId, userDetails);
        return SuccessResponseFactory.ok();
    }

}
