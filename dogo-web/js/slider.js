// Slider del inicio, sin librerías. Las diapositivas están en el HTML (así la primera imagen
// se descarga de inmediato); este script solo añade navegación, autoplay y gestos.
window.DoGo = window.DoGo || {};

(function () {
    'use strict';

    const { ICONS } = DoGo;
    const { h, svg } = DoGo.ui;

    const INTERVALO_MS = 6000;

    function initSlider(root) {
        const slides = [...root.querySelectorAll('.slide')];
        if (slides.length < 2) return;

        const reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
        let current = 0;
        let timer = null;
        let paused = false;

        const dots = h('div', { className: 'slider-dots', role: 'group', 'aria-label': 'Elegir diapositiva' },
            slides.map((_, i) => h('button', {
                type: 'button', className: 'slider-dot', 'aria-label': `Ir a la diapositiva ${i + 1}`, onclick: () => go(i, true),
            })));
        const prev = h('button', { type: 'button', className: 'slider-arrow prev', 'aria-label': 'Diapositiva anterior', onclick: () => go(current - 1, true) },
            svg(ICONS.chevronLeft));
        const next = h('button', { type: 'button', className: 'slider-arrow next', 'aria-label': 'Diapositiva siguiente', onclick: () => go(current + 1, true) },
            svg(ICONS.chevronRight));
        const pauseBtn = h('button', { type: 'button', className: 'slider-pause', onclick: () => setPaused(!paused) });
        root.append(prev, next, h('div', { className: 'slider-controls' }, dots, reduceMotion ? null : pauseBtn));

        function go(index, fromUser = false) {
            current = (index + slides.length) % slides.length;
            slides.forEach((slide, i) => {
                const active = i === current;
                slide.classList.toggle('is-active', active);
                slide.setAttribute('aria-hidden', String(!active));
                slide.inert = !active; // los enlaces de diapositivas ocultas no reciben foco
                if (active) {
                    // Carga la imagen de la diapositiva activa (y la siguiente) justo antes de mostrarla
                    slide.querySelectorAll('img[loading="lazy"]').forEach((img) => { img.loading = 'eager'; });
                }
            });
            slides[(current + 1) % slides.length].querySelectorAll('img[loading="lazy"]').forEach((img) => { img.loading = 'eager'; });
            dots.querySelectorAll('button').forEach((d, i) => d.setAttribute('aria-current', String(i === current)));
            if (fromUser) restart();
        }

        function setPaused(value) {
            paused = value;
            pauseBtn.textContent = paused ? '▶' : '❚❚';
            pauseBtn.setAttribute('aria-label', paused ? 'Reanudar presentación' : 'Pausar presentación');
            restart();
        }

        function restart() {
            clearInterval(timer);
            timer = null;
            if (!paused && !reduceMotion && !document.hidden) {
                timer = setInterval(() => go(current + 1), INTERVALO_MS);
            }
        }

        // Pausa mientras el usuario lo usa o la pestaña está oculta (no consume recursos)
        let hover = false;
        const hold = (value) => { hover = value; hover ? clearInterval(timer) : restart(); };
        root.addEventListener('mouseenter', () => hold(true));
        root.addEventListener('mouseleave', () => hold(false));
        root.addEventListener('focusin', () => hold(true));
        root.addEventListener('focusout', (e) => { if (!root.contains(e.relatedTarget)) hold(false); });
        document.addEventListener('visibilitychange', () => { if (!hover) restart(); });

        // Teclado
        root.addEventListener('keydown', (e) => {
            if (e.key === 'ArrowLeft') go(current - 1, true);
            if (e.key === 'ArrowRight') go(current + 1, true);
        });

        // Deslizar con el dedo
        let startX = null;
        root.addEventListener('pointerdown', (e) => { if (e.pointerType !== 'mouse') startX = e.clientX; });
        root.addEventListener('pointerup', (e) => {
            if (startX === null) return;
            const dx = e.clientX - startX;
            startX = null;
            if (Math.abs(dx) > 40) go(current + (dx < 0 ? 1 : -1), true);
        });

        go(0);
        setPaused(false);
    }

    DoGo.slider = { initSlider };
})();
