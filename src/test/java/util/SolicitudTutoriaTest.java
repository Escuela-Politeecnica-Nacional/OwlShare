package util;

import ec.edu.epn.modelo.EstadoSolicitud;
import ec.edu.epn.modelo.SolicitudTutoria;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SolicitudTutoriaTest {

    @Test
    void estadoInicialEsPendiente() {
        SolicitudTutoria solicitud = new SolicitudTutoria();

        assertEquals(EstadoSolicitud.PENDIENTE, solicitud.getEstado());
        assertNull(solicitud.getId());
    }
}
