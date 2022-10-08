package com.joe.workout.service;

import com.joe.workout.config.JwtTokenProvider;
import com.joe.workout.dto.JoinDto;
import com.joe.workout.dto.LoginDto;
import com.joe.workout.dto.TokenInfo;
import com.joe.workout.entity.Member;
import com.joe.workout.entity.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void join(JoinDto joinDto) {
        Member joinMember = Member.builder().username(joinDto.getUsername())
                .password(passwordEncoder.encode(joinDto.getPassword()))
                .nickname(joinDto.getNickname())
                .roles(Collections.singletonList("ROLE_USER"))
                .build();
        validateMemberId(joinMember);
        validateMemberNickname(joinMember);
        memberRepository.save(joinMember);
    }

    private void validateMemberNickname(Member joinMember) {
        if(memberRepository.findByNickname(joinMember.getNickname()).isPresent()) {
            throw new RuntimeException("이미 존재하는 닉네임 입니다");
        }
    }

    private void validateMemberId(Member joinMember) {
        if(memberRepository.findByNickname(joinMember.getUsername()).isPresent()) {
            throw new RuntimeException("이미 존재하는 아이디 입니다");
        }
    }

    @Transactional
    public TokenInfo login(LoginDto loginDto) {
        // 1. 로그인된 ID / PW 를 기반으로 하는 Authentication 객체 생성
        // 이때 생긴 객체의 인증여부인 Authenticated 값은 false
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
        // 2. 실제 검증
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        // 3. 인증 정보 기반으로 토큰 생성 후 반환
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
        return tokenInfo;
    }
}
