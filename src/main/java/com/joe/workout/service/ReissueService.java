package com.joe.workout.service;

import com.joe.workout.config.JwtTokenProvider;
import com.joe.workout.dto.TokenInfo;
import com.joe.workout.dto.TokenRequestDto;
import com.joe.workout.entity.Member;
import com.joe.workout.entity.MemberRepository;
import com.joe.workout.entity.RefreshToken;
import com.joe.workout.entity.RefreshTokenRepository;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReissueService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    public TokenInfo refresh(TokenRequestDto tokenRequestDto) {
        // Refresh 토큰 유효성 검사
        if (tokenRequestDto.getRefreshToken() != null && jwtTokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            // access 토큰 쪼개서 username 가져오고 refresh 토큰과 비교
            String username = jwtTokenProvider.parseClaims(tokenRequestDto.getAccessToken()).getSubject();
            if (!Objects.equals(username, jwtTokenProvider.parseClaims(tokenRequestDto.getRefreshToken()).getSubject())) {
                throw new JwtException("유효하지 않은 refresh 토큰 입니당");
            }
            RefreshToken findRefreshToken = refreshTokenRepository.findByUsername(username);
            if (findRefreshToken == null) {
                throw new UsernameNotFoundException("사용자를 찾을 수 없어용");
            }
            if (!findRefreshToken.getRefreshToken().equals(tokenRequestDto.getRefreshToken())) {
                throw new JwtException("유효하지 않은 토큰 입니당");
            }

            // Access 토큰 재발급
            Optional<Member> findMem = memberRepository.findByUsername(username);
            String authorities = findMem.get().getAuthorities()
                    .stream().map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));
            String newAccessToken = jwtTokenProvider.recreateAccessToken(username, authorities);

            // Refresh 토큰 만료시간 1일 미만일 시 refresh 토큰 update 후 발급
            if (jwtTokenProvider.reissueRefreshToken(tokenRequestDto.getRefreshToken())) {
                RefreshToken refreshToken = findRefreshToken.update(jwtTokenProvider.createRefreshToken(username));
                refreshTokenRepository.save(refreshToken);
                return TokenInfo.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken.getRefreshToken())
                        .grantType("Bearer")
                        .build();
            }

            return TokenInfo.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(tokenRequestDto.getRefreshToken())
                    .grantType("Bearer")
                    .build();
        }
        throw new JwtException("유효하지 않은 refresh 토큰 입니당");
    }
}
