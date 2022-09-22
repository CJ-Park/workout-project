package com.joe.workout.controller;

import com.joe.workout.dto.JoinDto;
import com.joe.workout.dto.LoginDto;
import com.joe.workout.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HomeController {
    private final MemberService memberService;

    @GetMapping("/")
    private String test() {
        return "this is homepage";
    }

    @PostMapping("/join")
    private String join(@RequestBody JoinDto joinDto) {
        return memberService.saveMember(joinDto)
                .getNickname();
    }

    @PostMapping("/login")
    private String login(@RequestBody LoginDto loginDto) {
        return memberService.validateLoginMember(loginDto).getNickname();
    }
}
