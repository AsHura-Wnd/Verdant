package com.example.controller;

import com.example.repository.jdbc.CategoryMetric;
import com.example.repository.jdbc.GardenJdbcDao;
import com.example.repository.jdbc.JdbcTipSummary;
import com.example.repository.jdbc.ProjectStatusMetric;
import com.example.util.SearchResultWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller exposing direct JDBC analytical metrics and parameterized tip searches.
 * Delegates SQL execution to {@link GardenJdbcDao} and wraps search results in
 * {@link SearchResultWrapper}.
 */
@RestController
@RequestMapping("/api/jdbc")
public class JdbcAnalyticsRestController {

    private final GardenJdbcDao gardenJdbcDao;

    /**
     * Creates a new {@code JdbcAnalyticsRestController}.
     *
     * @param gardenJdbcDao direct JDBC data access object
     */
    public JdbcAnalyticsRestController(GardenJdbcDao gardenJdbcDao) {
        this.gardenJdbcDao = gardenJdbcDao;
    }

    /**
     * Computes aggregate category and project status metrics using direct JDBC queries.
     *
     * @return JSON payload containing direct tip counts and aggregate metrics
     */
    @GetMapping("/metrics")
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

    /**
     * Searches gardening tips via parameterized JDBC queries wrapped in a generic container.
     *
     * @param keyword optional text filter matched against title, summary, or plant type
     * @param difficulty optional difficulty level filter
     * @return wrapped search results with match metadata
     */
    @GetMapping("/tips/search")
    public ResponseEntity<SearchResultWrapper<JdbcTipSummary>> searchTipsJdbc(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "difficulty", required = false) String difficulty) {

        List<JdbcTipSummary> rawResults = gardenJdbcDao.searchTipsJdbc(keyword, difficulty);
        SearchResultWrapper<JdbcTipSummary> wrapped = SearchResultWrapper.of(rawResults, keyword);
        return ResponseEntity.ok(wrapped);
    }
}
