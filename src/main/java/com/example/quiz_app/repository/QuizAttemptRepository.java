package com.example.quiz_app.repository;

import com.example.quiz_app.model.QuizAttempt;
import com.example.quiz_app.model.User;
import com.example.quiz_app.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    List<QuizAttempt> findByUserId(long userId);

    List<QuizAttempt> findByUser(User user);

    List<QuizAttempt> findByQuizId(Long quizId);

    List<QuizAttempt> findByUserIdAndQuizId(long userId, long quizId);
}
