package com.sucoding.blog.exceptions.custom;

public final class DuplicateCategoryException extends RuntimeException {
    public DuplicateCategoryException() {
        super("이미 존재하는 카테고리입니다.");
    }
}
