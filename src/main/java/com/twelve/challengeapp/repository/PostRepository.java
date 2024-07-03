package com.twelve.challengeapp.repository;

import com.twelve.challengeapp.entity.Post;
import com.twelve.challengeapp.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByPostLikesUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
