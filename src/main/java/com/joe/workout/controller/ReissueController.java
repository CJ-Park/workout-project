package com.joe.workout.controller;

import com.joe.workout.dto.TokenInfo;
import com.joe.workout.service.ReissueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReissueController {
    private final ReissueService reissueService;

    @PostMapping("/api/reissue")
    public ResponseEntity<TokenInfo> validateRefreshToken(@RequestHeader("Authorization") String refreshToken) {
        log.info("refresh 진행");
        TokenInfo tokenInfo = reissueService.refresh(refreshToken);
        return ResponseEntity.ok(tokenInfo);
    }
}
