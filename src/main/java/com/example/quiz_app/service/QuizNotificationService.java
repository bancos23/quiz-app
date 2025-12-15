package com.example.quiz_app.service;

import com.example.quiz_app.dto.OptionDTO;
import com.example.quiz_app.dto.QuestionDTO;
import com.example.quiz_app.dto.QuizDTO;
import com.example.quiz_app.dto.QuizUpdateMessage;
import com.example.quiz_app.dto.QuizUpdateMessage.UpdateType;
import com.example.quiz_app.model.AnswerOption;
import com.example.quiz_app.model.Question;
import com.example.quiz_app.model.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for sending real-time WebSocket notifications when quiz data is modified
 */
@Service
public class QuizNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public QuizNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Convert Quiz entity to DTO for safe serialization
     */
    public QuizDTO toQuizDTO(Quiz quiz) {
        QuizDTO dto = new QuizDTO();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        dto.setDescription(quiz.getDescription());
        dto.setPublished(quiz.isPublished());
        if (quiz.getCreatedBy() != null) {
            dto.setCreatedByUsername(quiz.getCreatedBy().getUsername());
        }
        if (quiz.getQuestions() != null) {
            dto.setQuestionCount(quiz.getQuestions().size());
        }
        return dto;
    }

    /**
     * Convert Question entity to DTO for safe serialization
     */
    public QuestionDTO toQuestionDTO(Question question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(question.getId());
        dto.setQuizId(question.getQuiz().getId());
        dto.setText(question.getText());
        dto.setType(question.getType().name());
        dto.setDisplayType(question.getDisplayType());
        if (question.getOptions() != null) {
            dto.setOptionCount(question.getOptions().size());
        }
        return dto;
    }

    /**
     * Convert AnswerOption entity to DTO for safe serialization
     */
    public OptionDTO toOptionDTO(AnswerOption option) {
        OptionDTO dto = new OptionDTO();
        dto.setId(option.getId());
        dto.setQuestionId(option.getQuestion().getId());
        dto.setText(option.getText());
        dto.setCorrect(option.isCorrect());
        return dto;
    }

    // Quiz notifications
    public void notifyQuizCreated(Quiz quiz) {
        QuizUpdateMessage message = new QuizUpdateMessage(UpdateType.QUIZ_CREATED, quiz.getId());
        message.setData(toQuizDTO(quiz));
        message.setMessage("New quiz created: " + quiz.getTitle());
        messagingTemplate.convertAndSend("/topic/quizzes", message);
    }

    public void notifyQuizUpdated(Quiz quiz) {
        QuizUpdateMessage message = new QuizUpdateMessage(UpdateType.QUIZ_UPDATED, quiz.getId());
        message.setData(toQuizDTO(quiz));
        message.setMessage("Quiz updated: " + quiz.getTitle());
        messagingTemplate.convertAndSend("/topic/quizzes", message);
        messagingTemplate.convertAndSend("/topic/quiz/" + quiz.getId(), message);
    }

    public void notifyQuizPublishToggled(Quiz quiz) {
        QuizUpdateMessage message = new QuizUpdateMessage(UpdateType.QUIZ_PUBLISHED, quiz.getId());
        message.setData(toQuizDTO(quiz));
        message.setMessage("Quiz " + (quiz.isPublished() ? "published" : "unpublished") + ": " + quiz.getTitle());
        messagingTemplate.convertAndSend("/topic/quizzes", message);
        messagingTemplate.convertAndSend("/topic/quiz/" + quiz.getId(), message);
    }

    public void notifyQuizDeleted(Long quizId) {
        QuizUpdateMessage message = new QuizUpdateMessage(UpdateType.QUIZ_DELETED, quizId);
        message.setMessage("Quiz deleted");
        messagingTemplate.convertAndSend("/topic/quizzes", message);
    }

    // Question notifications
    public void notifyQuestionAdded(Question question) {
        QuizUpdateMessage message = new QuizUpdateMessage(UpdateType.QUESTION_ADDED,
                question.getQuiz().getId(), question.getId());
        message.setData(toQuestionDTO(question));
        message.setMessage("New question added");
        messagingTemplate.convertAndSend("/topic/quiz/" + question.getQuiz().getId() + "/questions", message);
    }

    public void notifyQuestionUpdated(Question question) {
        QuizUpdateMessage message = new QuizUpdateMessage(UpdateType.QUESTION_UPDATED,
                question.getQuiz().getId(), question.getId());
        message.setData(toQuestionDTO(question));
        message.setMessage("Question updated");
        messagingTemplate.convertAndSend("/topic/quiz/" + question.getQuiz().getId() + "/questions", message);
    }

    public void notifyQuestionDeleted(Long quizId, Long questionId) {
        QuizUpdateMessage message = new QuizUpdateMessage(UpdateType.QUESTION_DELETED, quizId, questionId);
        message.setMessage("Question deleted");
        messagingTemplate.convertAndSend("/topic/quiz/" + quizId + "/questions", message);
    }

    // Option notifications
    public void notifyOptionAdded(AnswerOption option, Long quizId) {
        QuizUpdateMessage message = new QuizUpdateMessage(UpdateType.OPTION_ADDED,
                quizId, option.getQuestion().getId(), option.getId());
        message.setData(toOptionDTO(option));
        message.setMessage("New option added");
        messagingTemplate.convertAndSend("/topic/question/" + option.getQuestion().getId() + "/options", message);
    }

    public void notifyOptionUpdated(AnswerOption option, Long quizId) {
        QuizUpdateMessage message = new QuizUpdateMessage(UpdateType.OPTION_UPDATED,
                quizId, option.getQuestion().getId(), option.getId());
        message.setData(toOptionDTO(option));
        message.setMessage("Option updated");
        messagingTemplate.convertAndSend("/topic/question/" + option.getQuestion().getId() + "/options", message);
    }

    public void notifyOptionToggled(AnswerOption option, Long quizId) {
        QuizUpdateMessage message = new QuizUpdateMessage(UpdateType.OPTION_TOGGLED,
                quizId, option.getQuestion().getId(), option.getId());
        message.setData(toOptionDTO(option));
        message.setMessage("Option correctness toggled");
        messagingTemplate.convertAndSend("/topic/question/" + option.getQuestion().getId() + "/options", message);
    }

    public void notifyOptionDeleted(Long quizId, Long questionId, Long optionId) {
        QuizUpdateMessage message = new QuizUpdateMessage(UpdateType.OPTION_DELETED, quizId, questionId, optionId);
        message.setMessage("Option deleted");
        messagingTemplate.convertAndSend("/topic/question/" + questionId + "/options", message);
    }
}

