package com.example.domain.advisor;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Registry and polymorphic factory for botanical advisors.
 * Demonstrates:
 * - Runtime Polymorphism: Calls on AbstractPlantAdvisor dynamically dispatch to concrete subclasses.
 * - Collections and Generics: Utilizes Map<String, AbstractPlantAdvisor> and List<AbstractPlantAdvisor>.
 */
@Component
public class PlantAdvisorRegistry {

    private final Map<String, AbstractPlantAdvisor> registry = new LinkedHashMap<>();
    private final AbstractPlantAdvisor defaultAdvisor = new TropicalFoliageAdvisor();

    public PlantAdvisorRegistry() {
        registerAdvisor(new VegetableAdvisor());
        registerAdvisor(new SucculentAdvisor());
        registerAdvisor(new HerbAdvisor());
        registerAdvisor(new TropicalFoliageAdvisor());
    }

    public void registerAdvisor(AbstractPlantAdvisor advisor) {
        registry.put(advisor.getPlantGroup().toLowerCase(), advisor);
    }

    /**
     * Resolves the appropriate AbstractPlantAdvisor polymorphically by plant type keyword.
     */
    public AbstractPlantAdvisor resolveAdvisor(String plantType) {
        if (plantType == null || plantType.trim().isEmpty()) {
            return defaultAdvisor;
        }

        String lower = plantType.toLowerCase();

        if (lower.contains("tomato") || lower.contains("pepper") || lower.contains("vegetable") || lower.contains("chili")) {
            return registry.get("vegetables & edibles");
        } else if (lower.contains("succulent") || lower.contains("cacti") || lower.contains("cactus") || lower.contains("echeveria")) {
            return registry.get("succulents & cacti");
        } else if (lower.contains("herb") || lower.contains("basil") || lower.contains("thyme") || lower.contains("rosemary") || lower.contains("mint")) {
            return registry.get("culinary herbs");
        } else {
            return registry.getOrDefault("tropical foliage & aroids", defaultAdvisor);
        }
    }

    public List<AbstractPlantAdvisor> getAllAdvisors() {
        return new ArrayList<>(registry.values());
    }
}
