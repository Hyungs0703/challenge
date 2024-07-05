package com.twelve.challengeapp.service;

import com.twelve.challengeapp.jwt.UserDetailsImpl;

public interface FollowService {

    void addFollow(Long userId, UserDetailsImpl userDetails);
}
