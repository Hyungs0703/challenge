package com.twelve.challengeapp.follow;

import com.twelve.challengeapp.entity.Timestamped;
import com.twelve.challengeapp.entity.User;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

@Entity
public class Follow extends Timestamped {

    @EmbeddedId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private FollowId followId = new FollowId();

    public Follow(User followingUser, User followedUser) {
        this.followingUser = followingUser;
        this.followedUser = followedUser;
    }

    @ManyToOne
    @MapsId("followingUserId")
    @JoinColumn(name = "following_user_id")
    User followingUser;


    @ManyToOne
    @MapsId("followedUserId")
    @JoinColumn(name = "followed_userd_id")
    User followedUser;
}
