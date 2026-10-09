package com.example.service;

import com.example.model.GardeningTip;
import java.util.List;

public interface GardeningTipService {

    List<GardeningTip> getAllTips(String query, String category);

    GardeningTip getTipById(Long id);

    GardeningTip saveTip(GardeningTip tip);

    GardeningTip updateTip(Long id, GardeningTip updatedTip);

    void deleteTip(Long id);

    void likeTip(Long id);

    List<GardeningTip> getFeaturedTips();

    List<GardeningTip> getRecentTips();

    long countTotalTips();
}
