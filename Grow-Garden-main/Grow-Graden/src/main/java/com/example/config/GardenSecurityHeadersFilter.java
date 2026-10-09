package com.example.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom Jakarta Servlet Filter demonstrating explicit Java Servlet API Integration.
 * Intercepts incoming HTTP requests, records request execution duration, and attaches
 * security and platform diagnostic response headers:
 * - X-Content-Type-Options: nosniff
 * - X-Frame-Options: SAMEORIGIN
 * - X-Application-Name: GrowGarden Botanical Collective
 * - X-Response-Time-Ms: <duration>
 */
@Component
public class GardenSecurityHeadersFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Servlet filter lifecycle initialization
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        long startTime = System.currentTimeMillis();

        if (response instanceof HttpServletResponse httpResponse) {
            // Attach platform security and identity headers
            httpResponse.setHeader("X-Content-Type-Options", "nosniff");
            httpResponse.setHeader("X-Frame-Options", "SAMEORIGIN");
            httpResponse.setHeader("X-Application-Name", "GrowGarden Botanical Collective");
            httpResponse.setHeader("X-Servlet-Filter", "GardenSecurityHeadersFilter");
        }

        try {
            // Propagate request through the servlet filter chain
            chain.doFilter(request, response);
        } finally {
            if (response instanceof HttpServletResponse httpResponse) {
                long duration = Math.max(1, System.currentTimeMillis() - startTime);
                httpResponse.setHeader("X-Response-Time-Ms", String.valueOf(duration));
            }
        }
    }

    @Override
    public void destroy() {
        // Servlet filter lifecycle destruction
    }
}
