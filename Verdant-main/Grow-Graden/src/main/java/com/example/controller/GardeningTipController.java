package com.example.controller;

import com.example.exception.ResourceNotFoundException;
import com.example.model.GardeningTip;
import com.example.security.OwnershipService;
import com.example.service.GardeningTipService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Spring MVC controller managing web routes for gardening tips and horticultural articles.
 * Supports browsing, filtering, viewing, creating, liking, and ownership-protected editing/deletion.
 */
@Controller
@RequestMapping("/tips")
public class GardeningTipController {

    private final GardeningTipService tipService;
    private final OwnershipService ownershipService;

    /**
     * Creates a new {@code GardeningTipController}.
     *
     * @param tipService service handling gardening tip persistence and queries
     * @param ownershipService helper enforcing author and admin modification rules
     */
    public GardeningTipController(GardeningTipService tipService, OwnershipService ownershipService) {
        this.tipService = tipService;
        this.ownershipService = ownershipService;
    }

    /**
     * Renders the gardening tips catalog with optional keyword and category filtering.
     *
     * @param search optional keyword filter
     * @param category optional category filter
     * @param model Thymeleaf view model
     * @return the {@code tips/list} template name
     */
    @GetMapping
    public String listTips(@RequestParam(value = "search", required = false) String search,
                           @RequestParam(value = "category", required = false) String category,
                           Model model) {
        model.addAttribute("tips", tipService.getAllTips(search, category));
        model.addAttribute("searchQuery", search);
        model.addAttribute("selectedCategory", category);
        return "tips/list";
    }

    /**
     * Renders the detail view for a single gardening tip.
     *
     * @param id tip identifier
     * @param model Thymeleaf view model
     * @return the {@code tips/detail} template name, or redirect to {@code /tips} if not found
     */
    @GetMapping("/{id}")
    public String viewTip(@PathVariable("id") Long id, Model model) {
        try {
            GardeningTip tip = tipService.getTipById(id);
            model.addAttribute("tip", tip);
            model.addAttribute("recentTips", tipService.getRecentTips());
            model.addAttribute("canModify", ownershipService.canModify(tip.getAuthorUsername()));
            return "tips/detail";
        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            return "redirect:/tips";
        }
    }

    /**
     * Displays the creation form for a new gardening tip, pre-populating the current username.
     *
     * @param model Thymeleaf view model
     * @return the {@code tips/form} template name
     */
    @GetMapping("/new")
    public String newTipForm(Model model) {
        if (!model.containsAttribute("tip")) {
            GardeningTip tip = new GardeningTip();
            String currentUsername = ownershipService.getCurrentUsername();
            if (currentUsername != null) {
                tip.setAuthor(currentUsername);
            }
            model.addAttribute("tip", tip);
        }
        model.addAttribute("isEdit", false);
        return "tips/form";
    }

    /**
     * Validates and persists a newly submitted gardening tip attributed to the signed-in user.
     *
     * @param tip submitted gardening tip payload
     * @param bindingResult validation errors holder
     * @param redirectAttributes flash attributes for post-redirect feedback
     * @param model Thymeleaf view model
     * @return redirect to the created tip detail view, or form view on validation error
     */
    @PostMapping("/new")
    public String createTip(@Valid @ModelAttribute("tip") GardeningTip tip,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "tips/form";
        }
        tip.setAuthorUsername(ownershipService.getCurrentUsername());
        GardeningTip saved = tipService.saveTip(tip);
        redirectAttributes.addFlashAttribute("successMessage", "Gardening tip '" + saved.getTitle() + "' published successfully!");
        return "redirect:/tips/" + saved.getId();
    }

    /**
     * Displays the edit form for an existing gardening tip after verifying ownership.
     *
     * @param id tip identifier
     * @param model Thymeleaf view model
     * @return the {@code tips/form} template name
     */
    @GetMapping("/{id}/edit")
    public String editTipForm(@PathVariable("id") Long id, Model model) {
        GardeningTip tip = tipService.getTipById(id);
        ownershipService.verifyCanModify(tip.getAuthorUsername(), "gardening tip");
        model.addAttribute("tip", tip);
        model.addAttribute("isEdit", true);
        return "tips/form";
    }

    /**
     * Validates and saves edits to an existing gardening tip after verifying ownership.
     *
     * @param id tip identifier
     * @param tip updated gardening tip fields
     * @param bindingResult validation errors holder
     * @param redirectAttributes flash attributes for post-redirect feedback
     * @param model Thymeleaf view model
     * @return redirect to the updated tip detail view, or form view on validation error
     */
    @PostMapping("/{id}/edit")
    public String updateTip(@PathVariable("id") Long id,
                            @Valid @ModelAttribute("tip") GardeningTip tip,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        GardeningTip existing = tipService.getTipById(id);
        ownershipService.verifyCanModify(existing.getAuthorUsername(), "gardening tip");

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "tips/form";
        }
        tip.setAuthorUsername(existing.getAuthorUsername());
        tipService.updateTip(id, tip);
        redirectAttributes.addFlashAttribute("successMessage", "Gardening tip updated successfully!");
        return "redirect:/tips/" + id;
    }

    /**
     * Deletes a gardening tip after verifying ownership or administrator privileges.
     *
     * @param id tip identifier
     * @param redirectAttributes flash attributes for post-redirect feedback
     * @return redirect to {@code /tips}
     */
    @PostMapping("/{id}/delete")
    public String deleteTip(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        GardeningTip tip = tipService.getTipById(id);
        ownershipService.verifyCanModify(tip.getAuthorUsername(), "gardening tip");

        tipService.deleteTip(id);
        redirectAttributes.addFlashAttribute("successMessage", "Gardening tip deleted successfully.");
        return "redirect:/tips";
    }

    /**
     * Atomically increments the helpful likes counter for a gardening tip.
     *
     * @param id tip identifier
     * @param redirectAttributes flash attributes for post-redirect feedback
     * @return redirect to the tip detail view
     */
    @PostMapping("/{id}/like")
    public String likeTip(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        tipService.likeTip(id);
        redirectAttributes.addFlashAttribute("successMessage", "Thank you for supporting this tip!");
        return "redirect:/tips/" + id;
    }
}
