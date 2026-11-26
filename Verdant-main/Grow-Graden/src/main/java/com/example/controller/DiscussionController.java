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

/**
 * Spring MVC controller managing community plant clinic discussions and replies.
 * Handles listing, question creation, reply posting, resolution toggles, upvoting,
 * and ownership-protected deletion.
 */
@Controller
@RequestMapping("/discussions")
public class DiscussionController {

    private final DiscussionService discussionService;
    private final OwnershipService ownershipService;

    /**
     * Creates a new {@code DiscussionController}.
     *
     * @param discussionService service managing discussion questions and replies
     * @param ownershipService helper enforcing author and admin modification rules
     */
    public DiscussionController(DiscussionService discussionService, OwnershipService ownershipService) {
        this.discussionService = discussionService;
        this.ownershipService = ownershipService;
    }

    /**
     * Displays community discussion questions filtered by keyword, category, or resolution state.
     *
     * @param search optional search keyword
     * @param category optional category filter
     * @param resolved optional resolution status filter
     * @param model Thymeleaf view model
     * @return the {@code discussions/list} template name
     */
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

    /**
     * Displays a discussion question thread with replies and increments its view count.
     *
     * @param id question identifier
     * @param model Thymeleaf view model
     * @return the {@code discussions/detail} template name, or redirect if not found
     */
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

    /**
     * Displays the form for posting a new plant clinic question.
     *
     * @param model Thymeleaf view model
     * @return the {@code discussions/form} template name
     */
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

    /**
     * Validates and persists a new discussion question attributed to the authenticated user.
     *
     * @param question submitted question entity
     * @param bindingResult validation errors holder
     * @param redirectAttributes flash attributes for post-redirect feedback
     * @param model Thymeleaf view model
     * @return redirect to the new thread, or form view on validation errors
     */
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

    /**
     * Appends a validated community reply to an existing discussion question.
     *
     * @param id parent question identifier
     * @param reply submitted reply payload
     * @param bindingResult validation errors holder
     * @param redirectAttributes flash attributes for post-redirect feedback
     * @return redirect to the parent discussion detail view
     */
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

    /**
     * Toggles the resolved status of a discussion question after verifying ownership.
     *
     * @param id question identifier
     * @param redirectAttributes flash attributes for post-redirect feedback
     * @return redirect to the discussion detail view
     */
    @PostMapping("/{id}/toggle-resolve")
    public String toggleResolve(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        DiscussionQuestion question = discussionService.getQuestionById(id);
        ownershipService.verifyCanModify(question.getAuthorUsername(), "discussion question");

        discussionService.toggleResolved(id);
        redirectAttributes.addFlashAttribute("successMessage", "Discussion status updated.");
        return "redirect:/discussions/" + id;
    }

    /**
     * Deletes a discussion question after verifying ownership or administrator privileges.
     *
     * @param id question identifier
     * @param redirectAttributes flash attributes for post-redirect feedback
     * @return redirect to {@code /discussions}
     */
    @PostMapping("/{id}/delete")
    public String deleteQuestion(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        DiscussionQuestion question = discussionService.getQuestionById(id);
        ownershipService.verifyCanModify(question.getAuthorUsername(), "discussion question");

        discussionService.deleteQuestion(id);
        redirectAttributes.addFlashAttribute("successMessage", "Discussion question removed successfully.");
        return "redirect:/discussions";
    }

    /**
     * Deletes a specific reply after verifying reply authorship or administrator privileges.
     *
     * @param replyId reply identifier
     * @param questionId parent question identifier for redirect
     * @param redirectAttributes flash attributes for post-redirect feedback
     * @return redirect to the parent discussion detail view
     */
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

    /**
     * Atomically increments the upvote count for a helpful reply.
     *
     * @param replyId reply identifier
     * @param questionId parent question identifier for redirect
     * @param redirectAttributes flash attributes for post-redirect feedback
     * @return redirect to the parent discussion detail view
     */
    @PostMapping("/replies/{replyId}/upvote")
    public String upvoteReply(@PathVariable("replyId") Long replyId,
                              @RequestParam("questionId") Long questionId,
                              RedirectAttributes redirectAttributes) {
        discussionService.upvoteReply(replyId);
        redirectAttributes.addFlashAttribute("successMessage", "Upvoted advice as helpful!");
        return "redirect:/discussions/" + questionId;
    }
}
