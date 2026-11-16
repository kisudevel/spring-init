package com.sucoding.blog.exceptions.custom;

public class UploadedFileNotFoundException extends RuntimeException {
    public UploadedFileNotFoundException() {
        super("파일을 찾을 수 없습니다.");
    }
}

