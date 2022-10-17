package com.joe.workout.controller;

import com.joe.workout.dto.JoinDto;
import com.joe.workout.dto.LoginDto;
import com.joe.workout.dto.TokenInfo;
import com.joe.workout.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody JoinDto joinDto) {
        memberService.join(joinDto);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<TokenInfo> tokenLogin(@RequestBody LoginDto loginDto) {
        TokenInfo tokenInfo = memberService.login(loginDto);
        return ResponseEntity.ok(tokenInfo);
    }
}
