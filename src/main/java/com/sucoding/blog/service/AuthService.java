package com.sucoding.blog.service;

import com.sucoding.blog.exceptions.custom.MemberNotFoundException;
import com.sucoding.blog.model.Member;
import com.sucoding.blog.repository.MemberRepository;
import com.sucoding.blog.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void logout(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        // 로그아웃 시, 해당 회원의 모든 refresh token을 삭제한다.
        refreshTokenRepository.deleteByMemberId(memberId);

        // 토큰 발급 시각은 초 단위라, 같은 초에 발급된 토큰까지 막으려면 다음 초로 올린다
        member.setTokenInvalidBefore((LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(1)));
    }
}
