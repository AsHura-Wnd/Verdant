package com.example.service;

import com.example.model.GardeningTip;
import java.util.List;

/**
 * Service contract for managing gardening tips, search queries, and engagement counters.
 */
public interface GardeningTipService {

    /**
     * Retrieves gardening tips optionally filtered by search keyword or category.
     *
     * @param query optional text filter
     * @param category optional category filter
     * @return matching gardening tips ordered by creation timestamp descending
     */
    List<GardeningTip> getAllTips(String query, String category);

    /**
     * Retrieves a single gardening tip by identifier.
     *
     * @param id tip identifier
     * @return the matching gardening tip
     */
    GardeningTip getTipById(Long id);

    /**
     * Persists a new gardening tip.
     *
     * @param tip tip entity to create
     * @return saved tip with generated ID
     */
    GardeningTip saveTip(GardeningTip tip);

    /**
     * Updates mutable fields of an existing gardening tip.
     *
     * @param id tip identifier
     * @param updatedTip tip payload containing revised fields
     * @return persisted updated tip
     */
    GardeningTip updateTip(Long id, GardeningTip updatedTip);

    /**
     * Deletes a gardening tip by identifier.
     *
     * @param id tip identifier
     */
    void deleteTip(Long id);

    /**
     * Atomically increments the likes counter for a gardening tip.
     *
     * @param id tip identifier
     */
    void likeTip(Long id);

    /**
     * Returns top featured gardening tips ranked by like count.
     *
     * @return up to four most-liked tips
     */
    List<GardeningTip> getFeaturedTips();

    /**
     * Returns the most recently published gardening tips.
     *
     * @return up to five recent tips
     */
    List<GardeningTip> getRecentTips();

    /**
     * Counts all gardening tips stored in the platform.
     *
     * @return total tip count
     */
    long countTotalTips();
}
