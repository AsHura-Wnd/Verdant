/**
 * Verdant Community - Scroll-Driven Garden Reveal Controller
 * Scene A: Sky & Clouds (Hero) -> Scene B: Emerging Canopy -> Scene C: Living Garden
 *
 * Smoothly calculates scroll progress and passes lightweight CSS custom properties
 * to GPU-accelerated backdrop layers without touching cloud-shader.js.
 */
(function () {
    'use strict';

    function initGardenReveal() {
        const container = document.getElementById('scrollGardenReveal');
        if (!container) return;

        // Respect prefers-reduced-motion accessibility preference
        const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
        if (prefersReducedMotion) {
            container.classList.add('reduced-motion');
            return;
        }

        let ticking = false;

        function updateGardenReveal() {
            const scrollY = window.pageYOffset || document.documentElement.scrollTop || 0;

            // Scene A -> Scene B transition begins as user scrolls past the top of the hero
            // Scene B (100px - 750px): Treetops and layered foliage emerge with differential parallax
            // Scene C (750px+): Immersive rich garden environment becomes dominant
            const revealProgress = Math.min(1.0, Math.max(0.0, (scrollY - 60) / 650));
            const deepGardenProgress = Math.min(1.0, Math.max(0.0, (scrollY - 450) / 800));

            // Parallax rates: distant moves slow, midground moderate, foreground leaves fast
            const parallaxDistant = scrollY * 0.12;
            const parallaxMidground = scrollY * 0.22;
            const parallaxForeground = scrollY * 0.36;

            const rootStyle = document.documentElement.style;
            rootStyle.setProperty('--garden-reveal-opacity', revealProgress.toFixed(3));
            rootStyle.setProperty('--garden-deep-opacity', deepGardenProgress.toFixed(3));
            rootStyle.setProperty('--garden-p-distant', parallaxDistant.toFixed(1) + 'px');
            rootStyle.setProperty('--garden-p-mid', parallaxMidground.toFixed(1) + 'px');
            rootStyle.setProperty('--garden-p-fore', parallaxForeground.toFixed(1) + 'px');

            ticking = false;
        }

        window.addEventListener('scroll', function () {
            if (!ticking) {
                window.requestAnimationFrame(updateGardenReveal);
                ticking = true;
            }
        }, { passive: true });

        // Initial update
        updateGardenReveal();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initGardenReveal);
    } else {
        initGardenReveal();
    }
})();
