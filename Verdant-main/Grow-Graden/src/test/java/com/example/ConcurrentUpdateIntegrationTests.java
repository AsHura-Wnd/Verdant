package com.example;

import com.example.model.DiscussionQuestion;
import com.example.model.DiscussionReply;
import com.example.model.GardeningTip;
import com.example.repository.GardeningTipRepository;
import com.example.service.DiscussionService;
import com.example.service.GardeningTipService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Concurrency Integration Test Suite.
 * Validates thread safety, atomic database increments, and zero lost updates under
 * high parallel load for:
 * - Article likes (GardeningTip.likesCount)
 * - Discussion views (DiscussionQuestion.viewsCount)
 * - Discussion reply upvotes (DiscussionReply.upvotes)
 * - Optimistic locking protection (@Version) against simultaneous entity overwrite collisions
 */
@SpringBootTest
class ConcurrentUpdateIntegrationTests {

    @Autowired
    private GardeningTipService tipService;

    @Autowired
    private GardeningTipRepository tipRepository;

    @Autowired
    private DiscussionService discussionService;

    @Test
    @DisplayName("Concurrent Article Likes: 25 simultaneous threads like a tip with zero lost updates")
    void testConcurrentLikesOnArticle() throws Exception {
        GardeningTip tip = new GardeningTip(
                "High Concurrency Likes Benchmark Tip",
                "Plant Care",
                "Benchmarking thread-safe likes",
                "Full body text",
                "Benchmarker",
                "Beginner",
                "Ferns",
                null
        );
        tip.setAuthorUsername("elena");
        tip.setLikesCount(0);
        GardeningTip saved = tipService.saveTip(tip);
        Long tipId = saved.getId();
        assertNotNull(tipId);

        int threadCount = 25;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(threadCount);
        AtomicInteger successCounter = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startGate.await(); // Guarantee all threads trigger at the exact same millisecond
                    tipService.likeTip(tipId);
                    successCounter.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endGate.countDown();
                }
            });
        }

        startGate.countDown(); // Fire all threads simultaneously
        boolean finished = endGate.await(15, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(finished, "All concurrent like operations must complete within 15 seconds");
        assertEquals(threadCount, successCounter.get(), "All 25 threads must complete successfully");

        GardeningTip reloaded = tipService.getTipById(tipId);
        assertEquals(25, reloaded.getLikesCount(), "Final likesCount must be exactly 25 with ZERO lost updates");
    }

    @Test
    @DisplayName("Concurrent Discussion Views: 25 simultaneous threads view a question with zero lost updates")
    void testConcurrentViewsOnDiscussion() throws Exception {
        DiscussionQuestion question = new DiscussionQuestion(
                "High Concurrency Discussion Views Benchmark",
                "Plant Diseases",
                "Benchmarking thread-safe view counter increments",
                "Benchmarker",
                "Aroid",
                null
        );
        question.setAuthorUsername("marcus");
        question.setViewsCount(0);
        DiscussionQuestion saved = discussionService.saveQuestion(question);
        Long questionId = saved.getId();
        assertNotNull(questionId);

        int threadCount = 25;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startGate.await();
                    discussionService.getQuestionById(questionId);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endGate.countDown();
                }
            });
        }

        startGate.countDown();
        boolean finished = endGate.await(15, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(finished, "All concurrent view operations must complete within 15 seconds");

        // Reload question and inspect count
        DiscussionQuestion reloaded = discussionService.getQuestionById(questionId);
        // Initial 0 + 25 parallel views + 1 final inspection view = 26
        assertEquals(26, reloaded.getViewsCount(), "Final viewsCount must accurately record all 25 parallel views + 1 final read");
    }

    @Test
    @DisplayName("Concurrent Reply Upvotes: 25 simultaneous threads upvote an answer with zero lost updates")
    void testConcurrentUpvotesOnReply() throws Exception {
        DiscussionQuestion q = new DiscussionQuestion(
                "Upvotes Thread Safety Benchmark Question",
                "Pest Control",
                "Testing concurrent upvotes",
                "Benchmarker",
                "Monstera",
                null
        );
        q.setAuthorUsername("chloe");
        DiscussionQuestion savedQ = discussionService.saveQuestion(q);

        DiscussionReply reply = new DiscussionReply("Dr. Specialist", "Apply potassium silicate for cell wall strength.", true);
        reply.setAuthorUsername("elena");
        reply.setUpvotes(0);
        DiscussionReply savedReply = discussionService.addReply(savedQ.getId(), reply);
        Long replyId = savedReply.getId();
        assertNotNull(replyId);

        int threadCount = 25;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(threadCount);
        AtomicInteger successCounter = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startGate.await();
                    discussionService.upvoteReply(replyId);
                    successCounter.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endGate.countDown();
                }
            });
        }

        startGate.countDown();
        boolean finished = endGate.await(15, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(finished, "All concurrent upvote operations must complete within 15 seconds");
        assertEquals(threadCount, successCounter.get(), "All 25 threads must complete successfully");

        DiscussionReply reloaded = discussionService.getReplyById(replyId);
        assertEquals(25, reloaded.getUpvotes(), "Final upvotes must be exactly 25 with ZERO lost updates");
    }

    @Test
    @DisplayName("Optimistic Locking: Detects concurrent overwrite conflicts via JPA @Version")
    void testOptimisticLockingOnConcurrentEntityEdit() {
        GardeningTip tip = new GardeningTip(
                "Optimistic Locking Test Tip",
                "Propagation",
                "Initial summary",
                "Initial content",
                "Elena Rostova",
                "Beginner",
                "Pothos",
                null
        );
        tip.setAuthorUsername("elena");
        GardeningTip saved = tipRepository.save(tip);

        // Simulate two concurrent users reading the same version of the tip
        GardeningTip userAInstance = tipRepository.findById(saved.getId()).orElseThrow();
        GardeningTip userBInstance = tipRepository.findById(saved.getId()).orElseThrow();

        // User A modifies and saves first
        userAInstance.setTitle("User A Modified Title");
        tipRepository.saveAndFlush(userAInstance);

        // User B tries to save changes based on the stale version -> Must throw ObjectOptimisticLockingFailureException
        userBInstance.setTitle("User B Conflicting Title");
        assertThrows(ObjectOptimisticLockingFailureException.class, () -> {
            tipRepository.saveAndFlush(userBInstance);
        }, "Saving stale entity with outdated @Version must trigger optimistic locking failure");
    }
}
