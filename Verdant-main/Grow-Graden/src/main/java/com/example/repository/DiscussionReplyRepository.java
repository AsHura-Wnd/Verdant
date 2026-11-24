package com.example.repository;

import com.example.model.DiscussionReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussionReplyRepository extends JpaRepository<DiscussionReply, Long> {

    List<DiscussionReply> findByQuestionIdOrderByCreatedAtAsc(Long questionId);

    long countByQuestionId(Long questionId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE DiscussionReply r SET r.upvotes = r.upvotes + 1 WHERE r.id = :id")
    int incrementUpvotes(@Param("id") Long id);
}
