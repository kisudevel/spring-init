package com.sucoding.blog.dto.member;

import com.sucoding.blog.enums.MemberRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class MemberRoleUpdateRequest {
    @NotNull(message = "권한은 필수입니다.")
    private MemberRole role;
}
