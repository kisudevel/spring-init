package com.sucoding.blog.exceptions.custom;

public final class ReservedMemberValueException extends RuntimeException {
    private ReservedMemberValueException(String message) {
        super(message);
    }

    public static ReservedMemberValueException reservedEmail() {
        return new ReservedMemberValueException("해당 이메일은 사용할 수 없는 이메일입니다.");
    }

    public static ReservedMemberValueException reservedNickname() {
        return new ReservedMemberValueException("해당 닉네임은 사용할 수 없는 닉네임입니다.");
    }
}
