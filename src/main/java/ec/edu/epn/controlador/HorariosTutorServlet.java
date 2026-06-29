package ec.edu.epn.controlador;

import ec.edu.epn.dao.DisponibilidadTutorDAO;
import ec.edu.epn.dao.SolicitudTutoriaDAO;
import ec.edu.epn.modelo.DisponibilidadTutorVista;
import ec.edu.epn.modelo.SesionAgendadaVista;
import ec.edu.epn.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

public class HorariosTutorServlet extends HttpServlet {

    private final DisponibilidadTutorDAO disponibilidadDAO = new DisponibilidadTutorDAO();
    private final SolicitudTutoriaDAO solicitudDAO = new SolicitudTutoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario tutor = TutorAuth.requerirTutor(request, response);
        if (tutor == null) {
            return;
        }

        List<DisponibilidadTutorVista> disponibilidades;
        List<SesionAgendadaVista> sesionesAgendadas;
        try {
            disponibilidades = disponibilidadDAO.listarPorTutor(tutor.getId()).stream()
                    .map(DisponibilidadTutorVista::desde)
                    .toList();
            sesionesAgendadas = solicitudDAO.listarAgendadasPorTutor(tutor.getId()).stream()
                    .map(SesionAgendadaVista::desde)
                    .toList();
        } catch (RuntimeException e) {
            disponibilidades = List.of();
            sesionesAgendadas = List.of();
            request.setAttribute("error", "No se pudieron cargar los horarios.");
        }

        request.setAttribute("disponibilidades", disponibilidades);
        request.setAttribute("sesionesAgendadas", sesionesAgendadas);
        request.setAttribute("diasSemana", disponibilidadDAO.diasSemana());
        transferirMensaje(request.getSession(false), request, "exito");
        transferirMensaje(request.getSession(false), request, "error");

        request.getRequestDispatcher("/WEB-INF/tutor/gestion-horarios-tutor.jsp")
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
