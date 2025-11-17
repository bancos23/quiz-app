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

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public boolean isPublished() { return published; }
        public void setPublished(boolean published) { this.published = published; }
    }

    public static class QuestionForm {
        @NotBlank
        private String text;

        @NotBlank
        private String type;

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    public static class OptionForm {
        @NotBlank
        private String text;

        private boolean correct = false;

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }

        public boolean isCorrect() { return correct; }
        public void setCorrect(boolean correct) { this.correct = correct; }
    }

    // --- Helper: ensure user is admin ---
    private boolean isAdmin(HttpSession session) {
        String role = (String) session.getAttribute("role");
        return role != null && role.equalsIgnoreCase("admin");
    }

    /**
     * GET /admin/quizzes
     * List all quizzes for admin (published + drafts)
     */
    @GetMapping
    public String adminListQuizzes(Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/";
        }

        List<Quiz> quizzes = quizService.getAllQuizzes();
        model.addAttribute("quizzes", quizzes);
        model.addAttribute("isAdmin", true);
        return "quizzes/list";
    }

    /**
     * GET /admin/quizzes/create
     * Show quiz creation form
     */
    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/";
        }

        model.addAttribute("quizForm", new QuizForm());
        return "quizzes/create"; // templates/quizzes/create.html
    }

    /**
     * POST /admin/quizzes/create
     * Handle quiz creation
     */
    @PostMapping("/create")
    public String handleCreate(
            @Valid @ModelAttribute("quizForm") QuizForm form,
            BindingResult bindingResult,
            HttpSession session,
            Model model
    ) {
        if (!isAdmin(session)) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            return "quizzes/create";
        }

        Long userId = (Long) session.getAttribute("userId");

        quizService.createQuiz(
                form.getTitle(),
                form.getDescription(),
                form.isPublished(),
                userId
        );

        // after creation, go back to public quizzes list
        return "redirect:/quizzes";
    }

    /**
     * GET /admin/quizzes/{quizId}/questions
     * Show quiz with its questions and a form to add a new question.
     */
    @GetMapping("/{quizId}/questions")
    public String showQuestions(
            @PathVariable Long quizId,
            Model model,
            HttpSession session
    ) {
        if (!isAdmin(session)) {
            return "redirect:/";
        }

        Quiz quiz = quizService.getQuizWithQuestions(quizId);
        model.addAttribute("quiz", quiz);
        model.addAttribute("questionForm", new QuestionForm());
        return "quizzes/admin-questions";
    }

    /**
     * POST /admin/quizzes/{quizId}/questions
     * Add a new question to the quiz.
     */
    @PostMapping("/{quizId}/questions")
    public String addQuestion(
            @PathVariable Long quizId,
            @Valid @ModelAttribute("questionForm") QuestionForm form,
            BindingResult bindingResult,
            HttpSession session,
            Model model
    ) {
        if (!isAdmin(session)) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            Quiz quiz = quizService.getQuizWithQuestions(quizId);
            model.addAttribute("quiz", quiz);
            return "quizzes/admin-questions";
        }

        Question.QuestionType type = Question.QuestionType.valueOf(form.getType());
        quizService.addQuestionToQuiz(quizId, form.getText(), type);

        return "redirect:/admin/quizzes/" + quizId + "/questions";
    }

    /**
     * GET /admin/quizzes/{quizId}/questions/{questionId}/options
     * Show question with its options and a form to add new options.
     */
    /*@GetMapping("/{quizId}/questions/{questionId}/options")
    public String showOptions(
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            HttpSession session,
            Model model
    ) {
        if (!isAdmin(session)) {
            return "redirect:/";
        }

        Question question = quizService.getQuestionWithOptions(questionId);
        model.addAttribute("quizId", quizId);
        model.addAttribute("question", question);
        model.addAttribute("optionForm", new OptionForm());

        return "quizzes/admin-options";
    }*/

    @GetMapping("/{quizId}/questions/{questionId}/options")
    public String manageOptions(
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            HttpSession session,
            Model model
    ) {
        if (!isAdmin(session)) {
            return "redirect:/";
        }
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));

        model.addAttribute("quizId", quizId);
        model.addAttribute("question", question);
        model.addAttribute("optionForm", new OptionForm());

        return "quizzes/admin-options";
    }


    /**
     * POST /admin/quizzes/{quizId}/questions/{questionId}/options
     * Add a new answer option to a question.
     */
    @PostMapping("/{quizId}/questions/{questionId}/options")
    public String addOption(
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @Valid @ModelAttribute("optionForm") OptionForm form,
            BindingResult bindingResult,
            HttpSession session,
            Model model
    ) {
        if (!isAdmin(session)) {
            return "redirect:/";
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid question ID: " + questionId));

        if (bindingResult.hasErrors()) {
            model.addAttribute("quizId", quizId);
            model.addAttribute("question", question);
            return "quizzes/admin-options";
        }

        AnswerOption opt = new AnswerOption();
        opt.setQuestion(question);
        opt.setText(form.getText());
        opt.setCorrect(form.isCorrect());

        System.out.println("Saving option: text=" + form.getText() +
                ", correct=" + form.isCorrect());

        answerOptionRepository.save(opt);

        return "redirect:/admin/quizzes/" + quizId + "/questions/" + questionId + "/options";
    }
}
