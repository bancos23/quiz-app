package com.example.quiz_app.repository;

import com.example.quiz_app.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByPublished(boolean published);
}
