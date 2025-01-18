package com.supera.enem.repository;

import com.supera.enem.domain.Content;
import com.supera.enem.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT q FROM Question q JOIN FETCH q.contents c WHERE c = :content")
    List<Question> findByContents(@Param("content") Content content);

    @Query(value = "SELECT * FROM question q JOIN question_contents qc ON q.id = qc.question_id WHERE qc.content_id = :contentId ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestionsByContent(@Param("contentId") Long contentId, @Param("limit") int limit);

    Question getQuestionById(Long id);
}
