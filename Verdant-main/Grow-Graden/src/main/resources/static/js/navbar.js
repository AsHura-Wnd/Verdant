/**
 * Verdant Community - Aceternity-Inspired Navigation Controller
 * Handles Collapsible Hover/Pin Desktop Sidebar and Mobile Navigation Drawer
 * Pure Vanilla JavaScript (No React / No Next.js / No external dependencies)
 */
document.addEventListener('DOMContentLoaded', function () {
    'use strict';

    // =========================================================================
    // 1. Desktop Collapsible Floating Sidebar (Hover & Pin Behavior)
    // =========================================================================
    const desktopSidebar = document.getElementById('gardenFloatingSidebar');
    const pinToggleBtn = document.getElementById('sidebarPinToggleBtn');
    const pinIcon = document.getElementById('sidebarPinIcon');
    const STORAGE_KEY = 'verdant_sidebar_pinned';

    if (desktopSidebar) {
        const isPinned = localStorage.getItem(STORAGE_KEY) === 'true';

        if (isPinned) {
            desktopSidebar.classList.add('is-pinned');
            document.body.classList.add('sidebar-pinned');
            desktopSidebar.setAttribute('aria-expanded', 'true');
            if (pinIcon) {
                pinIcon.classList.remove('bi-pin-angle');
                pinIcon.classList.add('bi-pin-angle-fill');
            }
            if (pinToggleBtn) {
                pinToggleBtn.setAttribute('title', 'Unpin sidebar');
                pinToggleBtn.setAttribute('aria-pressed', 'true');
            }
        }

        let hoverTimeout = null;

        // Expand on mouse hover when not pinned
        desktopSidebar.addEventListener('mouseenter', function () {
            if (hoverTimeout) {
                clearTimeout(hoverTimeout);
                hoverTimeout = null;
            }
            if (!desktopSidebar.classList.contains('is-pinned')) {
                desktopSidebar.classList.add('is-hovered');
                document.body.classList.add('sidebar-hovered');
                desktopSidebar.setAttribute('aria-expanded', 'true');
            }
        });

        // Collapse on mouse leave when not pinned (debounced to eliminate edge flicker)
        desktopSidebar.addEventListener('mouseleave', function () {
            if (!desktopSidebar.classList.contains('is-pinned')) {
                if (hoverTimeout) clearTimeout(hoverTimeout);
                hoverTimeout = setTimeout(function () {
                    desktopSidebar.classList.remove('is-hovered');
                    document.body.classList.remove('sidebar-hovered');
                    desktopSidebar.setAttribute('aria-expanded', 'false');
                }, 60);
            }
        });

        // Accessibility: Expand on keyboard focus inside sidebar
        desktopSidebar.addEventListener('focusin', function () {
            if (!desktopSidebar.classList.contains('is-pinned')) {
                desktopSidebar.classList.add('is-hovered');
                document.body.classList.add('sidebar-hovered');
                desktopSidebar.setAttribute('aria-expanded', 'true');
            }
        });

        desktopSidebar.addEventListener('focusout', function (e) {
            if (!desktopSidebar.classList.contains('is-pinned')) {
                // If focus moves outside the sidebar, collapse
                if (!desktopSidebar.contains(e.relatedTarget)) {
                    desktopSidebar.classList.remove('is-hovered');
                    document.body.classList.remove('sidebar-hovered');
                    desktopSidebar.setAttribute('aria-expanded', 'false');
                }
            }
        });

        // Pin/Unpin button toggle
        if (pinToggleBtn) {
            pinToggleBtn.addEventListener('click', function (e) {
                e.preventDefault();
                e.stopPropagation();

                const nowPinned = desktopSidebar.classList.toggle('is-pinned');
                desktopSidebar.classList.remove('is-hovered');
                document.body.classList.remove('sidebar-hovered');
                document.body.classList.toggle('sidebar-pinned', nowPinned);
                localStorage.setItem(STORAGE_KEY, nowPinned ? 'true' : 'false');
                desktopSidebar.setAttribute('aria-expanded', nowPinned ? 'true' : 'false');

                if (pinIcon) {
                    if (nowPinned) {
                        pinIcon.classList.remove('bi-pin-angle');
                        pinIcon.classList.add('bi-pin-angle-fill');
                        pinToggleBtn.setAttribute('title', 'Unpin sidebar');
                        pinToggleBtn.setAttribute('aria-pressed', 'true');
                    } else {
                        pinIcon.classList.remove('bi-pin-angle-fill');
                        pinIcon.classList.add('bi-pin-angle');
                        pinToggleBtn.setAttribute('title', 'Pin sidebar open');
                        pinToggleBtn.setAttribute('aria-pressed', 'false');
                    }
                }
            });
        }
    }

    // =========================================================================
    // 2. Mobile Menu Drawer Navigation (< 992px)
    // =========================================================================
    const mobileToggle = document.getElementById('mobileNavToggle');
    const mobileMenu = document.getElementById('mobileNavMenu');
    const mobileBackdrop = document.getElementById('mobileNavBackdrop');
    const mobileCloseBtn = document.getElementById('mobileNavCloseBtn');

    function openMobileMenu() {
        if (!mobileMenu) return;
        mobileMenu.classList.add('is-open');
        mobileMenu.setAttribute('aria-hidden', 'false');

        if (mobileToggle) {
            mobileToggle.setAttribute('aria-expanded', 'true');
            mobileToggle.classList.add('is-active');
        }

        if (mobileBackdrop) {
            mobileBackdrop.classList.add('is-visible');
            mobileBackdrop.setAttribute('aria-hidden', 'false');
        }

        document.body.classList.add('navbar-mobile-open');

        // Focus close or first link in menu for accessibility
        const firstFocusable = mobileMenu.querySelector('a, button');
        if (firstFocusable) {
            setTimeout(function () {
                firstFocusable.focus();
            }, 100);
        }
    }

    function closeMobileMenu() {
        if (!mobileMenu) return;
        mobileMenu.classList.remove('is-open');
        mobileMenu.setAttribute('aria-hidden', 'true');

        if (mobileToggle) {
            mobileToggle.setAttribute('aria-expanded', 'false');
            mobileToggle.classList.remove('is-active');
            mobileToggle.focus();
        }

        if (mobileBackdrop) {
            mobileBackdrop.classList.remove('is-visible');
            mobileBackdrop.setAttribute('aria-hidden', 'true');
        }

        document.body.classList.remove('navbar-mobile-open');
    }

    if (mobileToggle) {
        mobileToggle.addEventListener('click', function (e) {
            e.preventDefault();
            e.stopPropagation();
            if (mobileMenu && mobileMenu.classList.contains('is-open')) {
                closeMobileMenu();
            } else {
                openMobileMenu();
            }
        });
    }

    if (mobileCloseBtn) {
        mobileCloseBtn.addEventListener('click', function (e) {
            e.preventDefault();
            closeMobileMenu();
        });
    }

    if (mobileBackdrop) {
        mobileBackdrop.addEventListener('click', function () {
            closeMobileMenu();
        });
    }

    // Close mobile menu on Escape key
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape' || e.key === 'Esc') {
            if (mobileMenu && mobileMenu.classList.contains('is-open')) {
                closeMobileMenu();
            }
        }
    });

    // Close mobile menu on window resize if crossing to desktop
    window.addEventListener('resize', function () {
        if (window.innerWidth >= 992) {
            if (mobileMenu && mobileMenu.classList.contains('is-open')) {
                closeMobileMenu();
            }
        }
    }, { passive: true });
});
