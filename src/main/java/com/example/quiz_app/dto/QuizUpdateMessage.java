package com.example.quiz_app.dto;

/**
 * DTO for WebSocket notification messages
 */
public class QuizUpdateMessage {

    public enum UpdateType {
        QUIZ_CREATED,
        QUIZ_UPDATED,
        QUIZ_DELETED,
        QUIZ_PUBLISHED,
        QUESTION_ADDED,
        QUESTION_UPDATED,
        QUESTION_DELETED,
        OPTION_ADDED,
        OPTION_UPDATED,
        OPTION_DELETED,
        OPTION_TOGGLED
    }

    private UpdateType type;
    private Long quizId;
    private Long questionId;
    private Long optionId;
    private Object data;
    private String message;

    public QuizUpdateMessage() {
    }

    public QuizUpdateMessage(UpdateType type, Long quizId) {
        this.type = type;
        this.quizId = quizId;
    }

    public QuizUpdateMessage(UpdateType type, Long quizId, Long questionId) {
        this.type = type;
        this.quizId = quizId;
        this.questionId = questionId;
    }

    public QuizUpdateMessage(UpdateType type, Long quizId, Long questionId, Long optionId) {
        this.type = type;
        this.quizId = quizId;
        this.questionId = questionId;
        this.optionId = optionId;
    }

    // Getters and setters
    public UpdateType getType() {
        return type;
    }

    public void setType(UpdateType type) {
        this.type = type;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

