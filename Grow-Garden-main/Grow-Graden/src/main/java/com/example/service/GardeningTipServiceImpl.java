package com.example.service;

import com.example.exception.GardeningTipNotFoundException;
import com.example.model.GardeningTip;
import com.example.repository.GardeningTipRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GardeningTipServiceImpl implements GardeningTipService {

    private final GardeningTipRepository tipRepository;

    public GardeningTipServiceImpl(GardeningTipRepository tipRepository) {
        this.tipRepository = tipRepository;
    }

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

    @Override
    @Transactional(readOnly = true)
    public GardeningTip getTipById(Long id) {
        return tipRepository.findById(id)
                .orElseThrow(() -> new GardeningTipNotFoundException(id));
    }

    @Override
    public GardeningTip saveTip(GardeningTip tip) {
        return tipRepository.save(tip);
    }

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

    @Override
    public void deleteTip(Long id) {
        tipRepository.deleteById(id);
    }

    @Override
    public void likeTip(Long id) {
        GardeningTip tip = getTipById(id);
        tip.setLikesCount(tip.getLikesCount() + 1);
        tipRepository.save(tip);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GardeningTip> getFeaturedTips() {
        return tipRepository.findTop4ByOrderByLikesCountDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GardeningTip> getRecentTips() {
        return tipRepository.findTop5ByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalTips() {
        return tipRepository.count();
    }
}
