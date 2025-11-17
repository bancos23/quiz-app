package com.example.quiz_app.repository;

import com.example.quiz_app.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findByPublished(boolean published);

    @EntityGraph(attributePaths = {"questions", "questions.options"})
    List<Quiz> findAll();

    @EntityGraph(attributePaths = {"questions", "questions.options" })
    Quiz findById(long id);
}
