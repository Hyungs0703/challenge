package com.twelve.challengeapp.controller;

import com.twelve.challengeapp.dto.PostResponseDto;
import com.twelve.challengeapp.entity.Post;
import com.twelve.challengeapp.jwt.UserDetailsImpl;
import com.twelve.challengeapp.service.like.LikeService;
import com.twelve.challengeapp.service.like.LikeServiceImpl;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostLikeController {

    private final LikeServiceImpl likeService;

    @PostMapping("/{postId}/likes")
    public ResponseEntity<?> postAddLikeCount(@PathVariable Long postId,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        likeService.addLikeToPost(postId, userDetails);
        return SuccessResponseFactory.ok();
    }

    @GetMapping("/likes")
    public ResponseEntity<?> getPosts(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                    @RequestParam(defaultValue = "0") int page) {

        List<Post> postList = likeService.getPosts(userDetails, page);
        List<PostResponseDto> postResponseDtoList = postList.stream()
                                                                .map(PostResponseDto::new)
                                                                .toList();
        return SuccessResponseFactory.ok(postResponseDtoList);
    }


    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<?> deletePostLike(@PathVariable Long postId,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        likeService.deleteLikeFromPost(postId, userDetails);
        return SuccessResponseFactory.ok();
    }

}
