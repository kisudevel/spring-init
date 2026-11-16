package com.sucoding.blog.repository;

import com.sucoding.blog.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    boolean existsByNameIgnoreCase(@NotBlank(message = "카테고리 이름은 필수입니다.") @Size(min = 2, max = 10, message = "카테고리 이름은 2자 이상 10자 이하로 입력해주세요.") String name);
}
