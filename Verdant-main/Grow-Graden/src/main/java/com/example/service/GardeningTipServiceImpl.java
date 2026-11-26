package com.example.service;

import com.example.exception.GardeningTipNotFoundException;
import com.example.model.GardeningTip;
import com.example.repository.GardeningTipRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Transactional Spring Data JPA implementation of {@link GardeningTipService}.
 */
@Service
@Transactional
@SuppressWarnings("null")
public class GardeningTipServiceImpl implements GardeningTipService {

    private final GardeningTipRepository tipRepository;

    /**
     * Creates a new {@code GardeningTipServiceImpl}.
     *
     * @param tipRepository JPA repository for gardening tips
     */
    public GardeningTipServiceImpl(GardeningTipRepository tipRepository) {
        this.tipRepository = tipRepository;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<GardeningTip> getAllTips(String query, String category) {
        if (query != null && !query.trim().isEmpty()) {
            return tipRepository.searchTips(query.trim());
        }
        if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("ALL")) {
            return tipRepository.findByCategoryIgnoreCaseOrderByCreatedAtDesc(category.trim());
        }
        return tipRepository.findAllByOrderByCreatedAtDesc();
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public GardeningTip getTipById(Long id) {
        return tipRepository.findById(id)
                .orElseThrow(() -> new GardeningTipNotFoundException(id));
    }

    /** {@inheritDoc} */
    @Override
    public GardeningTip saveTip(GardeningTip tip) {
        return tipRepository.save(tip);
    }

    /** {@inheritDoc} */
    @Override
    public GardeningTip updateTip(Long id, GardeningTip updatedTip) {
        GardeningTip existing = getTipById(id);
        existing.setTitle(updatedTip.getTitle());
        existing.setCategory(updatedTip.getCategory());
        existing.setSummary(updatedTip.getSummary());
        existing.setContent(updatedTip.getContent());
        existing.setAuthor(updatedTip.getAuthor());
        existing.setDifficultyLevel(updatedTip.getDifficultyLevel());
        existing.setPlantType(updatedTip.getPlantType());
        if (updatedTip.getImageUrl() != null && !updatedTip.getImageUrl().trim().isEmpty()) {
            existing.setImageUrl(updatedTip.getImageUrl());
        }
        return tipRepository.save(existing);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteTip(Long id) {
        tipRepository.deleteById(id);
    }

    /** {@inheritDoc} */
    @Override
    public void likeTip(Long id) {
        int updated = tipRepository.incrementLikesCount(id);
        if (updated == 0) {
            throw new GardeningTipNotFoundException(id);
        }
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<GardeningTip> getFeaturedTips() {
        return tipRepository.findTop4ByOrderByLikesCountDesc();
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<GardeningTip> getRecentTips() {
        return tipRepository.findTop5ByOrderByCreatedAtDesc();
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public long countTotalTips() {
        return tipRepository.count();
    }
}
