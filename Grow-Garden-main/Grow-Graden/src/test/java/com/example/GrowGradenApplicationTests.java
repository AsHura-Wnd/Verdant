package com.example;

import com.example.model.DiscussionQuestion;
import com.example.model.DiscussionReply;
import com.example.model.GardenProject;
import com.example.model.GardeningTip;
import com.example.service.DiscussionService;
import com.example.service.GardenProjectService;
import com.example.service.GardeningTipService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GrowGradenApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GardeningTipService tipService;

    @Autowired
    private DiscussionService discussionService;

    @Autowired
    private GardenProjectService projectService;

    @Test
    void contextLoads() {
        assertNotNull(tipService);
        assertNotNull(discussionService);
        assertNotNull(projectService);
    }

    @Test
    void testGardeningTipCRUD() {
        GardeningTip tip = new GardeningTip(
                "Testing Soil Moisture with Chopsticks",
                "Plant Care",
                "A simple kitchen tool technique to check moisture deep in the root zone.",
                "Insert a dry wooden chopstick 3 inches into the soil. If it comes out clean, it is time to water.",
                "Test Gardener",
                "Beginner",
                "Houseplants",
                null
        );
        GardeningTip saved = tipService.saveTip(tip);
        assertNotNull(saved.getId());
        assertEquals("Testing Soil Moisture with Chopsticks", saved.getTitle());

        // Like tip
        int initialLikes = saved.getLikesCount();
        tipService.likeTip(saved.getId());
        GardeningTip updated = tipService.getTipById(saved.getId());
        assertEquals(initialLikes + 1, updated.getLikesCount());

        // Update tip
        updated.setTitle("Updated Chopstick Moisture Test");
        tipService.updateTip(saved.getId(), updated);
        assertEquals("Updated Chopstick Moisture Test", tipService.getTipById(saved.getId()).getTitle());

        // Clean up
        tipService.deleteTip(saved.getId());
        assertThrows(IllegalArgumentException.class, () -> tipService.getTipById(saved.getId()));
    }

    @Test
    void testDiscussionQuestionAndReplies() {
        DiscussionQuestion q = new DiscussionQuestion(
                "Why are my succulent leaves turning translucent and mushy?",
                "Plant Diseases",
                "My Echeveria leaves became yellow, watery and soft after watering last week.",
                "DesertLover",
                "Echeveria Succulent",
                null
        );
        DiscussionQuestion savedQ = discussionService.saveQuestion(q);
        assertNotNull(savedQ.getId());
        assertFalse(savedQ.isResolved());

        // Add reply
        DiscussionReply reply = new DiscussionReply("SucculentPro", "Stop watering immediately! Translucent mushy leaves mean cell rupture from severe overwatering.", true);
        DiscussionReply savedReply = discussionService.addReply(savedQ.getId(), reply);
        assertNotNull(savedReply.getId());

        // Toggle resolved
        discussionService.toggleResolved(savedQ.getId());
        DiscussionQuestion resolvedQ = discussionService.getQuestionById(savedQ.getId());
        assertTrue(resolvedQ.isResolved());

        // Clean up
        discussionService.deleteQuestion(savedQ.getId());
    }

    @Test
    void testGardenProjectLifecycle() {
        GardenProject project = new GardenProject(
                "Terracotta Window Box Basil",
                "Sowing Sweet Basil seeds on kitchen windowsill",
                "Sweet Basil",
                "Indoor Windowsill",
                "Planning",
                LocalDate.now(),
                LocalDate.now().plusMonths(2),
                "Seeds planted in seed starting mix.",
                null,
                "Chef Garden"
        );
        GardenProject saved = projectService.saveProject(project);
        assertNotNull(saved.getId());
        assertEquals("Planning", saved.getStatus());

        saved.setStatus("In Progress");
        projectService.updateProject(saved.getId(), saved);
        assertEquals("In Progress", projectService.getProjectById(saved.getId()).getStatus());

        List<GardenProject> projects = projectService.getAllProjects(null, null);
        assertTrue(projects.size() > 0);

        projectService.deleteProject(saved.getId());
    }

    @Test
    void testFloatingSidebarRendersOnHomePage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("gardenFloatingSidebar")))
                .andExpect(content().string(containsString("sidebar-nav-container")))
                .andExpect(content().string(containsString("sidebar-share-btn")))
                .andExpect(content().string(containsString("Share Your Garden")))
                .andExpect(content().string(containsString("mobileNavToggle")))
                .andExpect(content().string(containsString("mobileNavMenu")))
                .andExpect(content().string(containsString("/js/navbar.js")))
                .andExpect(content().string(containsString("Home")))
                .andExpect(content().string(containsString("Gardening Tips")))
                .andExpect(content().string(containsString("Discussions")))
                .andExpect(content().string(containsString("Projects")))
                .andExpect(content().string(containsString("About")))
                .andExpect(content().string(containsString("sidebarPinToggleBtn")))
                .andExpect(content().string(containsString("sidebarPinIcon")))
                .andExpect(content().string(containsString("sidebar-expand-text")))
                // Ensure obsolete wide top navbar container and obsolete rail are absent
                .andExpect(content().string(not(containsString("gardenNavbarWrapper"))))
                .andExpect(content().string(not(containsString("sidebarDesktop"))));
    }

    @Test
    void testFloatingSidebarRendersOnTipsPage() throws Exception {
        mockMvc.perform(get("/tips"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("gardenFloatingSidebar")))
                .andExpect(content().string(containsString("Share Your Garden")))
                .andExpect(content().string(containsString("Gardening Tips")));
    }

    @Test
    void testFloatingSidebarRendersOnDiscussionsPage() throws Exception {
        mockMvc.perform(get("/discussions"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("gardenFloatingSidebar")))
                .andExpect(content().string(containsString("Share Your Garden")))
                .andExpect(content().string(containsString("Discussions")));
    }

    @Test
    void testFloatingSidebarRendersOnProjectsPage() throws Exception {
        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("gardenFloatingSidebar")))
                .andExpect(content().string(containsString("Share Your Garden")))
                .andExpect(content().string(containsString("Projects")));
    }

    @Test
    void testFloatingSidebarRendersOnAboutPage() throws Exception {
        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("gardenFloatingSidebar")))
                .andExpect(content().string(containsString("Share Your Garden")))
                .andExpect(content().string(containsString("About")));
    }

    @Test
    void testEditorialHeroAndFeatureSectionsRenderOnHomePage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("hero-editorial-section")))
                .andExpect(content().string(containsString("Cultivate Knowledge")))
                .andExpect(content().string(containsString("hero-metrics-strip")))
                .andExpect(content().string(containsString("editorial-feature-card")))
                .andExpect(content().string(containsString("editorial-tip-card")))
                // Ensure heavy rectangular opaque hero banner is removed
                .andExpect(content().string(not(containsString("hero-banner"))))
                .andExpect(content().string(not(containsString("hero-stats-badge"))));
    }

    @Test
    void testCloudShaderIntegratedGloballyAcrossWebsite() throws Exception {
        // Home page has global cloud canvas and script
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("globalCloudContainer")))
                .andExpect(content().string(containsString("heroCloudCanvas")))
                .andExpect(content().string(containsString("global-cloud-scrim")))
                .andExpect(content().string(containsString("/js/cloud-shader.js")));

        // Tips page has global cloud canvas and script
        mockMvc.perform(get("/tips"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("globalCloudContainer")))
                .andExpect(content().string(containsString("heroCloudCanvas")))
                .andExpect(content().string(containsString("/js/cloud-shader.js")));

        // Discussions page has global cloud canvas and script
        mockMvc.perform(get("/discussions"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("globalCloudContainer")))
                .andExpect(content().string(containsString("heroCloudCanvas")))
                .andExpect(content().string(containsString("/js/cloud-shader.js")));

        // Projects page has global cloud canvas and script
        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("globalCloudContainer")))
                .andExpect(content().string(containsString("heroCloudCanvas")))
                .andExpect(content().string(containsString("/js/cloud-shader.js")));

        // About page has global cloud canvas and script
        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("globalCloudContainer")))
                .andExpect(content().string(containsString("heroCloudCanvas")))
                .andExpect(content().string(containsString("/js/cloud-shader.js")));
    }
}
