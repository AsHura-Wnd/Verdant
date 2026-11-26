package com.example.controller;

import com.example.domain.advisor.AbstractPlantAdvisor;
import com.example.domain.advisor.CarePlan;
import com.example.domain.advisor.DiagnosticResult;
import com.example.domain.advisor.PlantAdvisorRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing polymorphic plant diagnosis and care-plan generation endpoints.
 * Dynamically resolves concrete {@link AbstractPlantAdvisor} implementations via
 * {@link PlantAdvisorRegistry}.
 */
@RestController
@RequestMapping("/api/advisor")
public class PlantAdvisorRestController {

    private final PlantAdvisorRegistry advisorRegistry;

    /**
     * Creates a new {@code PlantAdvisorRestController}.
     *
     * @param advisorRegistry registry resolving plant-specific advisors
     */
    public PlantAdvisorRestController(PlantAdvisorRegistry advisorRegistry) {
        this.advisorRegistry = advisorRegistry;
    }

    /**
     * Diagnoses a plant symptom using the advisor matching {@code plantType}.
     *
     * @param plantType plant category or species name
     * @param symptom observed botanical symptom description
     * @return diagnostic assessment and recommended organic solution
     */
    @GetMapping("/diagnose")
    public ResponseEntity<DiagnosticResult> diagnosePlant(
            @RequestParam("plantType") String plantType,
            @RequestParam("symptom") String symptom) {

        AbstractPlantAdvisor advisor = advisorRegistry.resolveAdvisor(plantType);
        return ResponseEntity.ok(advisor.diagnoseSymptom(symptom));
    }

    /**
     * Generates a tailored watering, light, and care schedule for a plant and growing space.
     *
     * @param plantType plant category or species name
     * @param spaceType growing environment (defaults to {@code Balcony})
     * @return structured care plan for the resolved plant group
     */
    @GetMapping("/care-plan")
    public ResponseEntity<CarePlan> generateCarePlan(
            @RequestParam("plantType") String plantType,
            @RequestParam(value = "spaceType", defaultValue = "Balcony") String spaceType) {

        AbstractPlantAdvisor advisor = advisorRegistry.resolveAdvisor(plantType);
        return ResponseEntity.ok(advisor.generateCarePlan(spaceType));
    }
}
