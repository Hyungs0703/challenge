package com.twelve.challengeapp.repository;

import com.twelve.challengeapp.entity.Post;
import com.twelve.challengeapp.entity.PostLike;
import com.twelve.challengeapp.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    PostLike findByPostAndUser(Post post, User user);

    boolean existsByUserAndPost(User user, Post post);
}
