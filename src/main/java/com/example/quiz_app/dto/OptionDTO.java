package com.example.quiz_app.dto;

/**
 * DTO for serializing AnswerOption data for WebSocket messages
 */
public class OptionDTO {
    private Long id;
    private Long questionId;
    private String text;
    private boolean correct;

    public OptionDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}

