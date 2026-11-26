package com.example.repository;

import com.example.model.DiscussionQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link DiscussionQuestion} queries and atomic view counting.
 */
@Repository
public interface DiscussionQuestionRepository extends JpaRepository<DiscussionQuestion, Long> {

    /**
     * Retrieves all discussion questions ordered newest first.
     *
     * @return ordered list of questions
     */
    List<DiscussionQuestion> findAllByOrderByCreatedAtDesc();

    /**
     * Retrieves discussion questions in a given category ordered newest first.
     *
     * @param category category label (case-insensitive)
     * @return matching questions
     */
    List<DiscussionQuestion> findByCategoryIgnoreCaseOrderByCreatedAtDesc(String category);

    /**
     * Retrieves discussion questions matching a specific resolution status.
     *
     * @param resolved {@code true} for solved threads, {@code false} for open threads
     * @return matching questions ordered newest first
     */
    List<DiscussionQuestion> findByResolvedOrderByCreatedAtDesc(boolean resolved);

    /**
     * Searches discussion questions by keyword across title, description, or plant type.
     *
     * @param query search substring
     * @return matching questions ordered newest first
     */
    @Query("SELECT q FROM DiscussionQuestion q WHERE " +
           "LOWER(q.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(q.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(q.plantType) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY q.createdAt DESC")
    List<DiscussionQuestion> searchQuestions(@Param("query") String query);

    /**
     * Retrieves the five most recently posted questions.
     *
     * @return recent discussion questions
     */
    List<DiscussionQuestion> findTop5ByOrderByCreatedAtDesc();

    /**
     * Counts questions matching the specified resolution flag.
     *
     * @param resolved resolution flag
     * @return count of matching questions
     */
    long countByResolved(boolean resolved);

    /**
     * Atomically increments {@code viewsCount} at the database layer without version contention.
     *
     * @param id question identifier
     * @return number of updated rows (1 if found, 0 otherwise)
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE DiscussionQuestion q SET q.viewsCount = q.viewsCount + 1 WHERE q.id = :id")
    int incrementViewsCount(@Param("id") Long id);
}
