package com.sucoding.blog.dto.member;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class MemberUpdateRequest {
    @Size(min = 1, max = 20, message = "비밀번호는 1자 이상 20자 이하로 작성해주세요.")
    private String password;
   
    @Size(min = 1, max = 20, message = "닉네임은 1자 이상 20자 이하로 작성해주세요.")
    private String nickname;

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }
}
