package com.twelve.challengeapp.controller;

import com.twelve.challengeapp.jwt.UserDetailsImpl;
import com.twelve.challengeapp.service.like.LikeServiceImpl;
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
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/comments/{commentId}/likes")
public class CommentLikeController {

    private final LikeServiceImpl likeService;


    @PostMapping
    public ResponseEntity<?> commentAddLike(@PathVariable Long postId,
                                            @PathVariable Long commentId,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        likeService.addLikeToComment(postId, commentId, userDetails);

        return SuccessResponseFactory.ok();
    }

    @DeleteMapping
    public ResponseEntity<?> commentDeleteLike(@PathVariable Long postId,
                                                @PathVariable Long commentId,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        likeService.deleteLikeFromComment(postId, commentId, userDetails);

        return SuccessResponseFactory.ok();
    }

}
