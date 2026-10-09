package com.example.controller;

import com.example.model.DiscussionQuestion;
import com.example.model.DiscussionReply;
import com.example.service.DiscussionService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/discussions")
public class DiscussionController {

    private final DiscussionService discussionService;

    public DiscussionController(DiscussionService discussionService) {
        this.discussionService = discussionService;
    }

    @GetMapping
    public String listQuestions(@RequestParam(value = "search", required = false) String search,
                                @RequestParam(value = "category", required = false) String category,
                                @RequestParam(value = "resolved", required = false) Boolean resolved,
                                Model model) {
        model.addAttribute("questions", discussionService.getAllQuestions(search, category, resolved));
        model.addAttribute("searchQuery", search);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedResolved", resolved);
        return "discussions/list";
    }

    @GetMapping("/{id}")
    public String viewQuestion(@PathVariable("id") Long id, Model model) {
        try {
            DiscussionQuestion question = discussionService.getQuestionById(id);
            model.addAttribute("question", question);
            if (!model.containsAttribute("newReply")) {
                model.addAttribute("newReply", new DiscussionReply());
            }
            return "discussions/detail";
        } catch (IllegalArgumentException e) {
            return "redirect:/discussions";
        }
    }

    @GetMapping("/new")
    public String newQuestionForm(Model model) {
        if (!model.containsAttribute("question")) {
            model.addAttribute("question", new DiscussionQuestion());
        }
        return "discussions/form";
    }

    @PostMapping("/new")
    public String createQuestion(@Valid @ModelAttribute("question") DiscussionQuestion question,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            return "discussions/form";
        }
        DiscussionQuestion saved = discussionService.saveQuestion(question);
        redirectAttributes.addFlashAttribute("successMessage", "Your question has been posted to the gardening community!");
        return "redirect:/discussions/" + saved.getId();
    }

    @PostMapping("/{id}/reply")
    public String addReply(@PathVariable("id") Long id,
                           @Valid @ModelAttribute("newReply") DiscussionReply reply,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Reply content and author name cannot be empty.");
            return "redirect:/discussions/" + id;
        }
        discussionService.addReply(id, reply);
        redirectAttributes.addFlashAttribute("successMessage", "Your advice has been added to the discussion!");
        return "redirect:/discussions/" + id;
    }

    @PostMapping("/{id}/toggle-resolve")
    public String toggleResolve(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        discussionService.toggleResolved(id);
        redirectAttributes.addFlashAttribute("successMessage", "Discussion status updated.");
        return "redirect:/discussions/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteQuestion(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            discussionService.deleteQuestion(id);
            redirectAttributes.addFlashAttribute("successMessage", "Discussion question removed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete question: " + e.getMessage());
        }
        return "redirect:/discussions";
    }

    @PostMapping("/replies/{replyId}/delete")
    public String deleteReply(@PathVariable("replyId") Long replyId,
                              @RequestParam("questionId") Long questionId,
                              RedirectAttributes redirectAttributes) {
        try {
            discussionService.deleteReply(replyId);
            redirectAttributes.addFlashAttribute("successMessage", "Reply removed.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/discussions/" + questionId;
    }

    @PostMapping("/replies/{replyId}/upvote")
    public String upvoteReply(@PathVariable("replyId") Long replyId,
                              @RequestParam("questionId") Long questionId,
                              RedirectAttributes redirectAttributes) {
        discussionService.upvoteReply(replyId);
        redirectAttributes.addFlashAttribute("successMessage", "Upvoted advice as helpful!");
        return "redirect:/discussions/" + questionId;
    }
}
