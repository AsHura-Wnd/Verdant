package com.example.repository.jdbc;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Direct JDBC Data Access Object for GrowGarden Platform.
 * Demonstrates academic compliance for direct JDBC APIs:
 * - java.sql.Connection
 * - java.sql.PreparedStatement (parameterized queries avoiding SQL injection)
 * - java.sql.ResultSet (manual cursor traversal and type extraction)
 * - Java 7+ try-with-resources resource management preventing database connection leaks
 */
@Repository
public class GardenJdbcDao {

    private final DataSource dataSource;

    public GardenJdbcDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Executes aggregate parameterized SQL query to compute category-level metrics.
     * Uses Connection, PreparedStatement, and ResultSet.
     */
    public List<CategoryMetric> getCategoryMetrics() {
        String sql = "SELECT category, COUNT(*) AS tip_count, " +
                     "COALESCE(SUM(likes_count), 0) AS total_likes, " +
                     "COALESCE(AVG(likes_count), 0.0) AS avg_likes " +
                     "FROM gardening_tips " +
                     "GROUP BY category " +
                     "ORDER BY total_likes DESC";

        List<CategoryMetric> results = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String category = rs.getString("category");
                long tipCount = rs.getLong("tip_count");
                long totalLikes = rs.getLong("total_likes");
                double avgLikes = rs.getDouble("avg_likes");

                results.add(new CategoryMetric(category, tipCount, totalLikes, avgLikes));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing direct JDBC category metrics query: " + e.getMessage(), e);
        }

        return results;
    }

    /**
     * Searches gardening tips using parameterized SQL with LIKE wildcards and optional filter parameters.
     * Demonstrates safe parameterized query construction with PreparedStatement.
     */
    public List<JdbcTipSummary> searchTipsJdbc(String keyword, String difficulty) {
        StringBuilder sql = new StringBuilder(
                "SELECT id, title, category, summary, author, difficulty_level, plant_type, likes_count, created_at " +
                "FROM gardening_tips WHERE 1=1 "
        );

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasDifficulty = difficulty != null && !difficulty.trim().isEmpty() && !difficulty.equalsIgnoreCase("ALL");

        if (hasKeyword) {
            sql.append("AND (LOWER(title) LIKE ? OR LOWER(summary) LIKE ? OR LOWER(plant_type) LIKE ?) ");
        }
        if (hasDifficulty) {
            sql.append("AND LOWER(difficulty_level) = ? ");
        }
        sql.append("ORDER BY likes_count DESC");

        List<JdbcTipSummary> results = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (hasKeyword && keyword != null) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                pstmt.setString(paramIndex++, pattern);
                pstmt.setString(paramIndex++, pattern);
                pstmt.setString(paramIndex++, pattern);
            }
            if (hasDifficulty && difficulty != null) {
                pstmt.setString(paramIndex++, difficulty.trim().toLowerCase());
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Long id = rs.getLong("id");
                    String title = rs.getString("title");
                    String category = rs.getString("category");
                    String summary = rs.getString("summary");
                    String author = rs.getString("author");
                    String difficultyLevel = rs.getString("difficulty_level");
                    String plantType = rs.getString("plant_type");
                    int likesCount = rs.getInt("likes_count");
                    
                    Timestamp ts = rs.getTimestamp("created_at");
                    LocalDateTime createdAt = (ts != null) ? ts.toLocalDateTime() : null;

                    results.add(new JdbcTipSummary(id, title, category, summary, author,
                            difficultyLevel, plantType, likesCount, createdAt));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing direct JDBC tip search: " + e.getMessage(), e);
        }

        return results;
    }

    /**
     * Aggregates personal garden projects by status using direct JDBC.
     */
    public List<ProjectStatusMetric> getProjectStatusMetrics() {
        String sql = "SELECT status, COUNT(*) AS project_count " +
                     "FROM gardening_projects " +
                     "GROUP BY status " +
                     "ORDER BY project_count DESC";

        List<ProjectStatusMetric> results = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String status = rs.getString("status");
                long count = rs.getLong("project_count");
                results.add(new ProjectStatusMetric(status, count));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing direct JDBC project status query: " + e.getMessage(), e);
        }

        return results;
    }

    /**
     * Returns direct scalar count of all tips in the database via JDBC.
     */
    public long getTipCountDirect() {
        String sql = "SELECT COUNT(*) FROM gardening_tips";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error executing direct JDBC tip count query: " + e.getMessage(), e);
        }
    }
}
