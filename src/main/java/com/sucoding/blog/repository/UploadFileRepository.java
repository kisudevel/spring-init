package com.sucoding.blog.repository;

import com.sucoding.blog.model.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {
    Optional<UploadFile> findByStoreName(String storedName);
}
