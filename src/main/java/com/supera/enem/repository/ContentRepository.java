package com.supera.enem.repository;

import com.supera.enem.domain.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long> {
    Content findByName(String name);
}
