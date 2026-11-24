package com.example.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service to enforce content ownership and role-based moderation.
 * Guarantees that:
 * - Content creators can modify and delete their own content.
 * - Platform moderators (ROLE_ADMIN) can edit or remove any content for community health.
 * - Unauthorized users and unauthenticated visitors cannot tamper with or delete community content.
 */
@Service
public class OwnershipService {

    public boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal());
    }

    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        return auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return null;
    }

    public boolean canModify(String authorUsername) {
        if (!isAuthenticated()) {
            return false;
        }
        if (isAdmin()) {
            return true;
        }
        String current = getCurrentUsername();
        return current != null && authorUsername != null && current.equalsIgnoreCase(authorUsername.trim());
    }

    public void verifyCanModify(String authorUsername, String resourceName) {
        if (!canModify(authorUsername)) {
            throw new AccessDeniedException(
                    "Access Denied: You do not have permission to modify or delete this " + resourceName + "."
            );
        }
    }
}
