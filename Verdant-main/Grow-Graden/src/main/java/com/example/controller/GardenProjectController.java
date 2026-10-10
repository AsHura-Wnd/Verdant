package com.example.controller;

import com.example.exception.ResourceNotFoundException;
import com.example.model.GardenProject;
import com.example.service.GardenProjectService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/projects")
public class GardenProjectController {

    private final GardenProjectService projectService;

    public GardenProjectController(GardenProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public String listProjects(@RequestParam(value = "search", required = false) String search,
                               @RequestParam(value = "status", required = false) String status,
                               Model model) {
        model.addAttribute("projects", projectService.getAllProjects(search, status));
        model.addAttribute("searchQuery", search);
        model.addAttribute("selectedStatus", status);

        model.addAttribute("inProgressCount", projectService.countProjectsByStatus("In Progress"));
        model.addAttribute("flourishingCount", projectService.countProjectsByStatus("Flourishing"));
        model.addAttribute("completedCount", projectService.countProjectsByStatus("Completed"));
        return "projects/list";
    }

    @GetMapping("/{id}")
    public String viewProject(@PathVariable("id") Long id, Model model) {
        try {
            GardenProject project = projectService.getProjectById(id);
            model.addAttribute("project", project);
            return "projects/detail";
        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            return "redirect:/projects";
        }
    }

    @GetMapping("/new")
    public String newProjectForm(Model model) {
        if (!model.containsAttribute("project")) {
            model.addAttribute("project", new GardenProject());
        }
        model.addAttribute("isEdit", false);
        return "projects/form";
    }

    @PostMapping("/new")
    public String createProject(@Valid @ModelAttribute("project") GardenProject project,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "projects/form";
        }
        GardenProject saved = projectService.saveProject(project);
        redirectAttributes.addFlashAttribute("successMessage", "Personal gardening project '" + saved.getTitle() + "' created successfully!");
        return "redirect:/projects/" + saved.getId();
    }

    @GetMapping("/{id}/edit")
    public String editProjectForm(@PathVariable("id") Long id, Model model) {
        try {
            GardenProject project = projectService.getProjectById(id);
            model.addAttribute("project", project);
            model.addAttribute("isEdit", true);
            return "projects/form";
        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            return "redirect:/projects";
        }
    }

    @PostMapping("/{id}/edit")
    public String updateProject(@PathVariable("id") Long id,
                                @Valid @ModelAttribute("project") GardenProject project,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "projects/form";
        }
        projectService.updateProject(id, project);
        redirectAttributes.addFlashAttribute("successMessage", "Gardening project updated successfully!");
        return "redirect:/projects/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteProject(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            projectService.deleteProject(id);
            redirectAttributes.addFlashAttribute("successMessage", "Gardening project deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete project: " + e.getMessage());
        }
        return "redirect:/projects";
    }
}
