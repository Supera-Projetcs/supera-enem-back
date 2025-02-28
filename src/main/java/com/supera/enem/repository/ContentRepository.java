package com.supera.enem.repository;

import com.supera.enem.domain.Content;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {
    Content findByName(String name);

    List<Content> findContentsBySubjectId(Long id);
}
