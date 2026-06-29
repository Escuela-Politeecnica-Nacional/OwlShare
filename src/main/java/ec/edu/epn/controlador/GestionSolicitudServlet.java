package ec.edu.epn.controlador;

import ec.edu.epn.dao.SolicitudTutoriaDAO;
import ec.edu.epn.modelo.EstadoSolicitud;
import ec.edu.epn.modelo.Rol;
import ec.edu.epn.modelo.SolicitudTutoria;
import ec.edu.epn.modelo.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

/**
 * Gestión de solicitudes de tutoría por parte del tutor autenticado.
 *
 *  POST /gestion-solicitud?accion=aceptar  → acepta la solicitud y bloquea el horario
 *  POST /gestion-solicitud?accion=rechazar → rechaza la solicitud y libera el horario
 *
 * Tras la acción redirige a la bandeja del tutor.
 */
@WebServlet("/gestion-solicitud")
public class GestionSolicitudServlet extends HttpServlet {

    private SolicitudTutoriaDAO solicitudDAO;

    @Override
    public void init() {
        solicitudDAO = new SolicitudTutoriaDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario tutor = tutorAutenticado(request, response);
        if (tutor == null) return;

        String idParam = request.getParameter("idSolicitud");
        String accion  = request.getParameter("accion");

        // Validar id
        if (estaVacio(idParam)) {
            redirigirConError(request, response, "Falta el identificador de la solicitud.");
            return;
        }

        long idSolicitud;
        try {
            idSolicitud = Long.parseLong(idParam.trim());
        } catch (NumberFormatException e) {
            redirigirConError(request, response, "El identificador de la solicitud no es válido.");
            return;
        }

        // Cargar solicitud
        Optional<SolicitudTutoria> opcional = solicitudDAO.buscarPorId(idSolicitud);
        if (opcional.isEmpty()) {
            redirigirConError(request, response, "No se encontró la solicitud indicada.");
            return;
        }

        SolicitudTutoria solicitud = opcional.get();

        // Verificar que el horario pertenece al tutor autenticado
        if (!solicitud.getHorario().getTutor().getId().equals(tutor.getId())) {
            redirigirConError(request, response, "No tienes permiso para gestionar esta solicitud.");
            return;
        }

        // Verificar que la solicitud esté en estado PENDIENTE
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            redirigirConError(request, response,
                    "Esta solicitud ya fue " + solicitud.getEstado().name().toLowerCase() + " y no puede modificarse.");
            return;
        }

        // Aplicar acción
        String comentario = request.getParameter("comentario");
        if (!estaVacio(comentario)) {
            solicitud.setComentario(comentario.trim());
        }

        if ("aceptar".equalsIgnoreCase(accion)) {
            solicitud.setEstado(EstadoSolicitud.ACEPTADA);
            ((SolicitudTutoriaDAO) solicitudDAO).actualizarEstado(solicitud);
            redirigirConExito(request, response, "Solicitud aceptada. El horario ha sido bloqueado.");

        } else if ("rechazar".equalsIgnoreCase(accion)) {
            solicitud.setEstado(EstadoSolicitud.RECHAZADA);
            solicitudDAO.actualizarEstado(solicitud);
            redirigirConExito(request, response, "Solicitud rechazada. El horario vuelve a estar disponible.");

        } else {
            redirigirConError(request, response, "Acción no reconocida.");
        }
    }

    // ── Utilidades ────────────────────────────────────────────────────────────

    /**
     * Verifica que haya un tutor autenticado en sesión.
     * Redirige al login si no hay sesión, o al inicio si el rol no es TUTOR.
     */
    private Usuario tutorAutenticado(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }

        if (usuario.getRol() != Rol.TUTOR) {
            response.sendRedirect(request.getContextPath() + "/inicio");
            return null;
        }

        return usuario;
    }

    private void redirigirConExito(HttpServletRequest request, HttpServletResponse response,
                                   String mensaje) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) session.setAttribute("exito", mensaje);
        response.sendRedirect(request.getContextPath() + "/solicitudes-tutor");
    }

    private void redirigirConError(HttpServletRequest request, HttpServletResponse response,
                                   String mensaje) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) session.setAttribute("error", mensaje);
        response.sendRedirect(request.getContextPath() + "/solicitudes-tutor");
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.isBlank();
    }
}