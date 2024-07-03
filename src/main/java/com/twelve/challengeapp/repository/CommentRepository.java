package com.twelve.challengeapp.repository;

import com.twelve.challengeapp.entity.Comment;
import com.twelve.challengeapp.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);

    Page<Comment> findByCommentLikesUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
