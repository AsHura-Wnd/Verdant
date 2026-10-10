package com.example.repository.jdbc;

/**
 * Value object representing project counts by status retrieved via direct JDBC ResultSet.
 */
public class ProjectStatusMetric {

    private final String status;
    private final long projectCount;

    public ProjectStatusMetric(String status, long projectCount) {
        this.status = status;
        this.projectCount = projectCount;
    }

    public String getStatus() {
        return status;
    }

    public long getProjectCount() {
        return projectCount;
    }
}
