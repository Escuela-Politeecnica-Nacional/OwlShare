package ec.edu.epn.controlador;

import ec.edu.epn.dao.DisponibilidadTutorDAO;
import ec.edu.epn.dao.UsuarioDAO;
import ec.edu.epn.modelo.DisponibilidadTutorVista;
import ec.edu.epn.modelo.TutorPerfilDetalle;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

public class DetalleTutorEstudianteServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final DisponibilidadTutorDAO disponibilidadDAO = new DisponibilidadTutorDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (EstudianteAuth.requerirEstudiante(request, response) == null) {
            return;
        }

        String idParam = trim(request.getParameter("id"));
        if (idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/estudiante/buscar-tutor");
            return;
        }

        long id;
        try {
            id = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/estudiante/buscar-tutor");
            return;
        }

        Optional<TutorPerfilDetalle> perfil;
        try {
            perfil = usuarioDAO.buscarPerfilTutor(id);
        } catch (RuntimeException e) {
            response.sendRedirect(request.getContextPath() + "/estudiante/buscar-tutor");
            return;
        }

        if (perfil.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/estudiante/buscar-tutor");
            return;
        }

        request.setAttribute("tutorPerfil", perfil.get());
        try {
            request.setAttribute("disponibilidades", disponibilidadDAO.listarPorTutor(id).stream()
                    .map(DisponibilidadTutorVista::desde)
                    .toList());
        } catch (RuntimeException e) {
            request.setAttribute("disponibilidades", java.util.List.of());
        }
        request.getRequestDispatcher("/WEB-INF/estudiante/detalle-tutor.jsp").forward(request, response);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
