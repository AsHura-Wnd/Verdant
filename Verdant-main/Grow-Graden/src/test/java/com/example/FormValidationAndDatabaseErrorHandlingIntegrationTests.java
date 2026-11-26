package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Test Suite for Form Validation and Database Error Handling.
 * Validates:
 * - Rejection of invalid, blank, or malformed web forms with field-level feedback
 * - Date consistency rules (targetDate must not precede startDate)
 * - Safe image URL pattern validation (guards against XSS/javascript schemes)
 * - Clean translation of unique constraint collisions into user-friendly messages
 * - Graceful REST error formatting without raw SQL or internal exception leaks
 */
@SpringBootTest
@AutoConfigureMockMvc
class FormValidationAndDatabaseErrorHandlingIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Validation: Submitting tip with blank required fields returns form with binding errors")
    @WithMockUser(username = "elena", roles = {"USER"})
    void testTipValidationFailsOnBlankRequiredFields() throws Exception {
        mockMvc.perform(post("/tips/new")
                        .with(csrf())
                        .param("title", "") // Invalid blank title
                        .param("category", "") // Invalid blank category
                        .param("summary", "") // Invalid blank summary
                        .param("content", "") // Invalid blank content
                        .param("author", "")) // Invalid blank author
                .andExpect(status().isOk())
                .andExpect(view().name("tips/form"))
                .andExpect(model().attributeHasFieldErrors("tip", "title", "category", "summary", "content", "author"));
    }

    @Test
    @DisplayName("Validation: Submitting project with targetDate before startDate is rejected")
    @WithMockUser(username = "marcus", roles = {"USER"})
    void testProjectValidationFailsOnInvalidDates() throws Exception {
        mockMvc.perform(post("/projects/new")
                        .with(csrf())
                        .param("title", "Balcony Herb Project")
                        .param("plantType", "Basil & Mint")
                        .param("location", "Balcony")
                        .param("status", "In Progress")
                        .param("gardenerName", "Dr. Marcus Chen")
                        .param("description", "Valid description of the project")
                        .param("startDate", "2026-06-15")
                        .param("targetDate", "2026-06-01")) // Invalid: target date before start date
                .andExpect(status().isOk())
                .andExpect(view().name("projects/form"))
                .andExpect(model().attributeHasFieldErrors("project", "targetDate"));
    }

    @Test
    @DisplayName("Validation: Image URL with non-http/https protocol fails pattern constraint")
    @WithMockUser(username = "elena", roles = {"USER"})
    void testTipValidationFailsOnInvalidImageUrl() throws Exception {
        mockMvc.perform(post("/tips/new")
                        .with(csrf())
                        .param("title", "Valid Title For Herb Care")
                        .param("category", "Plant Care")
                        .param("summary", "Valid summary of the tip")
                        .param("content", "Valid full length article content")
                        .param("author", "Elena Rostova")
                        .param("imageUrl", "javascript:alert(1)")) // Malicious protocol
                .andExpect(status().isOk())
                .andExpect(view().name("tips/form"))
                .andExpect(model().attributeHasFieldErrors("tip", "imageUrl"));
    }

    @Test
    @DisplayName("Database Error Handling: Duplicate username registration is handled with friendly error")
    void testRegistrationDuplicateUsernameHandling() throws Exception {
        // 'elena' already exists from DataInitializer
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "elena") // Duplicate
                        .param("fullName", "Another Elena")
                        .param("email", "another_elena@example.com")
                        .param("password", "Secret123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attribute("errorMessage", containsString("already taken")));
    }

    @Test
    @DisplayName("REST Error Handling: Non-existent entity returns clean JSON 404 without stack traces")
    void testRestNotFoundFormat() throws Exception {
        mockMvc.perform(get("/api/tips/888888"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value(containsString("888888")))
                .andExpect(jsonPath("$.path").value("/api/tips/888888"));
    }

    @Test
    @DisplayName("REST @RestControllerAdvice: Missing required query parameter returns clean 400 JSON")
    void testRestMissingRequestParameterReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/advisor/diagnose").param("plantType", "Tomato"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value(containsString("symptom")))
                .andExpect(jsonPath("$.path").value("/api/advisor/diagnose"));
    }

    @Test
    @DisplayName("REST @RestControllerAdvice: Invalid path variable type returns clean 400 JSON")
    void testRestTypeMismatchReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/tips/not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value(containsString("id")))
                .andExpect(jsonPath("$.path").value("/api/tips/not-a-number"));
    }
}
