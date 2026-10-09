package com.example.domain.advisor;

/**
 * Concrete tropical houseplant advisor inheriting from AbstractPlantAdvisor.
 * Specializes in Monstera, Pothos, Philodendron, Ferns, and Calatheas.
 */
public class TropicalFoliageAdvisor extends AbstractPlantAdvisor {

    public TropicalFoliageAdvisor() {
        super("Tropical Foliage & Aroids", "Humid Tropical / Indoor");
    }

    @Override
    public CarePlan generateCarePlan(String spaceType) {
        String soil = "Chunky aroid blend: 40% pine bark/cocopeat, 30% perlite, 20% vermicompost, 10% charcoal";
        String sunlight = "Bright Indirect / Dappled Sunlight (Shield from harsh noon solar rays)";
        String tip = "Maintain 55-65% relative humidity and wipe broad leaves with a damp cloth monthly.";
        int interval = 6;

        return new CarePlan(plantGroup, interval, sunlight, soil, tip);
    }

    @Override
    public DiagnosticResult diagnoseSymptom(String symptom) {
        String lower = (symptom != null) ? symptom.toLowerCase() : "";
        if (lower.contains("yellow") && (lower.contains("brown") || lower.contains("spot"))) {
            return new DiagnosticResult(
                    symptom,
                    "Root suffocation or fungal leaf blight from chronic overwatering",
                    "High",
                    "Allow top 3 inches of potting medium to dry out completely. Ensure planter has drainage holes.",
                    "Hydrogen peroxide (3%) root drench diluted 1:4 with water"
            );
        } else if (lower.contains("web") || lower.contains("mite") || lower.contains("speckle")) {
            return new DiagnosticResult(
                    symptom,
                    "Spider mite infestation thriving in dry indoor air",
                    "High",
                    "Wash entire foliage under shower spray and mist underside of leaves with neem oil emulsified with mild soap.",
                    "Neem oil spray + increasing ambient room humidity"
            );
        } else if (lower.contains("brown tips") || lower.contains("crispy")) {
            return new DiagnosticResult(
                    symptom,
                    "Low ambient humidity or tap water fluoride/chlorine sensitivity",
                    "Low",
                    "Switch to filtered or rainwater and group plants near a room humidifier.",
                    "Pebble humidity tray underneath planter"
            );
        }

        return new DiagnosticResult(
                symptom,
                "Environmental acclimation or light adjustment stress",
                "Low",
                "Keep away from direct air conditioner or heater drafts.",
                "Gentle morning indirect sunlight exposure"
        );
    }

    @Override
    public int calculateWateringIntervalDays(int ambientTemperatureCelsius) {
        if (ambientTemperatureCelsius >= 32) {
            return 4;
        } else if (ambientTemperatureCelsius >= 22) {
            return 7;
        } else {
            return 10;
        }
    }
}
