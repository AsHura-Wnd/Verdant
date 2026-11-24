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

@Controller
@RequestMapping("/tips")
public class GardeningTipController {

    private final GardeningTipService tipService;
    private final OwnershipService ownershipService;

    public GardeningTipController(GardeningTipService tipService, OwnershipService ownershipService) {
        this.tipService = tipService;
        this.ownershipService = ownershipService;
    }

    @GetMapping
    public String listTips(@RequestParam(value = "search", required = false) String search,
                           @RequestParam(value = "category", required = false) String category,
                           Model model) {
        model.addAttribute("tips", tipService.getAllTips(search, category));
        model.addAttribute("searchQuery", search);
        model.addAttribute("selectedCategory", category);
        return "tips/list";
    }

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

    @GetMapping("/{id}/edit")
    public String editTipForm(@PathVariable("id") Long id, Model model) {
        GardeningTip tip = tipService.getTipById(id);
        ownershipService.verifyCanModify(tip.getAuthorUsername(), "gardening tip");
        model.addAttribute("tip", tip);
        model.addAttribute("isEdit", true);
        return "tips/form";
    }

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

    @PostMapping("/{id}/delete")
    public String deleteTip(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        GardeningTip tip = tipService.getTipById(id);
        ownershipService.verifyCanModify(tip.getAuthorUsername(), "gardening tip");

        tipService.deleteTip(id);
        redirectAttributes.addFlashAttribute("successMessage", "Gardening tip deleted successfully.");
        return "redirect:/tips";
    }

    @PostMapping("/{id}/like")
    public String likeTip(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        tipService.likeTip(id);
        redirectAttributes.addFlashAttribute("successMessage", "Thank you for supporting this tip!");
        return "redirect:/tips/" + id;
    }
}
