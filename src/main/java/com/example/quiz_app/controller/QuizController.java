package com.example.quiz_app.controller;

import com.example.quiz_app.model.Quiz;
import com.example.quiz_app.model.QuizAttempt;
import com.example.quiz_app.service.QuizService;
import jakarta.servlet.http.HttpSession;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    /**
     * GET /quizzes/
     * List all published quizzes.
     */

    @GetMapping
    public String listQuizzes(Model model, HttpSession session) {
        String role = (String) session.getAttribute("role");
        boolean isAdmin = role != null && role.equalsIgnoreCase("admin");

        if (isAdmin)
            model.addAttribute("quizzes", quizService.getAllQuizzes());
        else
            model.addAttribute("quizzes", quizService.getAllPublishedQuizzes());

        model.addAttribute("isAdmin", isAdmin);
        return "quizzes/list";
    }

    /**
     * GET /quizzes/{id}
     * Display quiz with randomized questions/options
     */
    @GetMapping("/{id}")
    public String showQuiz(
            @PathVariable Long id,
            Model model,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Quiz quiz = quizService.getQuizForTaking(id);
        model.addAttribute("quiz", quiz);

        // We'll build a form that submits answers in a multi-value structure
        return "quizzes/take"; // templates/quizzes/take.html
    }

    /**
     * POST /quizzes/{id}
     * Handles quiz submission
     *
     * Form format:
     * answers[QUESTION_ID] = OPTION_ID
     * answers[QUESTION_ID] = OPTION_ID1, OPTION_ID2, ...
     */
    @PostMapping("/{id}")
    public String submitQuiz(
            @PathVariable Long id,
            @RequestParam Map<String, String> requestParams,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        /*
         * requestParams contains keys like:
         * answers[12] = "5"
         * answers[13] = "8,9"
         *
         * We need to convert this into:
         *
         * Map<Long, List<Long>> answersByQuestion
         */
        Map<Long, List<Long>> answersByQuestion = parseAnswers(requestParams);

        QuizAttempt attempt = quizService.submitAttempt(id, userId, answersByQuestion);

        // Redirect to results page
        return "redirect:/quizzes/" + id + "/attempts/" + attempt.getId();
    }

    /**
     * GET /quizzes/{quizId}/attempts/{attemptId}
     * Show quiz attempt results
     */

    @GetMapping("/{quizId}/attempts/{attemptId}")
    public String showAttempt(
            @PathVariable Long quizId,
            @PathVariable Long attemptId,
            Model model,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        QuizAttempt attempt = quizService.getAttemptWithAnswers(attemptId);
        model.addAttribute("attempt", attempt);
        model.addAttribute("quiz", attempt.getQuiz());
        model.addAttribute("score", attempt.getScore());
        model.addAttribute("total", attempt.getQuiz().getQuestions().size());

        return "quizzes/result"; // templates/quizzes/result.html
    }

    /**
     * Helper to parse answers from HTML form
     */
    private Map<Long, List<Long>> parseAnswers(Map<String, String> params) {
        Map<Long, List<Long>> answers = new HashMap<>();

        params.forEach((key, value) -> {
            if (key.startsWith("answers[")) {
                // Extract questionId from key pattern "answers[12]"
                Long questionId = Long.valueOf(
                        key.substring(key.indexOf('[') + 1, key.indexOf(']'))
                );

                List<Long> optionIds = new ArrayList<>();
                if (value != null && !value.isBlank()) {
                    // Could be "5" or "8,9"
                    for (String v : value.split(",")) {
                        optionIds.add(Long.valueOf(v.trim()));
                    }
                }

                answers.put(questionId, optionIds);
            }
        });

        return answers;
    }
}

