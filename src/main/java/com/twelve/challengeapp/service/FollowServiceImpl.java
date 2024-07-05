package com.twelve.challengeapp.service;

import com.twelve.challengeapp.entity.User;
import com.twelve.challengeapp.exception.DuplicateException;
import com.twelve.challengeapp.exception.NotFoundException;
import com.twelve.challengeapp.jwt.UserDetailsImpl;
import com.twelve.challengeapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void addFollow(Long userId, UserDetailsImpl userDetails) {
        validateUser(userId, userDetails);

        User followed = userRepository.findById(userDetails.getUserId()).orElseThrow(() ->
            new NotFoundException("Not Found User"));

        User following = userRepository.findById(userId).orElseThrow(() ->
            new NotFoundException("Not Found User"));

        followed.addFollow(following);
    }

    public User validateUser(Long userId, UserDetailsImpl userDetails) {
        if(userId.equals(userDetails.getUserId())) {
            throw new DuplicateException("Duplicate user");
        }

        return userRepository.findById(userId).orElseThrow(() ->
            new NotFoundException("Not Found Follow User"));
    }

}
