package com.sucoding.blog.dto.member;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sucoding.blog.enums.MemberRole;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonPropertyOrder({"id", "nickname", "email", "role", "createdAt"})
public class MemberResponse {
    private Long id;
    private String nickname;
    private String email;
    private MemberRole role;
    private LocalDateTime createdAt;
}
