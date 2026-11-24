package com.example;

import com.example.model.GardeningTip;
import com.example.service.GardeningTipService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Test Suite for User Authentication and Role-Based Authorization.
 * Validates:
 * - Public viewing of community content for anonymous guests
 * - Authentication gates preventing unauthenticated content creation or tampering
 * - Ownership isolation (User A cannot edit/delete User B's content)
 * - Administrative moderation override (ROLE_ADMIN can moderate any content)
 * - Self-registration and login form workflows
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationAndAuthorizationIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GardeningTipService tipService;

    @Test
    @DisplayName("Public Access: Anonymous guests can freely view tips list, tip detail, discussions, and projects")
    @WithAnonymousUser
    void testPublicBrowsingAllowedForAnonymous() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));

        mockMvc.perform(get("/tips"))
                .andExpect(status().isOk())
                .andExpect(view().name("tips/list"));

        mockMvc.perform(get("/discussions"))
                .andExpect(status().isOk())
                .andExpect(view().name("discussions/list"));

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(view().name("projects/list"));
    }

    @Test
    @DisplayName("Security Gate: Anonymous user is redirected to login when attempting to publish content")
    @WithAnonymousUser
    void testAnonymousCannotPublishTip() throws Exception {
        mockMvc.perform(get("/tips/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(post("/tips/new")
                        .with(csrf())
                        .param("title", "Unauthorized Post Attempt")
                        .param("category", "Plant Care")
                        .param("summary", "Summary")
                        .param("content", "Content body")
                        .param("author", "Hacker"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("Security Gate: Anonymous user cannot delete community content")
    @WithAnonymousUser
    void testAnonymousCannotDeleteTip() throws Exception {
        mockMvc.perform(post("/tips/1/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("Ownership Authorization: Content creator can edit their own tip")
    @WithMockUser(username = "elena", roles = {"USER"})
    void testAuthorCanEditOwnTip() throws Exception {
        // Tip 1 was initialized with authorUsername = 'elena'
        mockMvc.perform(get("/tips/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("tips/form"))
                .andExpect(model().attribute("isEdit", true));

        mockMvc.perform(post("/tips/1/edit")
                        .with(csrf())
                        .param("title", "Natural Aphid Control Updated")
                        .param("category", "Pest & Disease Control")
                        .param("summary", "A gentle, non-toxic horticultural solution updated.")
                        .param("content", "Updated directions for application.")
                        .param("author", "Elena Rostova")
                        .param("difficultyLevel", "Beginner"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tips/1"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @DisplayName("Ownership Authorization: Non-author user is forbidden from editing another member's tip")
    @WithMockUser(username = "marcus", roles = {"USER"})
    void testNonAuthorCannotEditAnotherUsersTip() throws Exception {
        // Tip 1 belongs to 'elena'. User 'marcus' must be rejected with access restriction
        mockMvc.perform(get("/tips/1/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?forbidden=true"))
                .andExpect(flash().attributeExists("errorMessage"));

        mockMvc.perform(post("/tips/1/edit")
                        .with(csrf())
                        .param("title", "Marcus Tampering Attempt")
                        .param("category", "Pest & Disease Control")
                        .param("summary", "Malicious summary")
                        .param("content", "Malicious content")
                        .param("author", "Dr. Marcus Chen"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?forbidden=true"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("Ownership Authorization: Non-author user is forbidden from deleting another member's tip")
    @WithMockUser(username = "marcus", roles = {"USER"})
    void testNonAuthorCannotDeleteAnotherUsersTip() throws Exception {
        // Tip 1 belongs to 'elena'. 'marcus' cannot delete it
        mockMvc.perform(post("/tips/1/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?forbidden=true"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("Role-Based Moderation: Platform Administrator (ROLE_ADMIN) can delete community content")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAdminCanDeleteAnyContent() throws Exception {
        // Create a tip by elena to test administrative deletion
        GardeningTip tip = new GardeningTip(
                "Tip for Admin Moderation Test",
                "Plant Care",
                "Short summary for moderation test",
                "Full body text here",
                "Elena Rostova",
                "Beginner",
                "Herbs",
                null
        );
        tip.setAuthorUsername("elena");
        GardeningTip saved = tipService.saveTip(tip);
        assertNotNull(saved.getId());

        // Admin can delete it cleanly
        mockMvc.perform(post("/tips/" + saved.getId() + "/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tips"))
                .andExpect(flash().attribute("successMessage", containsString("deleted successfully")));
    }

    @Test
    @DisplayName("Authentication: Login form submission with valid demo credentials succeeds")
    void testLoginFormSuccess() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "elena")
                        .param("password", "Elena@123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("Authentication: Login form submission with invalid password fails gracefully")
    void testLoginFormFailure() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "elena")
                        .param("password", "WrongPassword!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    @DisplayName("Self-Registration: New botanical account registration succeeds and allows login")
    void testUserSelfRegistration() throws Exception {
        String testUser = "new_grower_" + System.currentTimeMillis();
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", testUser)
                        .param("fullName", "New Community Grower")
                        .param("email", testUser + "@verdant.org")
                        .param("password", "SecretGrower@123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify the newly registered user can now log in
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", testUser)
                        .param("password", "SecretGrower@123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
