package com.sucoding.blog.util;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

// final 클래스로 선언하여 상속을 막는다.
public final class PageableFactory {

    private PageableFactory() {
        // private 생성자를 만들어서 외부에서 인스턴스를 생성하지 못하도록 막는다.
    }

    public static @NonNull Pageable create(Integer pageNumber,
                                           Integer pageSize,
                                           String sortBy,
                                           String sortOrder,
                                           List<String> sortableFields) {
        if (pageNumber < 1 || pageSize < 1) {
            throw new IllegalArgumentException("pageNumber와 pageSize는 1 이상이어야 합니다.");
        }

        if (!sortableFields.contains(sortBy)) {
            throw new IllegalArgumentException("정렬할 수 없는 필드입니다. : " + sortBy);
        }

        if (!sortOrder.equalsIgnoreCase("asc") && !sortOrder.equalsIgnoreCase("desc")) {
            throw new IllegalArgumentException("sortOrder는 asc 또는 desc만 가능합니다. : " + sortOrder);
        }

        Sort sort = Sort.by(
                sortOrder.equalsIgnoreCase("asc")
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC,
                sortBy
        );
        return PageRequest.of(pageNumber - 1, pageSize, sort);
    }
}
