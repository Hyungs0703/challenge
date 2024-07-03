package com.twelve.challengeapp.repository;

import com.twelve.challengeapp.entity.Comment;
import com.twelve.challengeapp.entity.CommentLike;
import com.twelve.challengeapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    CommentLike findByCommentAndUser(Comment comment, User user);

    boolean existsByUserAndComment(User user, Comment comment);
}
