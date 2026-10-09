package com.example.service;

import com.example.exception.GardenProjectNotFoundException;
import com.example.model.GardenProject;
import com.example.repository.GardenProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GardenProjectServiceImpl implements GardenProjectService {

    private final GardenProjectRepository projectRepository;

    public GardenProjectServiceImpl(GardenProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GardenProject> getAllProjects(String query, String status) {
        if (query != null && !query.trim().isEmpty()) {
            return projectRepository.searchProjects(query.trim());
        }
        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL")) {
            return projectRepository.findByStatusIgnoreCaseOrderByCreatedAtDesc(status.trim());
        }
        return projectRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public GardenProject getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new GardenProjectNotFoundException(id));
    }

    @Override
    public GardenProject saveProject(GardenProject project) {
        return projectRepository.save(project);
    }

    @Override
    public GardenProject updateProject(Long id, GardenProject updatedProject) {
        GardenProject existing = getProjectById(id);
        existing.setTitle(updatedProject.getTitle());
        existing.setDescription(updatedProject.getDescription());
        existing.setPlantType(updatedProject.getPlantType());
        existing.setLocation(updatedProject.getLocation());
        existing.setStatus(updatedProject.getStatus());
        existing.setStartDate(updatedProject.getStartDate());
        existing.setTargetDate(updatedProject.getTargetDate());
        existing.setNotes(updatedProject.getNotes());
        existing.setGardenerName(updatedProject.getGardenerName());
        if (updatedProject.getImageUrl() != null && !updatedProject.getImageUrl().trim().isEmpty()) {
            existing.setImageUrl(updatedProject.getImageUrl());
        }
        return projectRepository.save(existing);
    }

    @Override
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GardenProject> getRecentProjects() {
        return projectRepository.findTop4ByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalProjects() {
        return projectRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countProjectsByStatus(String status) {
        return projectRepository.countByStatusIgnoreCase(status);
    }
}
