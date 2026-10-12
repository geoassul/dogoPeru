// Formulario de contacto: se envía por WhatsApp (abre wa.me con el mensaje armado)
// o por correo (Formspree si está configurado en config.js; si no, abre el programa de correo).
(function () {
    'use strict';

    const { CONFIG } = DoGo;
    const { whatsappUrl } = DoGo.ui;

    const form = document.getElementById('contact-form');
    const status = document.getElementById('form-status');

    function datos() {
        const f = new FormData(form);
        return {
            nombre: f.get('nombre').toString().trim(),
            telefono: f.get('telefono').toString().trim(),
            email: f.get('email').toString().trim(),
            asunto: f.get('asunto').toString(),
            mensaje: f.get('mensaje').toString().trim(),
            trampa: f.get('website').toString(), // honeypot: los humanos no lo ven ni lo llenan
        };
    }

    function texto(d) {
        return [
            `Hola Do and Go, soy ${d.nombre}.`,
            `Asunto: ${d.asunto}`,
            d.mensaje,
            d.telefono && `Teléfono: ${d.telefono}`,
            d.email && `Correo: ${d.email}`,
        ].filter(Boolean).join('\n');
    }

    function mostrar(mensaje, tipo) {
        status.textContent = mensaje;
        status.className = `form-status ${tipo}`;
    }

    function validar(requiereEmail) {
        form.elements.email.required = requiereEmail;
        if (!form.reportValidity()) {
            mostrar('Revisa los campos marcados.', 'error');
            return null;
        }
        const d = datos();
        if (d.trampa) return null; // probablemente un bot
        return d;
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const canal = e.submitter?.value || 'whatsapp';

        if (canal === 'whatsapp') {
            const d = validar(false);
            if (!d) return;
            window.open(whatsappUrl(texto(d)), '_blank', 'noopener');
            mostrar('Abrimos WhatsApp con tu mensaje. Solo presiona "Enviar" allí.', 'ok');
            return;
        }

        // Correo
        const d = validar(true);
        if (!d) return;
        if (!CONFIG.FORMSPREE_ID) {
            const asunto = encodeURIComponent(`[Web] ${d.asunto} - ${d.nombre}`);
            window.location.href = `mailto:${CONFIG.tienda.email}?subject=${asunto}&body=${encodeURIComponent(texto(d))}`;
            mostrar('Abrimos tu programa de correo con el mensaje listo para enviar.', 'ok');
            return;
        }

        const boton = e.submitter;
        boton.disabled = true;
        mostrar('Enviando...', 'info');
        try {
            const response = await fetch(`https://formspree.io/f/${CONFIG.FORMSPREE_ID}`, {
                method: 'POST',
                headers: { Accept: 'application/json', 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    nombre: d.nombre, telefono: d.telefono, email: d.email, asunto: d.asunto, mensaje: d.mensaje,
                    _subject: `[Web Do and Go] ${d.asunto} - ${d.nombre}`,
                }),
            });
            if (!response.ok) throw new Error(String(response.status));
            form.reset();
            mostrar('¡Gracias! Recibimos tu mensaje y te responderemos pronto.', 'ok');
        } catch {
            mostrar('No pudimos enviar el correo. Intenta por WhatsApp o escríbenos directamente.', 'error');
        } finally {
            boton.disabled = false;
        }
    });
})();
