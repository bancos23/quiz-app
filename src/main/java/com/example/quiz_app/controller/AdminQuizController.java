package com.example.quiz_app.controller;

import com.example.quiz_app.model.AnswerOption;
import com.example.quiz_app.model.Quiz;
import com.example.quiz_app.model.Question;
import com.example.quiz_app.repository.AnswerOptionRepository;
import com.example.quiz_app.repository.QuestionRepository;
import com.example.quiz_app.service.QuizService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/quizzes")
public class AdminQuizController {

    private final QuizService quizService;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;

    public AdminQuizController(QuizService quizService, QuestionRepository questionRepository, AnswerOptionRepository answerOptionRepository) {
        this.quizService = quizService;
        this.questionRepository = questionRepository;
        this.answerOptionRepository = answerOptionRepository;
    }

    public static class QuizForm {
        @NotBlank
        private String title;

        private String description;

        private boolean published;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isPublished() {
            return published;
        }

        public void setPublished(boolean published) {
            this.published = published;
        }
    }

    public static class QuestionForm {
        @NotBlank
        private String text;

        @NotBlank
        private String type;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class OptionForm {
        @NotBlank
        private String text;

        private boolean correct = false;

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

    @GetMapping
    public String adminListQuizzes(Model model) {
        List<Quiz> quizzes = quizService.getAllQuizzes();
        model.addAttribute("quizzes", quizzes);
        model.addAttribute("isAdmin", true);
        return "quizzes/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("quizForm", new QuizForm());
        return "quizzes/create"; // templates/quizzes/create.html
    }

    @PostMapping("/create")
    public String handleCreate(@Valid @ModelAttribute("quizForm") QuizForm form, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) return "quizzes/create";

        Long userId = (Long) session.getAttribute("userId");

        quizService.createQuiz(form.getTitle(), form.getDescription(), form.isPublished(), userId);

        return "redirect:/quizzes";
    }

    @GetMapping("/{quizId}/questions")
    public String showQuestions(@PathVariable Long quizId, Model model) {
        Quiz quiz = quizService.getQuizWithQuestions(quizId);
        model.addAttribute("quiz", quiz);
        model.addAttribute("questionForm", new QuestionForm());
        return "quizzes/admin-questions";
    }

    @PostMapping("/{quizId}/questions")
    public String addQuestion(@PathVariable Long quizId, @Valid @ModelAttribute("questionForm") QuestionForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            Quiz quiz = quizService.getQuizWithQuestions(quizId);
            model.addAttribute("quiz", quiz);
            return "quizzes/admin-questions";
        }

        Question.QuestionType type = Question.QuestionType.valueOf(form.getType());
        quizService.addQuestionToQuiz(quizId, form.getText(), type);

        return "redirect:/admin/quizzes/" + quizId + "/questions";
    }

    @GetMapping("/{quizId}/questions/{questionId}/options")
    public String manageOptions(@PathVariable Long quizId, @PathVariable Long questionId, Model model) {
        Question question = questionRepository.findById(questionId).orElseThrow(/* ignore */);

        model.addAttribute("quizId", quizId);
        model.addAttribute("question", question);
        model.addAttribute("optionForm", new OptionForm());

        return "quizzes/admin-options";
    }

    @PostMapping("/{quizId}/questions/{questionId}/options")
    public String addOption(@PathVariable Long quizId, @PathVariable Long questionId, @Valid @ModelAttribute("optionForm") OptionForm form, BindingResult bindingResult, Model model) {
        Question question = questionRepository.findById(questionId).orElseThrow(/* ignore */);

        if (bindingResult.hasErrors()) {
            model.addAttribute("quizId", quizId);
            model.addAttribute("question", question);
            return "quizzes/admin-options";
        }

        AnswerOption opt = new AnswerOption();
        opt.setQuestion(question);
        opt.setText(form.getText());
        opt.setCorrect(form.isCorrect());

        answerOptionRepository.save(opt);

        return "redirect:/admin/quizzes/" + quizId + "/questions/" + questionId + "/options";
    }
}
