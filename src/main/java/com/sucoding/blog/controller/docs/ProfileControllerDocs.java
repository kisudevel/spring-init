package com.sucoding.blog.controller.docs;

import com.sucoding.blog.dto.profile.ProfileRequest;
import com.sucoding.blog.dto.profile.ProfileResponse;
import com.sucoding.blog.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "프로필", description = "회원의 자기소개, 사진, SNS 주소를 보고 고칩니다.")
public interface ProfileControllerDocs {
    @Operation(summary = "프로필 조회",
            description = "로그인 없이 부를 수 있습니다. 프로필을 입력하지 않은 회원은 빈 프로필로 돌려줍니다. "
                    + "없거나 탈퇴한 회원이면 404입니다.")
    @ApiResponse(responseCode = "200", description = "조회됨. 입력하지 않았으면 빈 프로필")
    @ApiResponse(responseCode = "404", description = "없거나 탈퇴한 회원")
    ResponseEntity<ProfileResponse> getProfile(Long memberId);

    @Operation(summary = "프로필 수정",
            description = "로그인한 회원만 호출할 수 있습니다. 자신의 프로필을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정됨. 수정된 프로필 반환")
    @ApiResponse(responseCode = "401", description = "로그인하지 않음")
    @ApiResponse(responseCode = "404", description = "회원이 존재하지 않음")
    @ApiResponse(responseCode = "400", description = "잘못된 요청. 프로필 요청 데이터가 유효하지 않음")
    ResponseEntity<ProfileResponse> updateMyProfile(
            CustomUserDetails userDetails,
            ProfileRequest profileRequest
    );
}
