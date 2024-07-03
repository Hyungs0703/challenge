package com.twelve.challengeapp.repository.like;

import com.twelve.challengeapp.entity.Comment;
import com.twelve.challengeapp.entity.like.CommentLike;
import com.twelve.challengeapp.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    Optional<CommentLike> findByCommentAndUser(Comment comment, User user);

    boolean existsByUserAndComment(User user, Comment comment);
}
