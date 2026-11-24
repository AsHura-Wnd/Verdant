package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Test Suite for H2 Database Console Security.
 * Validates:
 * - Anonymous visitors are redirected to authentication
 * - Standard community members (ROLE_USER) are forbidden from accessing database internals
 * - Only verified platform administrators (ROLE_ADMIN) can access the H2 console
 */
@SpringBootTest
@AutoConfigureMockMvc
class H2ConsoleSecurityIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("H2 Console Security: Anonymous visitors cannot access /h2-console")
    @WithAnonymousUser
    void testAnonymousCannotAccessH2Console() throws Exception {
        mockMvc.perform(get("/h2-console"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("H2 Console Security: Regular members (ROLE_USER) are forbidden from /h2-console")
    @WithMockUser(username = "elena", roles = {"USER"})
    void testRegularMemberForbiddenFromH2Console() throws Exception {
        mockMvc.perform(get("/h2-console"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?forbidden=true"));
    }

    @Test
    @DisplayName("H2 Console Security: Administrators (ROLE_ADMIN) pass security filter for /h2-console")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAdminGrantedAccessToH2Console() throws Exception {
        // With ROLE_ADMIN, Spring Security permits the request (not 401 or 403)
        mockMvc.perform(get("/h2-console"))
                .andExpect(status().is(org.hamcrest.Matchers.not(org.hamcrest.Matchers.is(403))))
                .andExpect(status().is(org.hamcrest.Matchers.not(org.hamcrest.Matchers.is(401))));
    }
}
