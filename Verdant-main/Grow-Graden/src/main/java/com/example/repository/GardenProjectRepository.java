package com.example.repository;

import com.example.model.GardenProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GardenProjectRepository extends JpaRepository<GardenProject, Long> {

    List<GardenProject> findAllByOrderByCreatedAtDesc();

    List<GardenProject> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);

    @Query("SELECT p FROM GardenProject p WHERE " +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.plantType) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.location) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY p.createdAt DESC")
    List<GardenProject> searchProjects(@Param("query") String query);

    List<GardenProject> findTop4ByOrderByCreatedAtDesc();

    long countByStatusIgnoreCase(String status);
}
