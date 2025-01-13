package com.supera.enem.repository;

import com.supera.enem.domain.Content;
import com.supera.enem.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT q FROM Question q JOIN q.contents c WHERE c = :content")
    List<Question> findByContents(@Param("content") Content content);
}
