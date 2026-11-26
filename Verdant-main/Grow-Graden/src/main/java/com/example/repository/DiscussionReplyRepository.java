package com.example.repository;

import com.example.model.DiscussionReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link DiscussionReply} persistence and atomic upvote increments.
 */
@Repository
public interface DiscussionReplyRepository extends JpaRepository<DiscussionReply, Long> {

    /**
     * Lists replies for a discussion question ordered chronologically.
     *
     * @param questionId parent question identifier
     * @return ordered list of replies
     */
    List<DiscussionReply> findByQuestionIdOrderByCreatedAtAsc(Long questionId);

    /**
     * Counts replies associated with a specific discussion question.
     *
     * @param questionId parent question identifier
     * @return reply count
     */
    long countByQuestionId(Long questionId);

    /**
     * Atomically increments {@code upvotes} for a reply at the database layer.
     *
     * @param id reply identifier
     * @return number of updated rows (1 if found, 0 otherwise)
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE DiscussionReply r SET r.upvotes = r.upvotes + 1 WHERE r.id = :id")
    int incrementUpvotes(@Param("id") Long id);
}
