package com.sucoding.blog.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CategoryRequest {
    @NotBlank(message = "카테고리 이름은 필수입니다.")
    @Size(min = 2, max = 10, message = "카테고리 이름은 2자 이상 10자 이하로 입력해주세요.")
    private String name;

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
}
