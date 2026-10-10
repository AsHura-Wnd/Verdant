package com.example.repository.jdbc;

/**
 * Value object representing aggregated category analytics retrieved via direct JDBC ResultSet.
 */
public class CategoryMetric {

    private final String category;
    private final long tipCount;
    private final long totalLikes;
    private final double averageLikes;

    public CategoryMetric(String category, long tipCount, long totalLikes, double averageLikes) {
        this.category = category;
        this.tipCount = tipCount;
        this.totalLikes = totalLikes;
        this.averageLikes = averageLikes;
    }

    public String getCategory() {
        return category;
    }

    public long getTipCount() {
        return tipCount;
    }

    public long getTotalLikes() {
        return totalLikes;
    }

    public double getAverageLikes() {
        return averageLikes;
    }
}
