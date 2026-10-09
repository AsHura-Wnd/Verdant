package com.example.domain.advisor;

/**
 * Concrete culinary herb advisor inheriting from AbstractPlantAdvisor.
 * Specializes in basil, mint, rosemary, oregano, and coriander management.
 */
public class HerbAdvisor extends AbstractPlantAdvisor {

    public HerbAdvisor() {
        super("Culinary Herbs", "Mediterranean to Temperate");
    }

    @Override
    public CarePlan generateCarePlan(String spaceType) {
        String soil = "Aerated potting mix with 30% perlite and 20% vermicompost (pH 6.5)";
        String sunlight = "Bright Direct Sunlight (4-6 hours daily on balcony or sunny sill)";
        String tip = "Pinch top flower buds weekly to channel energy into aromatic leafy foliage.";
        int interval = 3;

        return new CarePlan(plantGroup, interval, sunlight, soil, tip);
    }

    @Override
    public DiagnosticResult diagnoseSymptom(String symptom) {
        String lower = (symptom != null) ? symptom.toLowerCase() : "";
        if (lower.contains("powder") || lower.contains("white") || lower.contains("mildew")) {
            return new DiagnosticResult(
                    symptom,
                    "Powdery Mildew caused by dense canopy and stagnant humidity",
                    "Moderate",
                    "Prune dense inner stems to improve airflow and mist with baking soda solution (1 tsp/L).",
                    "Baking soda + mild soap bio-fungicide"
            );
        } else if (lower.contains("woody") || lower.contains("leggy")) {
            return new DiagnosticResult(
                    symptom,
                    "Lack of harvesting/pinching and low light intensity",
                    "Low",
                    "Cut back stems by one-third above a leaf node and relocate to higher sunlight.",
                    "Regular rotational pruning"
            );
        }

        return new DiagnosticResult(
                symptom,
                "Mild root saturation or nutrient depletion in small planter",
                "Low",
                "Top-dress with 1 tablespoon vermicompost every 3 weeks.",
                "Liquid seaweed extract diluted 1:500"
        );
    }

    @Override
    public int calculateWateringIntervalDays(int ambientTemperatureCelsius) {
        if (ambientTemperatureCelsius >= 30) {
            return 2;
        } else if (ambientTemperatureCelsius >= 18) {
            return 3;
        } else {
            return 5;
        }
    }
}
