package com.example.repository;

import com.example.model.GardenProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link GardenProject} persistence and status queries.
 */
@Repository
public interface GardenProjectRepository extends JpaRepository<GardenProject, Long> {

    /**
     * Retrieves all garden projects ordered newest first.
     *
     * @return ordered list of garden projects
     */
    List<GardenProject> findAllByOrderByCreatedAtDesc();

    /**
     * Retrieves garden projects matching a lifecycle status ordered newest first.
     *
     * @param status lifecycle status (case-insensitive)
     * @return matching projects
     */
    List<GardenProject> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);

    /**
     * Searches projects by keyword across title, description, plant type, or location.
     *
     * @param query search substring
     * @return matching garden projects ordered newest first
     */
    @Query("SELECT p FROM GardenProject p WHERE " +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.plantType) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.location) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY p.createdAt DESC")
    List<GardenProject> searchProjects(@Param("query") String query);

    /**
     * Retrieves the four most recently created garden projects.
     *
     * @return recent garden projects
     */
    List<GardenProject> findTop4ByOrderByCreatedAtDesc();

    /**
     * Counts garden projects matching a lifecycle status label.
     *
     * @param status status label (case-insensitive)
     * @return count of matching projects
     */
    long countByStatusIgnoreCase(String status);
}
