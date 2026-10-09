/**
 * GrowGarden Community - Aceternity-Inspired CloudShader Component
 * Pure Vanilla JavaScript WebGL Implementation (No React / No Next.js / No Tailwind / No external dependencies)
 *
 * Restrained botanical palette: Sage green, soft mint, cream, and subtle sky-blue highlights.
 * Rendered across the entire website as a full-viewport background with smooth, slow cloud movement.
 */
(function (global) {
    'use strict';

    // GLSL Vertex Shader: Fullscreen triangle
    const VERTEX_SHADER_SOURCE = `
attribute vec2 a_pos;
varying vec2 v_uv;
void main() {
    v_uv = a_pos * 0.5 + 0.5;
    gl_Position = vec4(a_pos, 0.0, 1.0);
}
`;

    // GLSL Fragment Shader: Procedural multi-layer clouds, domain warping, billow noise, and subtle sun illumination
    const FRAGMENT_SHADER_SOURCE = `
#ifdef GL_FRAGMENT_PRECISION_HIGH
precision highp float;
#else
precision mediump float;
#endif

varying vec2 v_uv;

uniform vec2 u_res;
uniform float u_time;
uniform float u_count;
uniform vec3 u_cloud;
uniform vec3 u_skyTop;
uniform vec3 u_skyBottom;

const mat2 R = mat2(0.80, 0.60, -0.60, 0.80);

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(41.31, 289.17))) * 26737.367);
}

float vnoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

float fbm(vec2 p) {
    float sum = 0.0;
    float amp = 0.5;
    for (int i = 0; i < 4; i++) {
        sum += amp * vnoise(p);
        p = R * p * 2.03 + 19.19;
        amp *= 0.5;
    }
    return sum;
}

// Billow noise: sharp puffy ridges for cauliflower cloud tops
float billow(vec2 p) {
    float sum = 0.0;
    float amp = 0.5;
    for (int i = 0; i < 5; i++) {
        sum += amp * (1.0 - abs(2.0 * vnoise(p) - 1.0));
        p = R * p * 2.11 + 13.37;
        amp *= 0.5;
    }
    return sum;
}

// Density for one cloud at point p
float cloudDensity(vec2 p, vec2 c, vec2 r, float seed, float t) {
    vec2 q = p - c;

    // Dome above center, flat base below
    float ry = q.y > 0.0 ? r.y : r.y * 0.42;
    float env = 1.0 - length(vec2(q.x / r.x, q.y / ry));
    if (env < -0.35) return 0.0;

    // Domain-warped billow detail, moves with cloud and evolves slowly
    vec2 dp = q * (2.4 / r.x) + seed;
    dp += 0.6 * vec2(
        fbm(dp * 1.4 + t * 0.04),
        fbm(dp * 1.4 + 7.7 - t * 0.03)
    );
    float detail = billow(dp * 1.6);

    return env + (detail - 0.62) * 0.62;
}

// Shades one cloud and blends it over background color with sun self-shadowing
vec3 shadeCloud(vec3 color, vec3 sky, vec2 p, vec2 c, vec2 r, float seed, float t, float dist) {
    float d = cloudDensity(p, c, r, seed, t);
    if (d < 0.02) return color;

    // Sample density toward sun (straight up) for self-shadowing
    float dUp = cloudDensity(p + vec2(0.0, r.y * 0.55), c, r, seed, t);
    float occl = clamp((dUp - d) * 1.1 + d * 0.55, 0.0, 1.0);

    vec3 lit = u_cloud * 1.02;
    vec3 shadow = mix(u_cloud * 0.62, sky, 0.35);
    vec3 cloudCol = mix(lit, shadow, occl * 0.85);

    float alpha = smoothstep(0.02, 0.38, d);

    // Subtle silver lining on thin cloud edges
    float rim = smoothstep(0.02, 0.14, d) * (1.0 - smoothstep(0.14, 0.40, d));
    cloudCol += rim * 0.08;

    // Atmospheric perspective: distant clouds gently fade into the sky
    cloudCol = mix(cloudCol, sky, dist * 0.35);
    alpha *= mix(1.0, 0.8, dist);

    return mix(color, cloudCol, alpha);
}

// One drifting cloud: horizontal wrap with gentle vertical bob
vec3 cloudPass(vec3 color, vec3 sky, vec2 p, float aspect, float t,
               float spd, float phase, float y, vec2 r, float seed, float dist) {
    float cx = mix(-r.x - 0.25, aspect + r.x + 0.25, fract(t * spd + phase));
    float cy = y + sin(t * 0.05 + phase * 6.2831) * 0.012;
    return shadeCloud(color, sky, p, vec2(cx, cy), r, seed, t, dist);
}

void main() {
    float aspect = u_res.x / u_res.y;
    vec2 p = vec2(v_uv.x * aspect, v_uv.y);
    float t = u_time;

    vec3 sky = mix(u_skyBottom, u_skyTop, v_uv.y);
    vec3 color = sky;

    // Faint atmospheric haze near the horizon
    color = mix(color, u_skyBottom * 1.04, smoothstep(0.35, 0.0, v_uv.y) * 0.45);

    // Subtle, low-intensity botanical sunlight glow in upper sky
    vec2 sunPos = vec2(aspect * 0.78, 0.92);
    float sunDist = length(p - sunPos);
    color += vec3(1.0, 0.96, 0.85) * exp(-sunDist * sunDist * 5.0) * 0.16;

    // Thin cirrus streaks high in the atmosphere
    float cirrusBand = smoothstep(0.55, 0.8, v_uv.y) * (1.0 - smoothstep(0.9, 1.0, v_uv.y));
    if (cirrusBand > 0.01) {
        float streak = fbm(vec2(p.x * 1.6 - t * 0.006, p.y * 12.0));
        float wisp = smoothstep(0.52, 0.78, streak) * cirrusBand;
        color = mix(color, u_cloud * 0.98, wisp * 0.30);
    }

    // Far cloud layer: small, high, gentle drift
    if (u_count > 5.5) {
        color = cloudPass(color, sky, p, aspect, t, 0.006, 0.10, 0.84, vec2(0.20, 0.10), 43.7, 1.0);
    }
    if (u_count > 4.5) {
        color = cloudPass(color, sky, p, aspect, t, 0.008, 0.62, 0.73, vec2(0.24, 0.12), 71.3, 0.85);
    }

    // Middle cloud layer: medium altitude and drift
    if (u_count > 3.5) {
        color = cloudPass(color, sky, p, aspect, t, 0.011, 0.33, 0.60, vec2(0.34, 0.16), 17.3, 0.55);
    }
    if (u_count > 2.5) {
        color = cloudPass(color, sky, p, aspect, t, 0.013, 0.80, 0.47, vec2(0.30, 0.15), 29.9, 0.45);
    }

    // Near cloud layer: low altitude, large volume, smooth drift
    if (u_count > 1.5) {
        color = cloudPass(color, sky, p, aspect, t, 0.016, 0.05, 0.35, vec2(0.46, 0.20), 91.1, 0.15);
    }
    color = cloudPass(color, sky, p, aspect, t, 0.020, 0.48, 0.20, vec2(0.56, 0.24), 57.2, 0.0);

    gl_FragColor = vec4(color, 1.0);
}
`;

    /**
     * Parses Hex or RGB string into [r, g, b] floats in range [0, 1]
     */
    function parseColor(colorStr) {
        if (!colorStr) return [0.98, 0.98, 0.96];
        const val = String(colorStr).trim();
        if (val.startsWith('#')) {
            const hex = val.slice(1);
            if (hex.length === 3) {
                return [
                    parseInt(hex[0] + hex[0], 16) / 255,
                    parseInt(hex[1] + hex[1], 16) / 255,
                    parseInt(hex[2] + hex[2], 16) / 255,
                ];
            }
            return [
                parseInt(hex.slice(0, 2), 16) / 255,
                parseInt(hex.slice(2, 4), 16) / 255,
                parseInt(hex.slice(4, 6), 16) / 255,
            ];
        }
        const rgb = val.match(/[\d.]+/g);
        if (rgb && rgb.length >= 3) {
            return [Number(rgb[0]) / 255, Number(rgb[1]) / 255, Number(rgb[2]) / 255];
        }
        return [0.98, 0.98, 0.96];
    }

    /**
     * Compiles a WebGL shader
     */
    function compileShader(gl, type, source) {
        const shader = gl.createShader(type);
        if (!shader) return null;
        gl.shaderSource(shader, source);
        gl.compileShader(shader);
        if (!gl.getShaderParameter(shader, gl.COMPILE_STATUS)) {
            console.warn('CloudShader: Shader compile error:', gl.getShaderInfoLog(shader));
            gl.deleteShader(shader);
            return null;
        }
        return shader;
    }

    /**
     * CloudShader Controller Constructor
     */
    function CloudShader(options) {
        options = options || {};

        // Restrained botanical color palette and settings:
        // Sage green, soft mint, cream, and subtle sky-blue highlights
        this.options = {
            canvas: options.canvas || document.getElementById('heroCloudCanvas'),
            container: options.container || document.getElementById('globalCloudContainer') || document.getElementById('heroCloudContainer'),
            speed: typeof options.speed === 'number' ? options.speed : 0.35,
            count: typeof options.count === 'number' ? options.count : 4.5,
            cloudColor: options.cloudColor || '#faf8f2',         // Cream cloud highlight
            skyTopColor: options.skyTopColor || '#3f6b5b',       // Restrained deep sage green with sky tone
            skyBottomColor: options.skyBottomColor || '#a2d2c2', // Soft mint with subtle sky-blue undertone
            maxDpr: typeof options.maxDpr === 'number' ? options.maxDpr : 1.5,
        };

        this.canvas = typeof this.options.canvas === 'string'
            ? document.getElementById(this.options.canvas)
            : this.options.canvas;

        this.container = typeof this.options.container === 'string'
            ? document.getElementById(this.options.container)
            : this.options.container;

        if (!this.canvas) {
            return;
        }

        this.gl = null;
        this.program = null;
        this.buffer = null;
        this.vertShader = null;
        this.fragShader = null;
        this.locations = {};

        this.animFrameId = null;
        this.isRunning = false;
        this.isTabVisible = !document.hidden;
        this.isInView = true;
        this.startTime = performance.now();
        this.pauseTimestamp = 0;

        this.reduceMotion = false;
        if (typeof window !== 'undefined' && window.matchMedia) {
            const mq = window.matchMedia('(prefers-reduced-motion: reduce)');
            this.reduceMotion = mq.matches;
            this.boundOnMotionChange = (e) => {
                this.reduceMotion = e.matches;
                if (this.reduceMotion) {
                    this.renderSingleFrame();
                    this.pause();
                } else {
                    this.resume();
                }
            };
            if (mq.addEventListener) {
                mq.addEventListener('change', this.boundOnMotionChange);
            }
        }

        this.init();
    }

    CloudShader.prototype.init = function () {
        const canvas = this.canvas;

        // Obtain WebGL context with optimal low-power settings
        const glOpts = {
            alpha: false,
            antialias: false,
            depth: false,
            stencil: false,
            preserveDrawingBuffer: false,
            powerPreference: 'low-power',
        };

        let gl = null;
        try {
            gl = canvas.getContext('webgl', glOpts) || canvas.getContext('experimental-webgl', glOpts);
        } catch (e) {
            console.warn('CloudShader: WebGL initialization failed:', e);
        }

        if (!gl) {
            console.info('CloudShader: WebGL not supported, displaying static botanical CSS fallback.');
            if (this.container) {
                this.container.classList.add('cloud-shader-fallback');
            }
            return;
        }

        this.gl = gl;

        // Compile vertex and fragment shaders
        const vert = compileShader(gl, gl.VERTEX_SHADER, VERTEX_SHADER_SOURCE);
        const frag = compileShader(gl, gl.FRAGMENT_SHADER, FRAGMENT_SHADER_SOURCE);
        if (!vert || !frag) {
            if (vert) gl.deleteShader(vert);
            if (frag) gl.deleteShader(frag);
            return;
        }
        this.vertShader = vert;
        this.fragShader = frag;

        // Link shader program
        const program = gl.createProgram();
        if (!program) return;
        gl.attachShader(program, vert);
        gl.attachShader(program, frag);
        gl.bindAttribLocation(program, 0, 'a_pos');
        gl.linkProgram(program);

        if (!gl.getProgramParameter(program, gl.LINK_STATUS)) {
            console.warn('CloudShader: Program link error:', gl.getProgramInfoLog(program));
            gl.deleteProgram(program);
            return;
        }
        this.program = program;
        gl.useProgram(program);

        // Fullscreen single triangle buffer
        const buffer = gl.createBuffer();
        gl.bindBuffer(gl.ARRAY_BUFFER, buffer);
        gl.bufferData(
            gl.ARRAY_BUFFER,
            new Float32Array([-1, -1, 3, -1, -1, 3]),
            gl.STATIC_DRAW
        );
        gl.enableVertexAttribArray(0);
        gl.vertexAttribPointer(0, 2, gl.FLOAT, false, 0, 0);
        this.buffer = buffer;

        // Uniform locations
        this.locations = {
            res: gl.getUniformLocation(program, 'u_res'),
            time: gl.getUniformLocation(program, 'u_time'),
            count: gl.getUniformLocation(program, 'u_count'),
            cloud: gl.getUniformLocation(program, 'u_cloud'),
            skyTop: gl.getUniformLocation(program, 'u_skyTop'),
            skyBottom: gl.getUniformLocation(program, 'u_skyBottom'),
        };

        // Resize handling
        this.resize();
        this.setupObservers();

        // Start render loop or draw static frame for reduced motion
        if (this.reduceMotion) {
            this.renderSingleFrame();
        } else {
            this.resume();
        }
    };

    CloudShader.prototype.resize = function () {
        if (!this.gl || !this.canvas) return;

        const canvas = this.canvas;
        const target = this.container || canvas;
        const dpr = Math.min(window.devicePixelRatio || 1, this.options.maxDpr || 1.5);

        const width = target.clientWidth || canvas.clientWidth || (typeof window !== 'undefined' ? window.innerWidth : 300);
        const height = target.clientHeight || canvas.clientHeight || (typeof window !== 'undefined' ? window.innerHeight : 200);

        const w = Math.max(1, Math.floor(width * dpr));
        const h = Math.max(1, Math.floor(height * dpr));

        if (canvas.width !== w || canvas.height !== h) {
            canvas.width = w;
            canvas.height = h;
        }

        this.gl.viewport(0, 0, w, h);
        this.gl.uniform2f(this.locations.res, w, h);
    };

    CloudShader.prototype.renderFrame = function (now) {
        if (!this.gl || !this.program) return;

        const gl = this.gl;
        const p = this.options;
        const elapsed = this.reduceMotion ? 0.0 : ((now - this.startTime) / 1000) * p.speed;

        const cloud = parseColor(p.cloudColor);
        const skyTop = parseColor(p.skyTopColor);
        const skyBottom = parseColor(p.skyBottomColor);

        gl.uniform1f(this.locations.time, elapsed);
        gl.uniform1f(this.locations.count, Math.min(6, Math.max(1, p.count)));
        gl.uniform3f(this.locations.cloud, cloud[0], cloud[1], cloud[2]);
        gl.uniform3f(this.locations.skyTop, skyTop[0], skyTop[1], skyTop[2]);
        gl.uniform3f(this.locations.skyBottom, skyBottom[0], skyBottom[1], skyBottom[2]);

        gl.drawArrays(gl.TRIANGLES, 0, 3);
    };

    CloudShader.prototype.renderSingleFrame = function () {
        this.renderFrame(this.startTime);
    };

    CloudShader.prototype.tick = function (now) {
        if (!this.isRunning) return;
        this.renderFrame(now);
        this.animFrameId = requestAnimationFrame((t) => this.tick(t));
    };

    CloudShader.prototype.resume = function () {
        if (this.isRunning || this.reduceMotion || !this.gl) return;
        if (!this.isTabVisible || !this.isInView) return;

        if (this.pauseTimestamp > 0) {
            // Adjust start time by pause interval so cloud motion does not jump forward abruptly
            this.startTime += (performance.now() - this.pauseTimestamp);
            this.pauseTimestamp = 0;
        }

        this.isRunning = true;
        this.animFrameId = requestAnimationFrame((t) => this.tick(t));
    };

    CloudShader.prototype.pause = function () {
        if (!this.isRunning) return;
        this.isRunning = false;
        if (this.animFrameId) {
            cancelAnimationFrame(this.animFrameId);
            this.animFrameId = null;
        }
        if (this.pauseTimestamp === 0) {
            this.pauseTimestamp = performance.now();
        }
    };

    CloudShader.prototype.setupObservers = function () {
        // ResizeObserver
        if (typeof ResizeObserver !== 'undefined') {
            this.resizeObserver = new ResizeObserver(() => {
                this.resize();
                if (this.reduceMotion) {
                    this.renderSingleFrame();
                }
            });
            this.resizeObserver.observe(this.container || this.canvas);
        } else {
            this.boundWindowResize = () => this.resize();
            window.addEventListener('resize', this.boundWindowResize);
        }

        // Visibility Change Listener (Pause animation when browser tab is inactive)
        this.boundVisibilityChange = () => {
            if (document.hidden) {
                this.isTabVisible = false;
                this.pause();
            } else {
                this.isTabVisible = true;
                this.resume();
            }
        };
        document.addEventListener('visibilitychange', this.boundVisibilityChange);

        // IntersectionObserver (Only for elements that can scroll out of view)
        // If container is fixed full viewport, it stays in view
        if (typeof IntersectionObserver !== 'undefined' && this.container && !this.container.classList.contains('global-cloud-canvas-container')) {
            this.intersectionObserver = new IntersectionObserver((entries) => {
                entries.forEach((entry) => {
                    if (entry.isIntersecting) {
                        this.isInView = true;
                        this.resume();
                    } else {
                        this.isInView = false;
                        this.pause();
                    }
                });
            }, { threshold: 0.05 });
            this.intersectionObserver.observe(this.container);
        }
    };

    CloudShader.prototype.destroy = function () {
        this.pause();

        if (this.resizeObserver) {
            this.resizeObserver.disconnect();
            this.resizeObserver = null;
        }
        if (this.boundWindowResize) {
            window.removeEventListener('resize', this.boundWindowResize);
        }
        if (this.boundVisibilityChange) {
            document.removeEventListener('visibilitychange', this.boundVisibilityChange);
        }
        if (this.intersectionObserver) {
            this.intersectionObserver.disconnect();
            this.intersectionObserver = null;
        }
        if (this.boundOnMotionChange && window.matchMedia) {
            const mq = window.matchMedia('(prefers-reduced-motion: reduce)');
            if (mq.removeEventListener) {
                mq.removeEventListener('change', this.boundOnMotionChange);
            }
        }

        if (this.gl) {
            const gl = this.gl;
            if (this.buffer) gl.deleteBuffer(this.buffer);
            if (this.program) gl.deleteProgram(this.program);
            if (this.vertShader) gl.deleteShader(this.vertShader);
            if (this.fragShader) gl.deleteShader(this.fragShader);
            this.gl = null;
        }
    };

    // Auto-initialize on DOM ready if cloud canvas is found
    document.addEventListener('DOMContentLoaded', function () {
        const cloudCanvas = document.getElementById('heroCloudCanvas');
        if (cloudCanvas) {
            global.growGardenGlobalCloudShader = new CloudShader({
                canvas: cloudCanvas,
                container: document.getElementById('globalCloudContainer') || document.getElementById('heroCloudContainer'),
                speed: 0.35,
                count: 4.5,
                cloudColor: '#faf8f2',
                skyTopColor: '#3f6b5b',
                skyBottomColor: '#a2d2c2',
                maxDpr: 1.5,
            });
        }
    });

    global.CloudShader = CloudShader;

})(typeof window !== 'undefined' ? window : this);
