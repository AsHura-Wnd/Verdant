package com.example.domain.advisor;

/**
 * Concrete vegetable garden advisor inheriting from AbstractPlantAdvisor.
 * Demonstrates runtime method overriding and specialized domain logic.
 */
public class VegetableAdvisor extends AbstractPlantAdvisor {

    public VegetableAdvisor() {
        super("Vegetables & Edibles", "Temperate to Subtropical");
    }

    @Override
    public CarePlan generateCarePlan(String spaceType) {
        String soil = "Rich, loamy compost blend (pH 6.2 - 6.8) with vermicompost and neem cake";
        String sunlight = "Full Direct Sunlight (Minimum 6-8 hours daily)";
        String tip = "Mulch base with straw to retain root moisture and prevent blossom-end rot.";
        int interval = "Balcony".equalsIgnoreCase(spaceType) ? 1 : 2;

        return new CarePlan(plantGroup, interval, sunlight, soil, tip);
    }

    @Override
    public DiagnosticResult diagnoseSymptom(String symptom) {
        String lower = (symptom != null) ? symptom.toLowerCase() : "";
        if (lower.contains("yellow") || lower.contains("curl")) {
            return new DiagnosticResult(
                    symptom,
                    "Aphid infestation or early nitrogen deficiency",
                    "Moderate",
                    "Spray cold-pressed neem oil (5ml/L) and dress base with high-nitrogen organic compost.",
                    "Neem oil + liquid castile soap emulsion"
            );
        } else if (lower.contains("black") || lower.contains("rot")) {
            return new DiagnosticResult(
                    symptom,
                    "Blossom-End Rot caused by uneven moisture and calcium deficiency",
                    "High",
                    "Establish consistent drip watering schedule and supplement bone meal or gypsum.",
                    "Eggshell infusion tea for organic calcium enrichment"
            );
        }

        return new DiagnosticResult(
                symptom,
                "General vegetable stress (heat shock or container root restriction)",
                "Low",
                "Ensure container depth >= 12 inches and water during early morning hours.",
                "Shade cloth during 12pm-3pm peak solar irradiance"
        );
    }

    @Override
    public int calculateWateringIntervalDays(int ambientTemperatureCelsius) {
        if (ambientTemperatureCelsius >= 32) {
            return 1; // Daily watering required during hot weather
        } else if (ambientTemperatureCelsius >= 20) {
            return 2;
        } else {
            return 3;
        }
    }
}
