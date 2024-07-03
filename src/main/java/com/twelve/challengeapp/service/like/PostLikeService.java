package com.twelve.challengeapp.service.like;

import com.twelve.challengeapp.entity.Post;
import com.twelve.challengeapp.jwt.UserDetailsImpl;
import jakarta.transaction.Transactional;
import java.util.List;

public interface PostLikeService {

    @Transactional
    void addLikeToPost(Long postId, UserDetailsImpl userDetails);

    @Transactional
    void deleteLikeFromPost(Long postId, UserDetailsImpl userDetails);

    List<Post> getPosts(UserDetailsImpl userDetails, int page);
}
