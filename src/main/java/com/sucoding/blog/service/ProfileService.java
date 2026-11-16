package com.sucoding.blog.service;

import com.sucoding.blog.dto.profile.ProfileRequest;
import com.sucoding.blog.dto.profile.ProfileResponse;
import com.sucoding.blog.exceptions.custom.MemberNotFoundException;
import com.sucoding.blog.mapper.ProfileMapper;
import com.sucoding.blog.model.Member;
import com.sucoding.blog.model.Profile;
import com.sucoding.blog.repository.MemberRepository;
import com.sucoding.blog.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;
    private final ProfileMapper profileMapper;

    public ProfileResponse getProfileByMemberId(Long memberId) {
        Member member = memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .orElseThrow(MemberNotFoundException::new);

        return profileRepository.findByMemberId(memberId)
                .map(profileMapper::toResponse)
                .orElseGet(() -> profileMapper.toEmptyResponse(member));
    }

    @Transactional
    public ProfileResponse updateMyProfile(Long memberId, ProfileRequest profileRequest) {
        Member member = memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .orElseThrow(MemberNotFoundException::new);

        // 프로필은 가입할 때 만들지 않으므로, 처음 수정할 때 만든다
        Profile profile = profileRepository.findByMemberId(memberId)
                .orElseGet(() -> {
                    Profile newProfile = new Profile();
                    newProfile.setMember(member);
                    return newProfile;
                });

        profileMapper.updateEntityFromRequest(profile, profileRequest);
        Profile savedProfile = profileRepository.save(profile);
        return profileMapper.toResponse(savedProfile);
    }
}
