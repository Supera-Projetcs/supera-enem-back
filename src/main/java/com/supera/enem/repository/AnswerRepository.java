package com.supera.enem.repository;

import com.supera.enem.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
        boolean existsByTestEntityAndQuestion(TestEntity testEntity, Question question);

        @Query("SELECT a FROM Answer a JOIN a.question q JOIN q.contents c " +
                "WHERE a.testEntity.student = :student AND c = :content")
        List<Answer> findAnswersByStudentAndContent(@Param("student") Student student, @Param("content") Content content);

        @Query("SELECT COUNT(a) FROM Answer a WHERE a.correct = true AND a.testEntity.student.id = :studentId")
        Long countCorrectAnswersByStudentId(@Param("studentId") Long studentId);

        @Query("SELECT COUNT(a) FROM Answer a WHERE a.correct = false AND a.testEntity.student.id = :studentId")
        Long countWrongAnswersByStudentId(@Param("studentId") Long studentId);
}
