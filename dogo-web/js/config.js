// =====================================================================================
//  CONFIGURACIÓN DE LA WEB "DO AND GO"
//  Edita aquí los datos de la tienda: la web los aplica en todas las páginas.
//  (Los .html traen una copia de respaldo de dirección/teléfono/horario por si el JavaScript no carga.)
//  Los valores marcados con  TODO  son de ejemplo: reemplázalos por los reales.
// =====================================================================================

window.DoGo = window.DoGo || {};

(function () {
    'use strict';

    DoGo.CONFIG = {
        // URL del backend (API Spring). En producción: la URL pública del backend, p. ej. https://api.doandgo.pe
        // Para pruebas se puede sobrescribir sin tocar el código:  localStorage.setItem('dogoApiUrl', 'http://localhost:8081')
        API_BASE_URL: readOverride('dogoApiUrl') || 'https://dogoperu.onrender.com',

        // Formspree (envío del formulario de contacto por correo). Crea un formulario gratis en https://formspree.io
        // y pega aquí su ID (p. ej. 'xyzabcd'). Vacío = el botón "Enviar por correo" abre el programa de correo (mailto).
        FORMSPREE_ID: '', // TODO

        tienda: {
            nombre: 'Do and Go',
            nombreLegal: 'Tienda de ropas Do and Go',
            slogan: 'Moda para todos los días, al alcance de tu barrio',
            descripcion: 'Tienda de ropa en Carapongo con polos, camisas, jeans, casacas y zapatillas para toda la familia. '
                + 'Atención personalizada y pedidos por WhatsApp.',
            anioFundacion: 2016, // TODO (historia de ejemplo, ver nosotros.html)

            direccion: {
                linea1: 'Mercado Carapongo, Calle Parinacochas',
                linea2: 'Carapongo, Lurigancho-Chosica',
                ciudad: 'Lima',
                codigoPostal: '15487',
                pais: 'Perú',
                referencia: 'Dentro del Mercado Carapongo', // TODO: número de puesto o referencia exacta
            },
            coordenadas: { lat: -12.002135, lng: -76.8639779 },
            // Ficha de Google Maps de la tienda
            googleMapsUrl: 'https://www.google.com/maps/place/Tienda+de+ropas+Do+and+Go/@-12.002135,-76.8639779,17z/data=!4m6!3m5!1s0x9105c31b853f63ab:0x35487aaebfcf696c!8m2!3d-12.002135!4d-76.8639779',
            // Opcional: en Google Maps abre la ficha de la tienda → Compartir → Insertar un mapa, y pega aquí
            // solo la URL del atributo src="..." (así el mapa muestra el nombre "Do and Go" en el marcador).
            // Vacío = mapa con marcador en las coordenadas.
            googleMapsEmbedUrl: '', // TODO

            telefono: '+51 999 999 999',      // TODO
            whatsapp: '51999999999',          // TODO: código de país + número, solo dígitos
            email: 'contacto@doandgo.pe',     // TODO

            horario: [                        // TODO
                { dias: 'Lunes a sábado', horas: '9:00 a. m. – 8:00 p. m.' },
                { dias: 'Domingos y feriados', horas: '9:00 a. m. – 2:00 p. m.' },
            ],

            redes: {                          // TODO: enlaces reales (deja '' para ocultar una red)
                facebook: 'https://www.facebook.com/',
                instagram: 'https://www.instagram.com/',
                tiktok: 'https://www.tiktok.com/',
            },

            mediosPago: ['Efectivo', 'Yape', 'Plin', 'Tarjeta débito / crédito'],

            // Libro de Reclamaciones (obligatorio en Perú). TODO: enlace a tu libro virtual
            libroReclamaciones: 'contacto.html#libro-reclamaciones',
        },

        // Imágenes provisionales (Unsplash) mientras no subas las fotos reales a /img.
        // Se usan cuando un producto no tiene imagen o el archivo de /img aún no existe.
        imagenesRespaldo: {
            polo: 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab',
            camisa: 'https://images.unsplash.com/photo-1596755094514-f87e34085b2c',
            pantalon: 'https://images.unsplash.com/photo-1542272604-787c3835535d',
            jean: 'https://images.unsplash.com/photo-1604176354204-9268737828e4',
            chaqueta: 'https://images.unsplash.com/photo-1591047139829-d91aecb6caea',
            zapato: 'https://images.unsplash.com/photo-1467043237213-65f2da53396f',
            general: 'https://images.unsplash.com/photo-1490481651871-ab68de25d43d',
        },
    };

    function readOverride(key) {
        try {
            return localStorage.getItem(key);
        } catch {
            return null;
        }
    }
})();
