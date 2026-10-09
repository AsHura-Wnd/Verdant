package com.example.repository;

import com.example.model.DiscussionReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussionReplyRepository extends JpaRepository<DiscussionReply, Long> {

    List<DiscussionReply> findByQuestionIdOrderByCreatedAtAsc(Long questionId);

    long countByQuestionId(Long questionId);
}
