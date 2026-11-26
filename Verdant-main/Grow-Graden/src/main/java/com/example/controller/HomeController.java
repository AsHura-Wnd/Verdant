package com.example.controller;

import com.example.service.DiscussionService;
import com.example.service.GardenProjectService;
import com.example.service.GardeningTipService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Spring MVC controller serving the public dashboard landing page and about page.
 */
@Controller
public class HomeController {

    private final GardeningTipService tipService;
    private final DiscussionService discussionService;
    private final GardenProjectService projectService;

    /**
     * Creates a new {@code HomeController}.
     *
     * @param tipService service for featured tips and totals
     * @param discussionService service for recent questions and totals
     * @param projectService service for recent projects and totals
     */
    public HomeController(GardeningTipService tipService,
                          DiscussionService discussionService,
                          GardenProjectService projectService) {
        this.tipService = tipService;
        this.discussionService = discussionService;
        this.projectService = projectService;
    }

    /**
     * Renders the community landing dashboard with summary metrics and featured items.
     *
     * @param model Thymeleaf view model
     * @return the {@code home} template name
     */
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("totalTips", tipService.countTotalTips());
        model.addAttribute("totalQuestions", discussionService.countTotalQuestions());
        model.addAttribute("resolvedQuestions", discussionService.countResolvedQuestions());
        model.addAttribute("totalProjects", projectService.countTotalProjects());

        model.addAttribute("featuredTips", tipService.getFeaturedTips());
        model.addAttribute("recentQuestions", discussionService.getRecentQuestions());
        model.addAttribute("recentProjects", projectService.getRecentProjects());

        return "home";
    }

    /**
     * Renders the static mission and architecture overview page.
     *
     * @param model Thymeleaf view model
     * @return the {@code about} template name
     */
    @GetMapping("/about")
    public String about(Model model) {
        return "about";
    }
}
