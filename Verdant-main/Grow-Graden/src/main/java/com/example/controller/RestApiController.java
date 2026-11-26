package com.example.controller;

import com.example.model.DiscussionQuestion;
import com.example.model.GardenProject;
import com.example.model.GardeningTip;
import com.example.service.DiscussionService;
import com.example.service.GardenProjectService;
import com.example.service.GardeningTipService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Core REST API controller providing health checks, platform statistics,
 * and read access to gardening tips, discussions, and personal garden projects.
 * JDBC analytics and plant advisory endpoints are separated into
 * {@link JdbcAnalyticsRestController} and {@link PlantAdvisorRestController}.
 */
@RestController
@RequestMapping("/api")
public class RestApiController {

    private final GardeningTipService tipService;
    private final DiscussionService discussionService;
    private final GardenProjectService projectService;

    /**
     * Creates a new {@code RestApiController}.
     *
     * @param tipService service for gardening tips
     * @param discussionService service for community discussions
     * @param projectService service for garden projects
     */
    public RestApiController(GardeningTipService tipService,
                             DiscussionService discussionService,
                             GardenProjectService projectService) {
        this.tipService = tipService;
        this.discussionService = discussionService;
        this.projectService = projectService;
    }

    /**
     * Lightweight platform health-check endpoint for container and cloud probes.
     *
     * @return status map indicating application readiness
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("application", "Verdant");
        health.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(health);
    }

    /**
     * Returns aggregate counts across tips, discussions, resolved cases, and projects.
     *
     * @return summary statistics map
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTips", tipService.countTotalTips());
        stats.put("totalDiscussions", discussionService.countTotalQuestions());
        stats.put("resolvedDiscussions", discussionService.countResolvedQuestions());
        stats.put("totalProjects", projectService.countTotalProjects());
        return ResponseEntity.ok(stats);
    }

    /**
     * Lists gardening tips with optional keyword search or category filter.
     *
     * @param search optional search keyword
     * @param category optional category filter
     * @return matching gardening tips
     */
    @GetMapping("/tips")
    public ResponseEntity<List<GardeningTip>> getAllTips(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "category", required = false) String category) {
        return ResponseEntity.ok(tipService.getAllTips(search, category));
    }

    /**
     * Retrieves a single gardening tip by ID.
     *
     * @param id tip identifier
     * @return matching gardening tip
     */
    @GetMapping("/tips/{id}")
    public ResponseEntity<GardeningTip> getTipById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(tipService.getTipById(id));
    }

    /**
     * Lists community discussion questions with optional search, category, and resolved filters.
     *
     * @param search optional search keyword
     * @param category optional category filter
     * @param resolved optional resolution status filter
     * @return matching discussion questions
     */
    @GetMapping("/discussions")
    public ResponseEntity<List<DiscussionQuestion>> getAllDiscussions(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "resolved", required = false) Boolean resolved) {
        return ResponseEntity.ok(discussionService.getAllQuestions(search, category, resolved));
    }

    /**
     * Retrieves a single discussion question by ID and atomically increments its view counter.
     *
     * @param id discussion question identifier
     * @return matching discussion question
     */
    @GetMapping("/discussions/{id}")
    public ResponseEntity<DiscussionQuestion> getDiscussionById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(discussionService.getQuestionById(id));
    }

    /**
     * Lists garden projects with optional keyword search or status filter.
     *
     * @param search optional search keyword
     * @param status optional project status filter
     * @return matching garden projects
     */
    @GetMapping("/projects")
    public ResponseEntity<List<GardenProject>> getAllProjects(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status) {
        return ResponseEntity.ok(projectService.getAllProjects(search, status));
    }

    /**
     * Retrieves a single garden project by ID.
     *
     * @param id garden project identifier
     * @return matching garden project
     */
    @GetMapping("/projects/{id}")
    public ResponseEntity<GardenProject> getProjectById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }
}
