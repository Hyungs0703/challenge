package com.twelve.challengeapp.controller.like;

import com.twelve.challengeapp.dto.CommentResponseDto;
import com.twelve.challengeapp.entity.Comment;
import com.twelve.challengeapp.jwt.UserDetailsImpl;
import com.twelve.challengeapp.service.like.CommentLikeServiceImpl;
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
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/comments")
public class CommentLikeController {

    private final CommentLikeServiceImpl commentLikeService;


    @PostMapping("/{commentId}/likes")
    public ResponseEntity<?> commentAddLike(@PathVariable Long postId,
                                            @PathVariable Long commentId,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        commentLikeService.addLikeToComment(postId, commentId, userDetails);

        return SuccessResponseFactory.ok();
    }

    @GetMapping("/likes")
    public ResponseEntity<?> getComments(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam(defaultValue = "0") int page) {

        List<Comment> commentList = commentLikeService.getComments(userDetails, page);
        List<CommentResponseDto> commentLikeList = commentList.stream()
            .map(CommentResponseDto::new)
            .toList();

        return SuccessResponseFactory.ok(commentLikeList);
    }


    @DeleteMapping("/{commentId}/likes")
    public ResponseEntity<?> commentDeleteLike(@PathVariable Long postId,
                                                @PathVariable Long commentId,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentLikeService.deleteLikeFromComment(postId, commentId, userDetails);

        return SuccessResponseFactory.ok();
    }

}
