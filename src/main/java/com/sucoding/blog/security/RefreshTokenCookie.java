package com.sucoding.blog.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RefreshTokenCookie {
    public static final String NAME = "refreshToken";
    private static final String PATH = "/api/public/auth/refresh";

    private final Long refreshExpirationMillis;
    private final String sameSite;

    public RefreshTokenCookie(@Value("${jwt.refresh-expiration}") Long refreshExpirationMillis,
                              @Value("${app.refresh-cookie.same-site}") String sameSite) {
        this.refreshExpirationMillis = refreshExpirationMillis;
        this.sameSite = sameSite;
    }

    private ResponseCookie.ResponseCookieBuilder base(String value) {
        return ResponseCookie.from(NAME, value)
                // 자바스크립트가 읽을 수 없다.
                .httpOnly(true)
                // HTTPS로만 전송한다
                .secure(true)
                // 다른 사이트에서 시작된 요청에는 붙지 않는다
                .sameSite(sameSite)
                // 재발급 요청에만 붙는다. 노출 범위를 우리가 좁힌다
                .path(PATH);
    }

    // 로그인할 때 내려보낸다
    public ResponseCookie create(String token) {
        return base(token).maxAge(Duration.ofMillis(refreshExpirationMillis)).build();
    }

    // 값이 비어 있고 수명이 0인 쿠키로 덮어써서 지운다
    public ResponseCookie expire() {
        return base("").maxAge(0).build();
    }
}
