// Animaciones al hacer scroll: WOW.js (js/vendor/wow.min.js) + animate.css (css/vendor/animate.min.css).
// Uso en el HTML: class="wow animate__fadeInUp" y, opcional, data-wow-delay="0.1s".
// - WOW oculta cada elemento .wow y, cuando entra en pantalla, le agrega "animate__animated" para que corra la animación.
// - live: true detecta también los elementos agregados después (tarjetas que llegan de la API).
// - Si el sistema pide reducir movimiento, no se inicia: todo se ve normal, sin animar.
window.DoGo = window.DoGo || {};

(function () {
    'use strict';

    const activo = typeof window.WOW === 'function'
        && !window.matchMedia('(prefers-reduced-motion: reduce)').matches;

    if (activo) {
        new window.WOW({
            boxClass: 'wow',
            animateClass: 'animate__animated', // animate.css 4 usa el prefijo animate__ (WOW 1.1.2 trae "animated" por defecto)
            offset: 60,
            mobile: true,
            live: true,
        }).init();
    }

    /**
     * Marca un elemento creado por JS para que aparezca al hacer scroll.
     * `i` es su posición en la grilla: escalona la entrada dentro de cada fila de 3.
     */
    function animar(el, i = 0, efecto = 'fadeInUp') {
        if (!activo) return el;
        el.classList.add('wow', `animate__${efecto}`);
        el.dataset.wowDelay = `${(i % 3) * 0.1}s`;
        return el;
    }

    DoGo.animar = animar;
})();
