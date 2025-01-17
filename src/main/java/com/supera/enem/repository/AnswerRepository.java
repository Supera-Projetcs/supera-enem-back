package com.supera.enem.repository;

import com.supera.enem.domain.Answer;
import com.supera.enem.domain.Content;
import com.supera.enem.domain.Question;
import com.supera.enem.domain.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
        boolean existsByTestEntityAndQuestion(TestEntity testEntity, Question question);
}
