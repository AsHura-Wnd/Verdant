package com.example.service;

import com.example.model.DiscussionQuestion;
import com.example.model.DiscussionReply;
import java.util.List;

public interface DiscussionService {

    List<DiscussionQuestion> getAllQuestions(String query, String category, Boolean resolved);

    DiscussionQuestion getQuestionById(Long id);

    DiscussionQuestion saveQuestion(DiscussionQuestion question);

    DiscussionQuestion updateQuestion(Long id, DiscussionQuestion updatedQuestion);

    void deleteQuestion(Long id);

    void toggleResolved(Long id);

    DiscussionReply addReply(Long questionId, DiscussionReply reply);

    void deleteReply(Long replyId);

    void upvoteReply(Long replyId);

    List<DiscussionQuestion> getRecentQuestions();

    long countTotalQuestions();

    long countResolvedQuestions();
}
