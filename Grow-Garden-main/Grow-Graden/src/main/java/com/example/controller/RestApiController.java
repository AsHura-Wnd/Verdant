package com.example.controller;

import com.example.domain.advisor.AbstractPlantAdvisor;
import com.example.domain.advisor.CarePlan;
import com.example.domain.advisor.DiagnosticResult;
import com.example.domain.advisor.PlantAdvisorRegistry;
import com.example.model.DiscussionQuestion;
import com.example.model.GardenProject;
import com.example.model.GardeningTip;
import com.example.repository.jdbc.CategoryMetric;
import com.example.repository.jdbc.GardenJdbcDao;
import com.example.repository.jdbc.JdbcTipSummary;
import com.example.repository.jdbc.ProjectStatusMetric;
import com.example.service.DiscussionService;
import com.example.service.GardenProjectService;
import com.example.service.GardeningTipService;
import com.example.util.SearchResultWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API Controller demonstrating:
 * - Direct JDBC Data Access via GardenJdbcDao (PreparedStatement, Connection, ResultSet)
 * - Polymorphic Domain Dispatch via PlantAdvisorRegistry and AbstractPlantAdvisor
 * - Student-written Generics via SearchResultWrapper<T>
 * - Standard Spring MVC REST endpoints with ResponseEntity
 */
@RestController
@RequestMapping("/api")
public class RestApiController {

    private final GardeningTipService tipService;
    private final DiscussionService discussionService;
    private final GardenProjectService projectService;
    private final GardenJdbcDao gardenJdbcDao;
    private final PlantAdvisorRegistry advisorRegistry;

    public RestApiController(GardeningTipService tipService,
                             DiscussionService discussionService,
                             GardenProjectService projectService,
                             GardenJdbcDao gardenJdbcDao,
                             PlantAdvisorRegistry advisorRegistry) {
        this.tipService = tipService;
        this.discussionService = discussionService;
        this.projectService = projectService;
        this.gardenJdbcDao = gardenJdbcDao;
        this.advisorRegistry = advisorRegistry;
    }

    // ==========================================
    // 0. Platform Health Check Endpoint
    // ==========================================

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("application", "Verdant");
        health.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(health);
    }

    // ==========================================
    // 1. Direct JDBC Endpoints
    // ==========================================

    @GetMapping("/jdbc/metrics")
    public ResponseEntity<Map<String, Object>> getJdbcMetrics() {
        List<CategoryMetric> categoryMetrics = gardenJdbcDao.getCategoryMetrics();
        List<ProjectStatusMetric> projectMetrics = gardenJdbcDao.getProjectStatusMetrics();
        long directTipCount = gardenJdbcDao.getTipCountDirect();

        Map<String, Object> response = new HashMap<>();
        response.put("provider", "Direct JDBC via java.sql.PreparedStatement & Connection");
        response.put("directTipCount", directTipCount);
        response.put("categoryAnalytics", categoryMetrics);
        response.put("projectStatusBreakdown", projectMetrics);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/jdbc/tips/search")
    public ResponseEntity<SearchResultWrapper<JdbcTipSummary>> searchTipsJdbc(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "difficulty", required = false) String difficulty) {

        List<JdbcTipSummary> rawResults = gardenJdbcDao.searchTipsJdbc(keyword, difficulty);
        SearchResultWrapper<JdbcTipSummary> wrapped = SearchResultWrapper.of(rawResults, keyword);

        return ResponseEntity.ok(wrapped);
    }

    // ==========================================
    // 2. Polymorphic Advisory Engine Endpoints
    // ==========================================

    @GetMapping("/advisor/diagnose")
    public ResponseEntity<DiagnosticResult> diagnosePlant(
            @RequestParam("plantType") String plantType,
            @RequestParam("symptom") String symptom) {

        // Polymorphic dynamic method dispatch: resolveAdvisor returns an AbstractPlantAdvisor subclass
        AbstractPlantAdvisor advisor = advisorRegistry.resolveAdvisor(plantType);
        DiagnosticResult diagnosis = advisor.diagnoseSymptom(symptom);

        return ResponseEntity.ok(diagnosis);
    }

    @GetMapping("/advisor/care-plan")
    public ResponseEntity<CarePlan> generateCarePlan(
            @RequestParam("plantType") String plantType,
            @RequestParam(value = "spaceType", defaultValue = "Balcony") String spaceType) {

        AbstractPlantAdvisor advisor = advisorRegistry.resolveAdvisor(plantType);
        CarePlan plan = advisor.generateCarePlan(spaceType);

        return ResponseEntity.ok(plan);
    }

    // ==========================================
    // 3. Existing JPA / MVC REST Endpoints
    // ==========================================

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
        return ResponseEntity.ok(tipService.getTipById(id));
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
        return ResponseEntity.ok(discussionService.getQuestionById(id));
    }

    @GetMapping("/projects")
    public ResponseEntity<List<GardenProject>> getAllProjects(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status) {
        return ResponseEntity.ok(projectService.getAllProjects(search, status));
    }

    @GetMapping("/projects/{id}")
    public ResponseEntity<GardenProject> getProjectById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }
}
