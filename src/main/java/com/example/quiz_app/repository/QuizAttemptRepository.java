package com.example.quiz_app.repository;

import com.example.quiz_app.model.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByUserIdAndQuizId(long userId, long quizId);
}
