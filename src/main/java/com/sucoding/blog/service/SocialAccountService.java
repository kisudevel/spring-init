package com.sucoding.blog.service;

import com.sucoding.blog.dto.auth.GoogleUserInfo;
import com.sucoding.blog.enums.AuthProvider;
import com.sucoding.blog.enums.MemberRole;
import com.sucoding.blog.model.Member;
import com.sucoding.blog.model.SocialAccount;
import com.sucoding.blog.repository.MemberRepository;
import com.sucoding.blog.repository.SocialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialAccountService {
    private final SocialAccountRepository socialAccountRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Member loginOrRegister(AuthProvider authProvider, GoogleUserInfo googleUserInfo) {

        // 1. 이미 연결된 소셜 계정이면 그 회원으로 로그인한다
        SocialAccount linked = socialAccountRepository.findByProviderAndProviderId(authProvider, googleUserInfo.sub())
                .orElse(null);

        if (linked != null) {
            return linked.getMember();
        }

        // 2. 같은 이메일로 이미 가입한 회원이 있으면 그 회원에 연결한다 (kisubootcamp@gmail.com)
        Member member = memberRepository.findByEmailAndDeletedAtIsNull(googleUserInfo.email()).orElse(null);

        // 3. 연결할 회원이 없으면 새로 만든다
        if (member == null) {
            member = new Member();
            member.setEmail(googleUserInfo.email());
            member.setNickname(googleUserInfo.name());
            member.setRole(MemberRole.USER);
            member.setPassword(null); // 비밀번호는 null로 설정
            memberRepository.save(member);
        }

        // 4. 소셜 계정을 연결한다
        SocialAccount socialAccount = new SocialAccount();
        socialAccount.setProvider(authProvider);
        socialAccount.setProviderId(googleUserInfo.sub());
        socialAccount.setMember(member);
        socialAccountRepository.save(socialAccount);

        return member;
    }
}
