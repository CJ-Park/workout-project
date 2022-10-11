package com.joe.workout.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(request.getServletContext().toString().equals("/users/login") || request.getServletContext().toString().equals("/users/join") || request.getServletContext().toString().equals("/api/reissue")) {
            chain.doFilter(request, response);
        }

        // Request Header 에서 Access 토큰 추출
        String accessToken = resolveAccessToken((HttpServletRequest) request);

        // validateToken 으로 유효성 검사
        if(accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            // Access 토큰 유효기간이 만료되면 자동으로 /api/reissue 실행

            // 유효하면 Authentication 객체를 가져와서 Security Context 에 저장
            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

    private String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
