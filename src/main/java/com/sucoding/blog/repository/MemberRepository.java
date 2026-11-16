package com.sucoding.blog.repository;

import com.sucoding.blog.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByIdAndDeletedAtIsNull(Long memberId);

    boolean existsByNicknameIgnoreCase(String nickname);

    Optional<Member> findByEmailAndDeletedAtIsNull(String email);
}
