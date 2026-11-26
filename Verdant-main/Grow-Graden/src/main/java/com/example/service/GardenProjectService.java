package com.example.service;

import com.example.model.GardenProject;
import java.util.List;

/**
 * Service contract for managing personal garden project journals and status analytics.
 */
public interface GardenProjectService {

    /**
     * Lists garden projects optionally filtered by search keyword or status.
     *
     * @param query optional text filter
     * @param status optional project status filter
     * @return matching garden projects
     */
    List<GardenProject> getAllProjects(String query, String status);

    /**
     * Retrieves a single garden project by identifier.
     *
     * @param id project identifier
     * @return the matching garden project
     */
    GardenProject getProjectById(Long id);

    /**
     * Persists a new garden project.
     *
     * @param project project entity to create
     * @return saved project with generated ID
     */
    GardenProject saveProject(GardenProject project);

    /**
     * Updates mutable fields of an existing garden project.
     *
     * @param id project identifier
     * @param updatedProject project payload containing updated values
     * @return persisted updated project
     */
    GardenProject updateProject(Long id, GardenProject updatedProject);

    /**
     * Deletes a garden project by identifier.
     *
     * @param id project identifier
     */
    void deleteProject(Long id);

    /**
     * Returns the most recently created garden projects.
     *
     * @return up to four recent projects
     */
    List<GardenProject> getRecentProjects();

    /**
     * Counts all garden projects stored in the platform.
     *
     * @return total project count
     */
    long countTotalProjects();

    /**
     * Counts garden projects matching a specific status label.
     *
     * @param status lifecycle status label (e.g. {@code In Progress})
     * @return count of projects in that status
     */
    long countProjectsByStatus(String status);
}
