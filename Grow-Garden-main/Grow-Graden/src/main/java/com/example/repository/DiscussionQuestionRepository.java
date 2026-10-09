package com.example.repository;

import com.example.model.DiscussionQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussionQuestionRepository extends JpaRepository<DiscussionQuestion, Long> {

    List<DiscussionQuestion> findAllByOrderByCreatedAtDesc();

    List<DiscussionQuestion> findByCategoryIgnoreCaseOrderByCreatedAtDesc(String category);

    List<DiscussionQuestion> findByResolvedOrderByCreatedAtDesc(boolean resolved);

    @Query("SELECT q FROM DiscussionQuestion q WHERE " +
           "LOWER(q.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(q.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(q.plantType) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY q.createdAt DESC")
    List<DiscussionQuestion> searchQuestions(@Param("query") String query);

    List<DiscussionQuestion> findTop5ByOrderByCreatedAtDesc();

    long countByResolved(boolean resolved);
}
