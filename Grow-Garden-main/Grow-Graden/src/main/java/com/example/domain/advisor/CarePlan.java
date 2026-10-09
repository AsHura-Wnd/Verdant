package com.example.domain.advisor;

/**
 * Value object encapsulating horticultural care plan specifications.
 */
public class CarePlan {

    private final String plantGroup;
    private final int wateringIntervalDays;
    private final String sunlightRequirement;
    private final String soilRecommendation;
    private final String seasonalTip;

    public CarePlan(String plantGroup, int wateringIntervalDays, String sunlightRequirement,
                    String soilRecommendation, String seasonalTip) {
        this.plantGroup = plantGroup;
        this.wateringIntervalDays = wateringIntervalDays;
        this.sunlightRequirement = sunlightRequirement;
        this.soilRecommendation = soilRecommendation;
        this.seasonalTip = seasonalTip;
    }

    public String getPlantGroup() {
        return plantGroup;
    }

    public int getWateringIntervalDays() {
        return wateringIntervalDays;
    }

    public String getSunlightRequirement() {
        return sunlightRequirement;
    }

    public String getSoilRecommendation() {
        return soilRecommendation;
    }

    public String getSeasonalTip() {
        return seasonalTip;
    }
}
