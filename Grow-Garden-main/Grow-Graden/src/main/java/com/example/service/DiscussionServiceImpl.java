package com.example.service;

import com.example.exception.DiscussionQuestionNotFoundException;
import com.example.exception.ResourceNotFoundException;
import com.example.model.DiscussionQuestion;
import com.example.model.DiscussionReply;
import com.example.repository.DiscussionQuestionRepository;
import com.example.repository.DiscussionReplyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DiscussionServiceImpl implements DiscussionService {

    private final DiscussionQuestionRepository questionRepository;
    private final DiscussionReplyRepository replyRepository;

    public DiscussionServiceImpl(DiscussionQuestionRepository questionRepository,
                                 DiscussionReplyRepository replyRepository) {
        this.questionRepository = questionRepository;
        this.replyRepository = replyRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscussionQuestion> getAllQuestions(String query, String category, Boolean resolved) {
        if (query != null && !query.trim().isEmpty()) {
            return questionRepository.searchQuestions(query.trim());
        }
        if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("ALL")) {
            return questionRepository.findByCategoryIgnoreCaseOrderByCreatedAtDesc(category.trim());
        }
        if (resolved != null) {
            return questionRepository.findByResolvedOrderByCreatedAtDesc(resolved);
        }
        return questionRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public DiscussionQuestion getQuestionById(Long id) {
        DiscussionQuestion question = questionRepository.findById(id)
                .orElseThrow(() -> new DiscussionQuestionNotFoundException(id));
        question.setViewsCount(question.getViewsCount() + 1);
        return questionRepository.save(question);
    }

    @Override
    public DiscussionQuestion saveQuestion(DiscussionQuestion question) {
        return questionRepository.save(question);
    }

    @Override
    public DiscussionQuestion updateQuestion(Long id, DiscussionQuestion updatedQuestion) {
        DiscussionQuestion existing = questionRepository.findById(id)
                .orElseThrow(() -> new DiscussionQuestionNotFoundException(id));
        existing.setTitle(updatedQuestion.getTitle());
        existing.setCategory(updatedQuestion.getCategory());
        existing.setDescription(updatedQuestion.getDescription());
        existing.setAuthor(updatedQuestion.getAuthor());
        existing.setPlantType(updatedQuestion.getPlantType());
        if (updatedQuestion.getImageUrl() != null && !updatedQuestion.getImageUrl().trim().isEmpty()) {
            existing.setImageUrl(updatedQuestion.getImageUrl());
        }
        return questionRepository.save(existing);
    }

    @Override
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    @Override
    public void toggleResolved(Long id) {
        DiscussionQuestion question = questionRepository.findById(id)
                .orElseThrow(() -> new DiscussionQuestionNotFoundException(id));
        question.setResolved(!question.isResolved());
        questionRepository.save(question);
    }

    @Override
    public DiscussionReply addReply(Long questionId, DiscussionReply reply) {
        DiscussionQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new DiscussionQuestionNotFoundException(questionId));
        reply.setQuestion(question);
        return replyRepository.save(reply);
    }

    @Override
    public void deleteReply(Long replyId) {
        replyRepository.deleteById(replyId);
    }

    @Override
    public void upvoteReply(Long replyId) {
        DiscussionReply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion Reply", replyId));
        reply.setUpvotes(reply.getUpvotes() + 1);
        replyRepository.save(reply);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscussionQuestion> getRecentQuestions() {
        return questionRepository.findTop5ByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalQuestions() {
        return questionRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countResolvedQuestions() {
        return questionRepository.countByResolved(true);
    }
}
