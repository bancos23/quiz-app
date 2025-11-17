package com.example.quiz_app.repository;

import com.example.quiz_app.model.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    List<UserAnswer> findByAttemptId(long attemptId);
    List<UserAnswer> findByAttemptIdAndQuestionId(long attemptId, long questionId);
}
