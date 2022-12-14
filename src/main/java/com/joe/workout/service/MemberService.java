package com.joe.workout.service;

import com.joe.workout.config.JwtTokenProvider;
import com.joe.workout.dto.JoinDto;
import com.joe.workout.dto.LoginDto;
import com.joe.workout.dto.TokenInfo;
import com.joe.workout.entity.Member;
import com.joe.workout.entity.MemberRepository;
import com.joe.workout.entity.RefreshToken;
import com.joe.workout.entity.RefreshTokenRepository;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository tokenRepository;

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
        if (memberRepository.findByNickname(joinMember.getNickname()).isPresent()) {
            throw new RuntimeException("?????? ???????????? ????????? ?????????");
        }
    }

    private void validateMemberId(Member joinMember) {
        if (memberRepository.findByNickname(joinMember.getUsername()).isPresent()) {
            throw new RuntimeException("?????? ???????????? ????????? ?????????");
        }
    }

    @Transactional
    public TokenInfo login(LoginDto loginDto) {
        // 0. ID / PW ??????
        validateLogin(loginDto);

        // 1. ???????????? ID / PW ??? ???????????? ?????? Authentication ?????? ??????
        // ?????? ?????? ????????? ??????????????? Authenticated ?????? false
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        // 2. ?????? ?????? ??? authentication ?????? ??????
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

        // 3. ?????? ?????? ???????????? ?????? ?????? ??? refresh ?????? save ??? ?????? ?????? ??????
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(tokenInfo.getRefreshToken())
                .username(loginDto.getUsername())
                .build();
        if (tokenRepository.existsByUsername(refreshToken.getUsername())) {
            log.info("refresh ?????? ?????? ??????");
            throw new JwtException("?????? ???????????? refresh ?????? ?????????");
        }
        tokenRepository.save(refreshToken);
        return tokenInfo;
    }

    private void validateLogin(LoginDto loginDto) {
        Optional<Member> findMem = memberRepository.findByUsername(loginDto.getUsername());
        if (findMem.isEmpty()) {
            throw new UsernameNotFoundException("???????????? ?????? ????????? ?????????");
        }
        if (!passwordEncoder.matches(loginDto.getPassword(), findMem.get().getPassword())) {
            throw new RuntimeException("????????? ???????????? ?????????");
        }
    }
}
