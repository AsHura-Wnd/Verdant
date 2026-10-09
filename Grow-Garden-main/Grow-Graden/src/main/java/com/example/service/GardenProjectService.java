package com.example.service;

import com.example.model.GardenProject;
import java.util.List;

public interface GardenProjectService {

    List<GardenProject> getAllProjects(String query, String status);

    GardenProject getProjectById(Long id);

    GardenProject saveProject(GardenProject project);

    GardenProject updateProject(Long id, GardenProject updatedProject);

    void deleteProject(Long id);

    List<GardenProject> getRecentProjects();

    long countTotalProjects();

    long countProjectsByStatus(String status);
}
