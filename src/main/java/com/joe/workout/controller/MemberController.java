package com.joe.workout.controller;

import com.joe.workout.config.JwtAuthenticationFilter;
import com.joe.workout.dto.JoinDto;
import com.joe.workout.dto.LoginDto;
import com.joe.workout.dto.TokenInfo;
import com.joe.workout.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class MemberController {
    private final MemberService memberService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

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
