package com.example.domain.advisor;

/**
 * Abstract base class demonstrating Core Java OOP Inheritance and Polymorphic Contracts.
 * Defines the common state and abstract operations for specialized botanical advisors.
 */
public abstract class AbstractPlantAdvisor {

    protected final String plantGroup;
    protected final String defaultClimateZone;

    public AbstractPlantAdvisor(String plantGroup, String defaultClimateZone) {
        this.plantGroup = plantGroup;
        this.defaultClimateZone = defaultClimateZone;
    }

    public String getPlantGroup() {
        return plantGroup;
    }

    public String getDefaultClimateZone() {
        return defaultClimateZone;
    }

    /**
     * Common template method shared across all advisors.
     */
    public String getAdvisorProfile() {
        return String.format("Certified Horticultural Advisory Module: %s (Optimized for: %s)",
                plantGroup, defaultClimateZone);
    }

    /**
     * Polymorphic abstract method to generate customized care plan based on growing space.
     */
    public abstract CarePlan generateCarePlan(String spaceType);

    /**
     * Polymorphic abstract method to diagnose reported symptoms and produce remediation advice.
     */
    public abstract DiagnosticResult diagnoseSymptom(String symptom);

    /**
     * Polymorphic abstract method calculating watering interval adjusted for temperature.
     */
    public abstract int calculateWateringIntervalDays(int ambientTemperatureCelsius);
}
