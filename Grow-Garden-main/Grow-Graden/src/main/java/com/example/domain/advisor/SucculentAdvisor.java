package com.example.domain.advisor;

/**
 * Concrete succulent and cacti advisor inheriting from AbstractPlantAdvisor.
 * Demonstrates specialized drought-tolerant plant care and symptom analysis.
 */
public class SucculentAdvisor extends AbstractPlantAdvisor {

    public SucculentAdvisor() {
        super("Succulents & Cacti", "Arid to Mediterranean");
    }

    @Override
    public CarePlan generateCarePlan(String spaceType) {
        String soil = "Fast-draining gritty mix: 50% perlite/pumice, 30% coarse sand, 20% potting soil";
        String sunlight = "Bright Direct to Filtered Sunlight (4-6 hours daily)";
        String tip = "Follow the soak-and-dry method. Only water when soil is 100% dry to the pot bottom.";
        int interval = "Indoor Windowsill".equalsIgnoreCase(spaceType) ? 14 : 10;

        return new CarePlan(plantGroup, interval, sunlight, soil, tip);
    }

    @Override
    public DiagnosticResult diagnoseSymptom(String symptom) {
        String lower = (symptom != null) ? symptom.toLowerCase() : "";
        if (lower.contains("mushy") || lower.contains("translucent") || lower.contains("yellow")) {
            return new DiagnosticResult(
                    symptom,
                    "Severe Overwatering leading to cellular edema and root rot",
                    "Critical",
                    "Cease watering immediately. Unpot, prune rotted black roots, let callous for 3 days, and repot in dry gritty mix.",
                    "Active charcoal powder dusted on root cuts as anti-fungal barrier"
            );
        } else if (lower.contains("wrinkle") || lower.contains("shrivel")) {
            return new DiagnosticResult(
                    symptom,
                    "Dehydration and underwatering",
                    "Low",
                    "Give a deep bottom-watering soak until surface moisture is visible, then drain thoroughly.",
                    "Bottom-watering tray soak for 20 minutes"
            );
        }

        return new DiagnosticResult(
                symptom,
                "Etiolation (stretching) due to insufficient light",
                "Low",
                "Gradually move closer to south-facing window or install full-spectrum grow light.",
                "Sunlight acclimation over 7 days"
        );
    }

    @Override
    public int calculateWateringIntervalDays(int ambientTemperatureCelsius) {
        if (ambientTemperatureCelsius >= 35) {
            return 7; // Even in hot weather, succulents store water in fleshy leaves
        } else if (ambientTemperatureCelsius >= 20) {
            return 12;
        } else {
            return 21; // Semi-dormant during cool winter periods
        }
    }
}
