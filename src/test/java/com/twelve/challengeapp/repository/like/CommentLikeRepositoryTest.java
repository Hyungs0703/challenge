package com.twelve.challengeapp.repository.like;

import com.twelve.challengeapp.common.RepositoryTest;
import com.twelve.challengeapp.entity.Comment;
import com.twelve.challengeapp.entity.User;
import java.util.HashSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


@RepositoryTest
class CommentLikeRepositoryTest {

    @Test
    @DisplayName("댓글에 좋아요가 저장되는지 확인")
    void test_insertCommentLike() {
        //Given
        User user = User.builder().id(1L).build();
        Comment comment = Comment.builder().id(1L).user(user).commentLikes(new HashSet<>()).build();
        comment.addLike(user);


    }

    @Test
    @DisplayName("해당 댓글 좋아요 삭제 되는지 확인 테스트")
    void test_deleteCommentLike() {
        User user = User.builder().id(1L).build();
        Comment comment = Comment.builder().id(1L).user(user).commentLikes(new HashSet<>()).build();

        comment.removeLike(user);
    }
}
