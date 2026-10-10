package com.example.repository;

import com.example.model.GardeningTip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GardeningTipRepository extends JpaRepository<GardeningTip, Long> {

    List<GardeningTip> findAllByOrderByCreatedAtDesc();

    List<GardeningTip> findByCategoryIgnoreCaseOrderByCreatedAtDesc(String category);

    @Query("SELECT t FROM GardeningTip t WHERE " +
           "LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(t.summary) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(t.content) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(t.plantType) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY t.createdAt DESC")
    List<GardeningTip> searchTips(@Param("query") String query);

    List<GardeningTip> findTop4ByOrderByLikesCountDesc();

    List<GardeningTip> findTop5ByOrderByCreatedAtDesc();

    long countByCategoryIgnoreCase(String category);
}
