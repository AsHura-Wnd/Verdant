package com.example.controller;

import com.example.service.DiscussionService;
import com.example.service.GardenProjectService;
import com.example.service.GardeningTipService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final GardeningTipService tipService;
    private final DiscussionService discussionService;
    private final GardenProjectService projectService;

    public HomeController(GardeningTipService tipService,
                          DiscussionService discussionService,
                          GardenProjectService projectService) {
        this.tipService = tipService;
        this.discussionService = discussionService;
        this.projectService = projectService;
    }

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

    @GetMapping("/about")
    public String about(Model model) {
        return "about";
    }
}
