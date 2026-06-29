package ec.edu.epn.controlador;

import ec.edu.epn.dao.SolicitudTutoriaDAO;
import ec.edu.epn.modelo.EstadoSolicitud;
import ec.edu.epn.modelo.SolicitudTutoriaVista;
import ec.edu.epn.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

public class SolicitudesTutorServlet extends HttpServlet {

    private final SolicitudTutoriaDAO solicitudDAO = new SolicitudTutoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario tutor = TutorAuth.requerirTutor(request, response);
        if (tutor == null) {
            return;
        }

        List<SolicitudTutoriaVista> solicitudes;
        try {
            solicitudes = solicitudDAO.listarPorTutor(tutor.getId()).stream()
                    .map(SolicitudTutoriaVista::desde)
                    .toList();
        } catch (RuntimeException e) {
            solicitudes = List.of();
            request.setAttribute("error", "No se pudieron cargar las solicitudes.");
        }

        request.setAttribute("solicitudes", solicitudes);
        transferirMensaje(request.getSession(false), request, "exito");
        transferirMensaje(request.getSession(false), request, "error");

        request.getRequestDispatcher("/WEB-INF/tutor/bandeja-solicitudes-tutor.jsp")
                .forward(request, response);
    }

    private void transferirMensaje(HttpSession session, HttpServletRequest request, String clave) {
        if (session == null) {
            return;
        }
        Object valor = session.getAttribute(clave);
        if (valor != null) {
            request.setAttribute(clave, valor);
            session.removeAttribute(clave);
        }
    }
}
