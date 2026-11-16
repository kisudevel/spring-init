package com.sucoding.blog.service;

import com.sucoding.blog.dto.auth.SocialAccountResponse;
import com.sucoding.blog.dto.common.PageResponse;
import com.sucoding.blog.dto.member.*;
import com.sucoding.blog.dto.profile.ProfileResponse;
import com.sucoding.blog.enums.MemberRole;
import com.sucoding.blog.exceptions.custom.DuplicateMemberException;
import com.sucoding.blog.exceptions.custom.MemberNotFoundException;
import com.sucoding.blog.exceptions.custom.ReservedMemberValueException;
import com.sucoding.blog.exceptions.custom.RoleException;
import com.sucoding.blog.mapper.MemberMapper;
import com.sucoding.blog.mapper.ProfileMapper;
import com.sucoding.blog.model.Member;
import com.sucoding.blog.model.SocialAccount;
import com.sucoding.blog.repository.MemberRepository;
import com.sucoding.blog.repository.PostRepository;
import com.sucoding.blog.repository.ProfileRepository;
import com.sucoding.blog.repository.SocialAccountRepository;
import com.sucoding.blog.util.PageableFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private static final List<String> SORTABLE_FIELDS = List.of("id", "nickname", "email", "createdAt");
    private static final String DELETED_NICKNAME_SUFFIX = "탈퇴한회원_";
    private static final String DELETED_EMAIL_SUFFIX = "deleted_";
    private static final String DELETED_EMAIL_DOMAIN = "@deleted.local";

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final PostRepository postRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

    public PageResponse<MemberResponse> getAllMembers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Pageable pageDetails = PageableFactory.create(pageNumber, pageSize, sortBy, sortOrder, SORTABLE_FIELDS);
        Page<Member> memberPage = memberRepository.findAll(pageDetails);
        List<Member> members = memberPage.getContent();
        return PageResponse.of(memberPage, memberMapper.toResponseList(members));
    }

    public MemberPublicResponse getMemberById(Long memberId) {
        Member member = findExistingMember(memberId);
        return memberMapper.toPublicResponse(member);
    }

    public MemberMeResponse getCurrentMember(Long memberId) {
        Member member = findExistingMember(memberId);

        // 프로필은 가입할 때 만들지 않으므로, 처음 조회할 때 없으면 빈 프로필을 만들어서 반환한다
        ProfileResponse profileResponse = profileRepository.findByMemberId(memberId)
                .map(profileMapper::toResponse)
                .orElseGet(() -> profileMapper.toEmptyResponse(member));

        // 소셜 계정은 가입할 때 만들므로, 없으면 빈 리스트를 반환한다
        List<SocialAccount> socialAccounts = socialAccountRepository.findByMemberIdOrderByIdAsc(memberId);
        List<SocialAccountResponse> socialAccountResponses = memberMapper.toSocialAccountResponseList(socialAccounts);

        return memberMapper.toMeResponse(member, profileResponse, socialAccountResponses);
    }

    @Transactional
    public MemberAdminResponse setRole(Long adminId, Long memberId, MemberRoleUpdateRequest roleUpdateRequest) {
        Member member = findExistingMember(memberId);

        if (member.getId().equals(adminId)) {
            throw new RoleException();
        }
        member.setRole(roleUpdateRequest.getRole());
        return memberMapper.toAdminResponse(member);
    }

    @Transactional
    public Member createMember(MemberCreateRequest memberCreateRequest) {
        ensureNotReservedEmail(memberCreateRequest.getEmail());
        ensureNotReservedNickname(memberCreateRequest.getNickname());

        if (memberRepository.existsByEmail(memberCreateRequest.getEmail())) {
            throw DuplicateMemberException.duplicateEmail();
        }

        if (memberRepository.existsByNicknameIgnoreCase(memberCreateRequest.getNickname())) {
            throw DuplicateMemberException.duplicateNickname();
        }

        Member memberEntity = memberMapper.toEntity(memberCreateRequest);
        memberEntity.setRole(MemberRole.USER);
        memberEntity.setPassword(passwordEncoder.encode(memberCreateRequest.getPassword()));

        return memberRepository.save(memberEntity);
    }

    @Transactional
    public MemberPublicResponse updateMember(Long memberId, MemberUpdateRequest memberRequest) {
        Member existingMember = findExistingMember(memberId);

        if (memberRequest.getNickname() != null) {
            ensureNotReservedNickname(memberRequest.getNickname());
            if (!memberRequest.getNickname().equalsIgnoreCase(existingMember.getNickname())
                    && memberRepository.existsByNicknameIgnoreCase(memberRequest.getNickname())) {
                throw DuplicateMemberException.duplicateNickname();
            }
            existingMember.setNickname(memberRequest.getNickname());
        }

        if (memberRequest.getPassword() != null) {
            existingMember.setPassword(passwordEncoder.encode(memberRequest.getPassword()));
        }

        return memberMapper.toPublicResponse(existingMember);
    }

    @Transactional
    public void deleteMember(Long memberId) {
        Member member = findExistingMember(memberId);

        member.setDeletedAt(LocalDateTime.now());

        // 닉네임은 개인정보는 아니지만 사이트 정책에 의거하여 남기지 않는다.
        member.setNickname(DELETED_NICKNAME_SUFFIX + memberId);

        // 이메일은 개인정보이므로 즉시 파괴한다.
        member.setEmail(DELETED_EMAIL_SUFFIX + memberId + DELETED_EMAIL_DOMAIN);

        // 게시글은 삭제하지는 않지만, 모두 비공개 처리한다.
        postRepository.hideAllByMemberId(memberId);

        socialAccountRepository.deleteByMemberId(memberId);

        member.setPassword(null);
    }

    private void ensureNotReservedEmail(String email) {
        if (email.endsWith(DELETED_EMAIL_DOMAIN)) {
            throw ReservedMemberValueException.reservedEmail();
        }
    }

    private void ensureNotReservedNickname(String nickname) {
        if (nickname.startsWith(DELETED_NICKNAME_SUFFIX)) {
            throw ReservedMemberValueException.reservedNickname();
        }
    }

    private Member findExistingMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }
}
