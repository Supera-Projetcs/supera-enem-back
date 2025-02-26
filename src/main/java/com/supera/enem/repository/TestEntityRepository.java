package com.supera.enem.repository;

import com.supera.enem.domain.Subject;
import com.supera.enem.domain.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestEntityRepository extends JpaRepository<TestEntity, Long> {
}
