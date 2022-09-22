package com.joe.workout.service;

import com.joe.workout.dto.JoinDto;
import com.joe.workout.dto.LoginDto;
import com.joe.workout.entity.Member;
import com.joe.workout.entity.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public Member saveMember(JoinDto joinDto) {
        validateDuplicateMember(joinDto);
        Member m = Member.createUser(joinDto, new BCryptPasswordEncoder());
        return memberRepository.save(m);
    }

    private void validateDuplicateMember(JoinDto joinDto) {
        Member findMember = memberRepository.findByUsername((joinDto.getUsername()));
        if(findMember != null) {
            throw new IllegalStateException("이미 존재하는 ID 입니다.");
        }
    }

    public Member validateLoginMember(LoginDto loginDto) {
        Member findMember = memberRepository.findByUsername(loginDto.getUsername());
        if(findMember == null) {
            throw new IllegalStateException("존재하지 않는 ID 입니다.");
        }
        if(new BCryptPasswordEncoder().matches(loginDto.getPassword(), findMember.getPassword())) {
            return findMember;
        } else {
         throw new IllegalStateException("잘못된 비밀번호입니다.");
        }
    }
}
