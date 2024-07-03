package com.twelve.challengeapp.repository.like;

import com.twelve.challengeapp.entity.Post;
import com.twelve.challengeapp.entity.like.PostLike;
import com.twelve.challengeapp.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByPostAndUser(Post post, User user);

    boolean existsByUserAndPost(User user, Post post);
}
