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

/**
 * Spring MVC controller managing personal garden project journals and milestones.
 * Enforces date-order validation and ownership checks on edits and deletions.
 */
@Controller
@RequestMapping("/projects")
public class GardenProjectController {

    private final GardenProjectService projectService;
    private final OwnershipService ownershipService;

    /**
     * Creates a new {@code GardenProjectController}.
     *
     * @param projectService service handling garden project persistence and queries
     * @param ownershipService helper enforcing author and admin modification rules
     */
    public GardenProjectController(GardenProjectService projectService, OwnershipService ownershipService) {
        this.projectService = projectService;
        this.ownershipService = ownershipService;
    }

    /**
     * Displays personal garden projects with optional search, status filtering, and summary counts.
     *
     * @param search optional search keyword
     * @param status optional project status filter
     * @param model Thymeleaf view model
     * @return the {@code projects/list} template name
     */
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

    /**
     * Displays detailed progress notes and metadata for a single garden project.
     *
     * @param id project identifier
     * @param model Thymeleaf view model
     * @return the {@code projects/detail} template name, or redirect to {@code /projects} if missing
     */
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

    /**
     * Displays the creation form for a new garden project.
     *
     * @param model Thymeleaf view model
     * @return the {@code projects/form} template name
     */
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

    /**
     * Validates and saves a new garden project attributed to the authenticated user.
     *
     * @param project submitted garden project payload
     * @param bindingResult validation errors holder
     * @param redirectAttributes flash attributes for post-redirect feedback
     * @param model Thymeleaf view model
     * @return redirect to the created project detail view, or form view on validation error
     */
    @PostMapping("/new")
    public String createProject(@Valid @ModelAttribute("project") GardenProject project,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        validateProjectDates(project, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "projects/form";
        }
        project.setAuthorUsername(ownershipService.getCurrentUsername());
        GardenProject saved = projectService.saveProject(project);
        redirectAttributes.addFlashAttribute("successMessage", "Personal gardening project '" + saved.getTitle() + "' created successfully!");
        return "redirect:/projects/" + saved.getId();
    }

    /**
     * Displays the edit form for an existing garden project after verifying ownership.
     *
     * @param id project identifier
     * @param model Thymeleaf view model
     * @return the {@code projects/form} template name
     */
    @GetMapping("/{id}/edit")
    public String editProjectForm(@PathVariable("id") Long id, Model model) {
        GardenProject project = projectService.getProjectById(id);
        ownershipService.verifyCanModify(project.getAuthorUsername(), "garden project");

        model.addAttribute("project", project);
        model.addAttribute("isEdit", true);
        return "projects/form";
    }

    /**
     * Validates and saves updates to an existing garden project after verifying ownership.
     *
     * @param id project identifier
     * @param project updated garden project fields
     * @param bindingResult validation errors holder
     * @param redirectAttributes flash attributes for post-redirect feedback
     * @param model Thymeleaf view model
     * @return redirect to the project detail view, or form view on validation error
     */
    @PostMapping("/{id}/edit")
    public String updateProject(@PathVariable("id") Long id,
                                @Valid @ModelAttribute("project") GardenProject project,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        GardenProject existing = projectService.getProjectById(id);
        ownershipService.verifyCanModify(existing.getAuthorUsername(), "garden project");

        validateProjectDates(project, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "projects/form";
        }
        project.setAuthorUsername(existing.getAuthorUsername());
        projectService.updateProject(id, project);
        redirectAttributes.addFlashAttribute("successMessage", "Gardening project updated successfully!");
        return "redirect:/projects/" + id;
    }

    /**
     * Deletes a garden project after verifying ownership or administrator privileges.
     *
     * @param id project identifier
     * @param redirectAttributes flash attributes for post-redirect feedback
     * @return redirect to {@code /projects}
     */
    @PostMapping("/{id}/delete")
    public String deleteProject(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        GardenProject project = projectService.getProjectById(id);
        ownershipService.verifyCanModify(project.getAuthorUsername(), "garden project");

        projectService.deleteProject(id);
        redirectAttributes.addFlashAttribute("successMessage", "Gardening project deleted successfully.");
        return "redirect:/projects";
    }

    private void validateProjectDates(GardenProject project, BindingResult bindingResult) {
        if (project.getStartDate() != null && project.getTargetDate() != null
                && project.getTargetDate().isBefore(project.getStartDate())) {
            bindingResult.rejectValue("targetDate", "error.project", "Target completion date cannot be before start date");
        }
    }
}
