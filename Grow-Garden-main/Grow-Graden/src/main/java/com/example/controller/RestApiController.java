package com.example.controller;

import com.example.model.DiscussionQuestion;
import com.example.model.GardenProject;
import com.example.model.GardeningTip;
import com.example.service.DiscussionService;
import com.example.service.GardenProjectService;
import com.example.service.GardeningTipService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RestApiController {

    private final GardeningTipService tipService;
    private final DiscussionService discussionService;
    private final GardenProjectService projectService;

    public RestApiController(GardeningTipService tipService,
                             DiscussionService discussionService,
                             GardenProjectService projectService) {
        this.tipService = tipService;
        this.discussionService = discussionService;
        this.projectService = projectService;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTips", tipService.countTotalTips());
        stats.put("totalDiscussions", discussionService.countTotalQuestions());
        stats.put("resolvedDiscussions", discussionService.countResolvedQuestions());
        stats.put("totalProjects", projectService.countTotalProjects());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/tips")
    public ResponseEntity<List<GardeningTip>> getAllTips(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "category", required = false) String category) {
        return ResponseEntity.ok(tipService.getAllTips(search, category));
    }

    @GetMapping("/tips/{id}")
    public ResponseEntity<GardeningTip> getTipById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(tipService.getTipById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/discussions")
    public ResponseEntity<List<DiscussionQuestion>> getAllDiscussions(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "resolved", required = false) Boolean resolved) {
        return ResponseEntity.ok(discussionService.getAllQuestions(search, category, resolved));
    }

    @GetMapping("/discussions/{id}")
    public ResponseEntity<DiscussionQuestion> getDiscussionById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(discussionService.getQuestionById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/projects")
    public ResponseEntity<List<GardenProject>> getAllProjects(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status) {
        return ResponseEntity.ok(projectService.getAllProjects(search, status));
    }

    @GetMapping("/projects/{id}")
    public ResponseEntity<GardenProject> getProjectById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(projectService.getProjectById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
