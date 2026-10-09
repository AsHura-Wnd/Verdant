package com.example;

import com.example.domain.advisor.AbstractPlantAdvisor;
import com.example.domain.advisor.CarePlan;
import com.example.domain.advisor.DiagnosticResult;
import com.example.domain.advisor.PlantAdvisorRegistry;
import com.example.domain.advisor.SucculentAdvisor;
import com.example.domain.advisor.VegetableAdvisor;
import com.example.exception.GardeningTipNotFoundException;
import com.example.exception.ResourceNotFoundException;
import com.example.model.DiscussionQuestion;
import com.example.model.DiscussionReply;
import com.example.model.GardenProject;
import com.example.model.GardeningTip;
import com.example.repository.jdbc.CategoryMetric;
import com.example.repository.jdbc.GardenJdbcDao;
import com.example.repository.jdbc.JdbcTipSummary;
import com.example.repository.jdbc.ProjectStatusMetric;
import com.example.service.DiscussionService;
import com.example.service.GardenProjectService;
import com.example.service.GardeningTipService;
import com.example.util.SearchResultWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Autowired
    private GardenJdbcDao gardenJdbcDao;

    @Autowired
    private PlantAdvisorRegistry advisorRegistry;

    @Test
    void contextLoads() {
        assertNotNull(tipService);
        assertNotNull(discussionService);
        assertNotNull(projectService);
        assertNotNull(gardenJdbcDao);
        assertNotNull(advisorRegistry);
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
        assertThrows(ResourceNotFoundException.class, () -> tipService.getTipById(saved.getId()));
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

    // =========================================================================
    // Academic Compliance Verification Tests: JDBC, OOP, Generics & Servlets
    // =========================================================================

    @Test
    void testDirectJdbcDaoCategoryMetricsAndAggregateQueries() {
        // Direct JDBC invocation using java.sql.Connection, PreparedStatement & ResultSet
        List<CategoryMetric> metrics = gardenJdbcDao.getCategoryMetrics();
        assertNotNull(metrics, "JDBC Category metrics should not be null");
        assertFalse(metrics.isEmpty(), "JDBC Category metrics should contain aggregated category data");

        CategoryMetric first = metrics.get(0);
        assertNotNull(first.getCategory());
        assertTrue(first.getTipCount() > 0, "Tip count should be positive");
        assertTrue(first.getTotalLikes() >= 0, "Likes should be non-negative");

        long directCount = gardenJdbcDao.getTipCountDirect();
        assertTrue(directCount > 0, "Direct JDBC scalar count should match seeded tips");

        List<ProjectStatusMetric> projectMetrics = gardenJdbcDao.getProjectStatusMetrics();
        assertNotNull(projectMetrics, "JDBC Project status metrics should not be null");
        assertFalse(projectMetrics.isEmpty(), "JDBC Project metrics should contain status aggregates");
    }

    @Test
    void testDirectJdbcParameterizedSearch() {
        // Direct JDBC parameterized PreparedStatement search avoiding SQL injection
        List<JdbcTipSummary> results = gardenJdbcDao.searchTipsJdbc("Aphid", null);
        assertNotNull(results);
        assertFalse(results.isEmpty(), "Should find neem oil aphid tip via direct JDBC");
        assertEquals("Pest & Disease Control", results.get(0).getCategory());

        // Parameterized search with difficulty filter
        List<JdbcTipSummary> beginnerResults = gardenJdbcDao.searchTipsJdbc(null, "Beginner");
        assertNotNull(beginnerResults);
        for (JdbcTipSummary tip : beginnerResults) {
            assertEquals("Beginner", tip.getDifficultyLevel());
        }
    }

    @Test
    void testOopInheritanceAndPolymorphicAdvisors() {
        // Verify class inheritance: all specialized advisors extend AbstractPlantAdvisor
        AbstractPlantAdvisor vegAdvisor = new VegetableAdvisor();
        AbstractPlantAdvisor succAdvisor = new SucculentAdvisor();

        assertTrue(vegAdvisor instanceof AbstractPlantAdvisor, "VegetableAdvisor must extend AbstractPlantAdvisor");
        assertTrue(succAdvisor instanceof AbstractPlantAdvisor, "SucculentAdvisor must extend AbstractPlantAdvisor");

        // Verify template method inheritance
        assertTrue(vegAdvisor.getAdvisorProfile().contains("Certified Horticultural Advisory Module"));

        // Verify runtime polymorphism & method overriding: distinct watering intervals
        int vegWatering = vegAdvisor.calculateWateringIntervalDays(30);
        int succWatering = succAdvisor.calculateWateringIntervalDays(30);
        assertNotEquals(vegWatering, succWatering, "Polymorphic method must produce different behaviors");
        assertTrue(vegWatering < succWatering, "Vegetables require more frequent watering than succulents");

        // Verify polymorphic dynamic diagnosis
        DiagnosticResult succDiag = succAdvisor.diagnoseSymptom("yellow translucent mushy leaves");
        assertEquals("Critical", succDiag.getSeverityLevel());
        assertTrue(succDiag.getSuspectedCause().contains("Overwatering"));

        DiagnosticResult vegDiag = vegAdvisor.diagnoseSymptom("black rot on bottom of tomato");
        assertEquals("High", vegDiag.getSeverityLevel());
        assertTrue(vegDiag.getSuspectedCause().contains("Blossom-End Rot"));

        // Verify advisor factory / registry resolution
        AbstractPlantAdvisor resolvedTomato = advisorRegistry.resolveAdvisor("San Marzano Tomato");
        assertTrue(resolvedTomato instanceof VegetableAdvisor);

        AbstractPlantAdvisor resolvedSucculent = advisorRegistry.resolveAdvisor("Echeveria Succulent");
        assertTrue(resolvedSucculent instanceof SucculentAdvisor);
    }

    @Test
    void testCustomExceptionHierarchyAndRestGlobalHandling() throws Exception {
        // Verify exception inheritance
        GardeningTipNotFoundException tipEx = new GardeningTipNotFoundException(99999L);
        assertTrue(tipEx instanceof ResourceNotFoundException, "GardeningTipNotFoundException must inherit from ResourceNotFoundException");
        assertTrue(tipEx instanceof RuntimeException, "Must be an unchecked RuntimeException");
        assertEquals("Gardening Tip", tipEx.getResourceName());
        assertEquals(99999L, tipEx.getResourceId());

        // Verify REST API 404 translation via @ControllerAdvice
        mockMvc.perform(get("/api/tips/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value(containsString("99999")))
                .andExpect(jsonPath("$.path").value("/api/tips/99999"));
    }

    @Test
    void testGenericSearchResultWrapper() {
        // Verify student-written generic container SearchResultWrapper<T>
        List<String> items = List.of("Monstera", "Basil", "Petunia");
        SearchResultWrapper<String> wrapper = SearchResultWrapper.of(items, "garden");

        assertFalse(wrapper.isEmpty());
        assertEquals(3, wrapper.getTotalCount());
        assertEquals("garden", wrapper.getQueryFilter());

        // Verify generic type transformation map(Function<T, R>)
        SearchResultWrapper<Integer> lengthWrapper = wrapper.map(String::length);
        assertEquals(3, lengthWrapper.getTotalCount());
        assertEquals(List.of(8, 5, 7), lengthWrapper.getItems());
        assertTrue((Boolean) lengthWrapper.getMetadata().get("hasResults"));
    }

    @Test
    void testJakartaServletFilterResponseHeaders() throws Exception {
        // Verify custom Jakarta Servlet Filter executes on web requests and REST APIs
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Application-Name", "GrowGarden Botanical Collective"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("X-Frame-Options", "SAMEORIGIN"))
                .andExpect(header().string("X-Servlet-Filter", "GardenSecurityHeadersFilter"));

        mockMvc.perform(get("/api/stats"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Application-Name", "GrowGarden Botanical Collective"))
                .andExpect(header().exists("X-Response-Time-Ms"));
    }

    @Test
    void testJdbcAndAdvisorRestEndpoints() throws Exception {
        // Direct JDBC metrics endpoint
        mockMvc.perform(get("/api/jdbc/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.provider").value(containsString("Direct JDBC")))
                .andExpect(jsonPath("$.directTipCount").isNumber())
                .andExpect(jsonPath("$.categoryAnalytics").isArray())
                .andExpect(jsonPath("$.projectStatusBreakdown").isArray());

        // Direct JDBC parameterized search endpoint
        mockMvc.perform(get("/api/jdbc/tips/search").param("keyword", "Neem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.totalCount").isNumber());

        // Polymorphic advisor diagnosis endpoint
        mockMvc.perform(get("/api/advisor/diagnose")
                        .param("plantType", "Tomato")
                        .param("symptom", "yellow leaves"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.suspectedCause").value(containsString("Aphid")))
                .andExpect(jsonPath("$.organicSolution").isString());

        // Polymorphic care plan endpoint
        mockMvc.perform(get("/api/advisor/care-plan")
                        .param("plantType", "Succulent")
                        .param("spaceType", "Indoor Windowsill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plantGroup").value("Succulents & Cacti"))
                .andExpect(jsonPath("$.wateringIntervalDays").value(14));
    }
}
