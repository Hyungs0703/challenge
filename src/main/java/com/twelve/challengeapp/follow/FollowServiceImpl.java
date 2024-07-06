package com.twelve.challengeapp.follow;

import com.twelve.challengeapp.entity.User;
import com.twelve.challengeapp.exception.AlreadyException;
import com.twelve.challengeapp.exception.DuplicateException;
import com.twelve.challengeapp.exception.NotFoundException;
import com.twelve.challengeapp.jwt.UserDetailsImpl;
import com.twelve.challengeapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Override
    public void follow(Long followingUserId, UserDetailsImpl followedUser) {
        if (isFollowing(followedUser.getUserId(), followedUser.getUserId())) {
            throw new AlreadyException("이미 팔로우 중입니다.");
        } else if (followingUserId.equals(followedUser.getUserId())) {
            throw new DuplicateException("자기 자신은 팔로우 할 수 없습니다.");
        }
        User followingUser = userRepository.findById(followingUserId).orElseThrow(() ->
            new NotFoundException("Not Found User"));
        followRepository.save(new Follow(followingUser, followedUser.getUser()));
    }

    @Override
    public void unfollow(Long followingUserId, UserDetailsImpl followedUser) {
        if (!isFollowing(followingUserId, followedUser.getUserId())) {
            throw new NotFoundException("팔로우 중이 아닙니다.");
        }
        followRepository.deleteById(new FollowId(followingUserId, followedUser.getUserId()));
    }

    public boolean isFollowing(Long followingUserId, Long followedUserId) {
        return followRepository.existsById(new FollowId(followingUserId, followedUserId));
    }

}
