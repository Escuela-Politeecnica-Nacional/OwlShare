package ec.edu.epn.controlador;

import ec.edu.epn.dao.SolicitudTutoriaDAO;
import ec.edu.epn.modelo.EstadoSolicitud;
import ec.edu.epn.modelo.SolicitudTutoria;
import ec.edu.epn.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

/**
 * El tutor acepta o rechaza solicitudes de mentoría pendientes.
 */
public class GestionSolicitudServlet extends HttpServlet {

    private final SolicitudTutoriaDAO solicitudDAO = new SolicitudTutoriaDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        Usuario tutor = TutorAuth.requerirTutor(request, response);
        if (tutor == null) {
            return;
        }

        String idParam = trim(request.getParameter("idSolicitud"));
        String accion = trim(request.getParameter("accion"));

        if (idParam.isEmpty()) {
            redirigirConError(request, response, "Falta el identificador de la solicitud.");
            return;
        }

        long idSolicitud;
        try {
            idSolicitud = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            redirigirConError(request, response, "El identificador de la solicitud no es válido.");
            return;
        }

        Optional<SolicitudTutoria> opcional;
        try {
            opcional = solicitudDAO.buscarPorId(idSolicitud);
        } catch (RuntimeException e) {
            redirigirConError(request, response, "No se pudo consultar la solicitud.");
            return;
        }

        if (opcional.isEmpty()) {
            redirigirConError(request, response, "No se encontró la solicitud indicada.");
            return;
        }

        SolicitudTutoria solicitud = opcional.get();

        if (!solicitud.getHorario().getTutor().getId().equals(tutor.getId())) {
            redirigirConError(request, response, "No tienes permiso para gestionar esta solicitud.");
            return;
        }

        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            redirigirConError(request, response,
                    "Esta solicitud ya fue " + solicitud.getEstado().name().toLowerCase()
                            + " y no puede modificarse.");
            return;
        }

        EstadoSolicitud nuevoEstado;
        String mensajeExito;
        if ("aceptar".equalsIgnoreCase(accion)) {
            nuevoEstado = EstadoSolicitud.ACEPTADA;
            mensajeExito = "Solicitud aceptada. El horario ha sido reservado.";
        } else if ("rechazar".equalsIgnoreCase(accion)) {
            nuevoEstado = EstadoSolicitud.RECHAZADA;
            mensajeExito = "Solicitud rechazada. El horario vuelve a estar disponible.";
        } else {
            redirigirConError(request, response, "Acción no reconocida.");
            return;
        }

        try {
            solicitudDAO.actualizarEstado(idSolicitud, nuevoEstado);
        } catch (RuntimeException e) {
            redirigirConError(request, response, "No se pudo actualizar la solicitud.");
            return;
        }

        redirigirConExito(request, response, mensajeExito);
    }

    private void redirigirConExito(HttpServletRequest request, HttpServletResponse response, String mensaje)
            throws IOException {
        guardarMensajeFlash(request, "exito", mensaje);
        response.sendRedirect(request.getContextPath() + "/tutor/solicitudes");
    }

    private void redirigirConError(HttpServletRequest request, HttpServletResponse response, String mensaje)
            throws IOException {
        guardarMensajeFlash(request, "error", mensaje);
        response.sendRedirect(request.getContextPath() + "/tutor/solicitudes");
    }

    private void guardarMensajeFlash(HttpServletRequest request, String clave, String mensaje) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute(clave, mensaje);
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
