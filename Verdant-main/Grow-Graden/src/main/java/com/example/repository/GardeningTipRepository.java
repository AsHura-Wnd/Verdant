package com.example.repository;

import com.example.model.GardeningTip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link GardeningTip} persistence, search, and atomic likes updates.
 */
@Repository
public interface GardeningTipRepository extends JpaRepository<GardeningTip, Long> {

    /**
     * Retrieves all gardening tips ordered newest first.
     *
     * @return ordered list of gardening tips
     */
    List<GardeningTip> findAllByOrderByCreatedAtDesc();

    /**
     * Retrieves gardening tips belonging to the specified category ordered newest first.
     *
     * @param category category name (case-insensitive)
     * @return matching gardening tips
     */
    List<GardeningTip> findByCategoryIgnoreCaseOrderByCreatedAtDesc(String category);

    /**
     * Searches tips matching a keyword across title, summary, content, or plant type.
     *
     * @param query search substring
     * @return matching gardening tips ordered newest first
     */
    @Query("SELECT t FROM GardeningTip t WHERE " +
           "LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(t.summary) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(t.content) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(t.plantType) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY t.createdAt DESC")
    List<GardeningTip> searchTips(@Param("query") String query);

    /**
     * Retrieves the top four most-liked tips.
     *
     * @return featured gardening tips
     */
    List<GardeningTip> findTop4ByOrderByLikesCountDesc();

    /**
     * Retrieves the five most recently published tips.
     *
     * @return recent gardening tips
     */
    List<GardeningTip> findTop5ByOrderByCreatedAtDesc();

    /**
     * Counts tips within a given category.
     *
     * @param category category name (case-insensitive)
     * @return count of matching tips
     */
    long countByCategoryIgnoreCase(String category);

    /**
     * Atomically increments {@code likesCount} at the database layer to prevent lost updates.
     *
     * @param id tip identifier
     * @return number of updated rows (1 if found, 0 otherwise)
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE GardeningTip t SET t.likesCount = t.likesCount + 1 WHERE t.id = :id")
    int incrementLikesCount(@Param("id") Long id);
}
