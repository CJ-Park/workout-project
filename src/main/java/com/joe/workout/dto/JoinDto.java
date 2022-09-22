package com.joe.workout.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinDto {

    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min = 8, message = "최소 8자리 이상 입력해주세요.")
    private String password;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    private String nickname;
}
