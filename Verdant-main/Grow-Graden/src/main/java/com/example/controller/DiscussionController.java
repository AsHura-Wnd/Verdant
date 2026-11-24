package com.example.controller;

import com.example.exception.ResourceNotFoundException;
import com.example.model.DiscussionQuestion;
import com.example.model.DiscussionReply;
import com.example.security.OwnershipService;
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
    private final OwnershipService ownershipService;

    public DiscussionController(DiscussionService discussionService, OwnershipService ownershipService) {
        this.discussionService = discussionService;
        this.ownershipService = ownershipService;
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
            model.addAttribute("canModify", ownershipService.canModify(question.getAuthorUsername()));
            if (!model.containsAttribute("newReply")) {
                DiscussionReply reply = new DiscussionReply();
                String currentUser = ownershipService.getCurrentUsername();
                if (currentUser != null) {
                    reply.setAuthor(currentUser);
                }
                model.addAttribute("newReply", reply);
            }
            return "discussions/detail";
        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            return "redirect:/discussions";
        }
    }

    @GetMapping("/new")
    public String newQuestionForm(Model model) {
        if (!model.containsAttribute("question")) {
            DiscussionQuestion question = new DiscussionQuestion();
            String currentUser = ownershipService.getCurrentUsername();
            if (currentUser != null) {
                question.setAuthor(currentUser);
            }
            model.addAttribute("question", question);
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
        question.setAuthorUsername(ownershipService.getCurrentUsername());
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
        reply.setAuthorUsername(ownershipService.getCurrentUsername());
        discussionService.addReply(id, reply);
        redirectAttributes.addFlashAttribute("successMessage", "Your advice has been added to the discussion!");
        return "redirect:/discussions/" + id;
    }

    @PostMapping("/{id}/toggle-resolve")
    public String toggleResolve(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        DiscussionQuestion question = discussionService.getQuestionById(id);
        ownershipService.verifyCanModify(question.getAuthorUsername(), "discussion question");

        discussionService.toggleResolved(id);
        redirectAttributes.addFlashAttribute("successMessage", "Discussion status updated.");
        return "redirect:/discussions/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteQuestion(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        DiscussionQuestion question = discussionService.getQuestionById(id);
        ownershipService.verifyCanModify(question.getAuthorUsername(), "discussion question");

        discussionService.deleteQuestion(id);
        redirectAttributes.addFlashAttribute("successMessage", "Discussion question removed successfully.");
        return "redirect:/discussions";
    }

    @PostMapping("/replies/{replyId}/delete")
    public String deleteReply(@PathVariable("replyId") Long replyId,
                              @RequestParam("questionId") Long questionId,
                              RedirectAttributes redirectAttributes) {
        DiscussionReply reply = discussionService.getReplyById(replyId);
        ownershipService.verifyCanModify(reply.getAuthorUsername(), "discussion reply");

        discussionService.deleteReply(replyId);
        redirectAttributes.addFlashAttribute("successMessage", "Reply removed.");
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
