package com.sucoding.blog.mapper;

import com.sucoding.blog.dto.auth.SocialAccountResponse;
import com.sucoding.blog.dto.member.*;
import com.sucoding.blog.dto.profile.ProfileResponse;
import com.sucoding.blog.model.Member;
import com.sucoding.blog.model.SocialAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "tokenInvalidBefore", ignore = true)
    Member toEntity(MemberCreateRequest memberCreateRequest);

    MemberResponse toResponse(Member member);

    List<MemberResponse> toResponseList(List<Member> members);

    MemberPublicResponse toPublicResponse(Member member);

    @Mapping(target = "nickname", source = "member.nickname")
    @Mapping(target = "profile", source = "profile")
    @Mapping(target = "socialAccounts", source = "socialAccounts")
    MemberMeResponse toMeResponse(Member member, ProfileResponse profile, List<SocialAccountResponse> socialAccounts);

    MemberAdminResponse toAdminResponse(Member member);

    SocialAccountResponse toSocialAccountResponse(SocialAccount socialAccount);

    List<SocialAccountResponse> toSocialAccountResponseList(List<SocialAccount> socialAccounts);
}
