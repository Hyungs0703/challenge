package com.twelve.challengeapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

@Entity
public class PostLike extends Like {

    @Builder
    public PostLike(User user, Post post) {
        this.user = user;
        this.post = post;
    }

    @ManyToOne
    @JoinColumn(name = "post_id")
    Post post;
}
