package com.example.config;

import com.example.exception.ErrorResponse;
import com.example.security.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

/**
 * Enterprise Spring Security 6 Configuration for Verdant Botanical Collective.
 * Features:
 * - BCrypt Password Hashing
 * - Custom database-backed UserDetailsService
 * - Role-Based Access Control (ROLE_USER, ROLE_ADMIN)
 * - Fine-grained endpoint authorization distinguishing public reading from authenticated authorship
 * - Dedicated H2 console security restricted to ROLE_ADMIN with safe sameOrigin framing
 * - Graceful JSON vs HTML error responses for unauthorized/forbidden requests
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            String requestUri = request.getRequestURI();
            if (requestUri.startsWith("/api/")) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                ErrorResponse error = new ErrorResponse(
                        HttpStatus.FORBIDDEN.value(),
                        "Forbidden",
                        "Access Denied: You do not have permission to access or modify this resource.",
                        requestUri
                );
                objectMapper.writeValue(response.getOutputStream(), error);
            } else {
                request.getSession().setAttribute("errorMessage", "Access Denied: You do not have permission to perform this action.");
                response.sendRedirect(request.getContextPath() + "/?forbidden=true");
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .csrf(csrf -> csrf
                // Expose stateless REST endpoints and local H2 console without CSRF token
                .ignoringRequestMatchers("/api/**", "/h2-console/**")
            )
            .headers(headers -> headers
                // Permit sameOrigin frames strictly for the H2 database console UI
                .frameOptions(frame -> frame.sameOrigin())
            )
            .authorizeHttpRequests(auth -> auth
                // Static assets (CSS, JavaScript, WebGL noise textures, images, favicon)
                .requestMatchers("/css/**", "/js/**", "/img/**", "/favicon.ico", "/error").permitAll()

                // Public browsing endpoints (visitors can freely explore the botanical collective)
                .requestMatchers(HttpMethod.GET, "/", "/about").permitAll()
                .requestMatchers(HttpMethod.GET, "/tips", "/tips/{id:[0-9]+}").permitAll()
                .requestMatchers(HttpMethod.GET, "/discussions", "/discussions/{id:[0-9]+}").permitAll()
                .requestMatchers(HttpMethod.GET, "/projects", "/projects/{id:[0-9]+}").permitAll()

                // Public platform health and REST APIs
                .requestMatchers(HttpMethod.GET, "/api/health", "/api/stats", "/api/jdbc/**", "/api/advisor/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/tips/**", "/api/discussions/**", "/api/projects/**").permitAll()

                // Public login and self-registration
                .requestMatchers("/login", "/register").permitAll()

                // H2 Database Console - strictly guarded, requires administrative credentials
                .requestMatchers("/h2-console/**").hasRole("ADMIN")

                // All content publication, modification, deletion, likes, and replies require authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", false)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedHandler(customAccessDeniedHandler())
                .defaultAuthenticationEntryPointFor(
                    new Http403ForbiddenEntryPoint(),
                    request -> request.getRequestURI().startsWith("/api/")
                )
                .authenticationEntryPoint(new org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint("/login"))
            );

        return http.build();
    }
}
