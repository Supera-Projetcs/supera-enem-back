package com.supera.enem.repository;

import com.supera.enem.domain.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Subject findByName(String name);
}
