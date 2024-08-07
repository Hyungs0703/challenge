package com.twelve.challengeapp.dto;

import com.twelve.challengeapp.entity.Post;
import com.twelve.challengeapp.entity.User;
import lombok.Getter;

@Getter
public class LikeResponseDto {

    private User user;
    private Post post;

    public LikeResponseDto(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}

