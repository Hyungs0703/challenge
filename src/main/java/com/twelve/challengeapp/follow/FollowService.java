package com.twelve.challengeapp.follow;

import com.twelve.challengeapp.jwt.UserDetailsImpl;

public interface FollowService {

    void follow(Long followingUser, UserDetailsImpl followedUser);

    void unfollow(Long followingUserId, UserDetailsImpl followedUserId);

}
