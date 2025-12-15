package com.example.quiz_app.service;

import com.example.quiz_app.model.*;
import com.example.quiz_app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;

    @Autowired
    public QuizService(QuizRepository quizRepository,
                       UserRepository userRepository,
                       QuizAttemptRepository quizAttemptRepository,
                       UserAnswerRepository userAnswerRepository,
                       QuestionRepository questionRepository,
                       AnswerOptionRepository answerOptionRepository) {
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.userAnswerRepository = userAnswerRepository;
        this.questionRepository = questionRepository;
        this.answerOptionRepository = answerOptionRepository;
    }

    @Transactional
    public Quiz getQuizForTaking(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(/* ignore */);

        quiz.getQuestions().size();
        quiz.getQuestions().forEach(q -> q.getOptions().size());

        Collections.shuffle(quiz.getQuestions());
        quiz.getQuestions().forEach(q -> Collections.shuffle(q.getOptions()));

        return quiz;
    }

    @Transactional
    public QuizAttempt submitAttempt(Long quizId, Long userId, Map<Long, List<Long>> answers) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(/* ignore */);
        User user = userRepository.findById(userId).orElseThrow(/* ignore */);

        quiz.getQuestions().forEach(q -> q.getOptions().size());

        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setUser(user);
        attempt.setStartedAt(LocalDateTime.now());

        List<UserAnswer> userAnswers = new ArrayList<>();
        int score = 0;

        for (Question q : quiz.getQuestions()) {
            Long questionId = q.getId();
            List<Long> selectedOptionIds =
                    answers.getOrDefault(questionId, Collections.emptyList());

            Map<Long, AnswerOption> optionsById = new HashMap<>();
            for (AnswerOption option : q.getOptions())
                optionsById.put(option.getId(), option);

            for (Long optionId : selectedOptionIds) {
                AnswerOption option = optionsById.get(optionId);
                if (option == null) {
                    throw new IllegalArgumentException("Invalid answer option ID: " + optionId + " for question ID: " + questionId);
                }

                UserAnswer userAnswer = new UserAnswer();
                userAnswer.setAttempt(attempt);
                userAnswer.setQuestion(q);
                userAnswer.setSelectedOption(option);
                userAnswers.add(userAnswer);
            }

            if (isAnswerCorrect(q, selectedOptionIds))
                score++;
        }

        attempt.setScore(score);
        attempt.setCompletedAt(LocalDateTime.now());
        attempt.setAnswers(userAnswers);

        QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);
        userAnswerRepository.saveAll(userAnswers);

        return savedAttempt;
    }

    private boolean isAnswerCorrect(Question question, List<Long> selectedOptionIds) {
        Set<Long> selectedSet = new HashSet<>(selectedOptionIds);

        Set<Long> correctIds = new HashSet<>();
        for (AnswerOption option : question.getOptions())
            if (option.isCorrect())
                correctIds.add(option.getId());

        return selectedSet.equals(correctIds);
    }

    @Transactional(readOnly = true)
    public List<QuizAttempt> getAttemptsForUserAndQuiz(Long userId, Long quizId) {
        return quizAttemptRepository.findByUserIdAndQuizId(userId, quizId);
    }

    @Transactional(readOnly = true)
    public QuizAttempt getAttemptWithAnswers(Long attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId).orElseThrow(/* ignore */);

        attempt.getAnswers().size();
        attempt.getAnswers().forEach(a -> {
            a.getQuestion().getId();
            a.getSelectedOption().getId();
        });

        return attempt;
    }

    @Transactional(readOnly = true)
    public List<Quiz> getAllPublishedQuizzes() {
        return quizRepository.findByPublished(true);
    }

    @Transactional(readOnly = true)
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    @Transactional
    public Quiz createQuiz(String title, String description, boolean published, Long createdByUserId) {

        User creator = null;
        if (createdByUserId != null)
            creator = userRepository.findById(createdByUserId).orElse(null);

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setDescription(description);
        quiz.setPublished(published);
        quiz.setCreatedBy(creator);
        return quizRepository.save(quiz);
    }

    @Transactional
    public Quiz getQuizWithQuestions(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(/* ignore */);
        quiz.getQuestions().size();
        return quiz;
    }

    @Transactional
    public Question addQuestionToQuiz(Long quizId, String text, Question.QuestionType type) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(/* ignore */);

        Question q = new Question();
        q.setQuiz(quiz);
        q.setText(text);
        q.setType(type);

        return questionRepository.save(q);
    }

    @Transactional(readOnly = true)
    public Question getQuestionWithOptions(Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(/* ignore */);
        question.getOptions().size();
        return question;
    }

    @Transactional
    public AnswerOption addOptionToQuestion(Long questionId, String text, boolean correct) {
        Question question = questionRepository.findById(questionId).orElseThrow(/* ignore */);

        AnswerOption option = new AnswerOption();
        option.setQuestion(question);
        option.setText(text);
        option.setCorrect(correct);

        return answerOptionRepository.save(option);
    }
}
