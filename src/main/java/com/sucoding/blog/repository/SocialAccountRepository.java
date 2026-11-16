package com.sucoding.blog.repository;

import com.sucoding.blog.enums.AuthProvider;
import com.sucoding.blog.model.SocialAccount;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, Integer> {
    @EntityGraph(attributePaths = {"member"})
    Optional<SocialAccount> findByProviderAndProviderId(AuthProvider authProvider, String sub);

    @Modifying
    @Query("delete from SocialAccount s where s.member.id = :memberId")
    void deleteByMemberId(@Param("memberId") Long memberId);

    List<SocialAccount> findByMemberIdOrderByIdAsc(Long memberId);
}
