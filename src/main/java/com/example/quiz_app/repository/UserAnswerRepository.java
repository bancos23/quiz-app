package com.example.quiz_app.repository;

import com.example.quiz_app.model.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    void deleteByQuestionId(Long questionId);
    void deleteBySelectedOptionId(Long optionId);
}
