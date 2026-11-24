package com.example.controller;

import com.example.exception.ResourceNotFoundException;
import com.example.model.GardenProject;
import com.example.security.OwnershipService;
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
    private final OwnershipService ownershipService;

    public GardenProjectController(GardenProjectService projectService, OwnershipService ownershipService) {
        this.projectService = projectService;
        this.ownershipService = ownershipService;
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
            model.addAttribute("canModify", ownershipService.canModify(project.getAuthorUsername()));
            return "projects/detail";
        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            return "redirect:/projects";
        }
    }

    @GetMapping("/new")
    public String newProjectForm(Model model) {
        if (!model.containsAttribute("project")) {
            GardenProject project = new GardenProject();
            String currentUser = ownershipService.getCurrentUsername();
            if (currentUser != null) {
                project.setGardenerName(currentUser);
            }
            model.addAttribute("project", project);
        }
        model.addAttribute("isEdit", false);
        return "projects/form";
    }

    @PostMapping("/new")
    public String createProject(@Valid @ModelAttribute("project") GardenProject project,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (project.getStartDate() != null && project.getTargetDate() != null
                && project.getTargetDate().isBefore(project.getStartDate())) {
            bindingResult.rejectValue("targetDate", "error.project", "Target completion date cannot be before start date");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "projects/form";
        }
        project.setAuthorUsername(ownershipService.getCurrentUsername());
        GardenProject saved = projectService.saveProject(project);
        redirectAttributes.addFlashAttribute("successMessage", "Personal gardening project '" + saved.getTitle() + "' created successfully!");
        return "redirect:/projects/" + saved.getId();
    }

    @GetMapping("/{id}/edit")
    public String editProjectForm(@PathVariable("id") Long id, Model model) {
        GardenProject project = projectService.getProjectById(id);
        ownershipService.verifyCanModify(project.getAuthorUsername(), "garden project");

        model.addAttribute("project", project);
        model.addAttribute("isEdit", true);
        return "projects/form";
    }

    @PostMapping("/{id}/edit")
    public String updateProject(@PathVariable("id") Long id,
                                @Valid @ModelAttribute("project") GardenProject project,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        GardenProject existing = projectService.getProjectById(id);
        ownershipService.verifyCanModify(existing.getAuthorUsername(), "garden project");

        if (project.getStartDate() != null && project.getTargetDate() != null
                && project.getTargetDate().isBefore(project.getStartDate())) {
            bindingResult.rejectValue("targetDate", "error.project", "Target completion date cannot be before start date");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "projects/form";
        }
        project.setAuthorUsername(existing.getAuthorUsername());
        projectService.updateProject(id, project);
        redirectAttributes.addFlashAttribute("successMessage", "Gardening project updated successfully!");
        return "redirect:/projects/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteProject(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        GardenProject project = projectService.getProjectById(id);
        ownershipService.verifyCanModify(project.getAuthorUsername(), "garden project");

        projectService.deleteProject(id);
        redirectAttributes.addFlashAttribute("successMessage", "Gardening project deleted successfully.");
        return "redirect:/projects";
    }
}
