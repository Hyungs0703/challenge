package com.twelve.challengeapp.controller;

import com.twelve.challengeapp.util.SuccessResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController("/api/users/{userId}/followers")
public class FollowController {


    @PostMapping
    public ResponseEntity<?> addFollow() {

        return SuccessResponseFactory.ok();
    }

}
