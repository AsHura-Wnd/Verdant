package com.example.service;

import com.example.model.DiscussionQuestion;
import com.example.model.DiscussionReply;
import java.util.List;

/**
 * Service contract for managing plant clinic discussion threads, replies, and engagement counters.
 */
public interface DiscussionService {

    /**
     * Lists discussion questions optionally filtered by keyword, category, or resolved flag.
     *
     * @param query optional text filter
     * @param category optional category filter
     * @param resolved optional resolution state filter
     * @return matching discussion questions
     */
    List<DiscussionQuestion> getAllQuestions(String query, String category, Boolean resolved);

    /**
     * Retrieves a discussion question by ID and atomically increments its view counter.
     *
     * @param id question identifier
     * @return the matching discussion question
     */
    DiscussionQuestion getQuestionById(Long id);

    /**
     * Persists a new discussion question.
     *
     * @param question question entity to save
     * @return saved question with generated ID
     */
    DiscussionQuestion saveQuestion(DiscussionQuestion question);

    /**
     * Updates editable fields on an existing discussion question.
     *
     * @param id question identifier
     * @param updatedQuestion payload containing updated values
     * @return saved updated question
     */
    DiscussionQuestion updateQuestion(Long id, DiscussionQuestion updatedQuestion);

    /**
     * Deletes a discussion question and its cascading replies.
     *
     * @param id question identifier
     */
    void deleteQuestion(Long id);

    /**
     * Toggles the resolved flag on a discussion question.
     *
     * @param id question identifier
     */
    void toggleResolved(Long id);

    /**
     * Appends a reply to an existing discussion question.
     *
     * @param questionId parent question identifier
     * @param reply reply entity to save
     * @return saved reply entity
     */
    DiscussionReply addReply(Long questionId, DiscussionReply reply);

    /**
     * Retrieves a single discussion reply by ID.
     *
     * @param replyId reply identifier
     * @return matching reply entity
     */
    DiscussionReply getReplyById(Long replyId);

    /**
     * Deletes a specific reply by ID.
     *
     * @param replyId reply identifier
     */
    void deleteReply(Long replyId);

    /**
     * Atomically increments the helpful upvote counter on a reply.
     *
     * @param replyId reply identifier
     */
    void upvoteReply(Long replyId);

    /**
     * Returns the most recently posted discussion questions.
     *
     * @return up to five recent questions
     */
    List<DiscussionQuestion> getRecentQuestions();

    /**
     * Counts all discussion questions stored in the platform.
     *
     * @return total question count
     */
    long countTotalQuestions();

    /**
     * Counts discussion questions marked as resolved.
     *
     * @return resolved question count
     */
    long countResolvedQuestions();
}
