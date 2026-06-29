package ec.edu.epn.controlador;

import ec.edu.epn.dao.UsuarioDAO;
import ec.edu.epn.modelo.TutorPerfilDetalle;
import ec.edu.epn.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

public class SolicitarMentoriaServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario estudiante = EstudianteAuth.requerirEstudiante(request, response);
        if (estudiante == null) {
            return;
        }

        String tutorIdParam = trim(request.getParameter("tutorId"));
        if (tutorIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/estudiante/buscar-tutor");
            return;
        }

        long tutorId;
        try {
            tutorId = Long.parseLong(tutorIdParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/estudiante/buscar-tutor");
            return;
        }

        Optional<TutorPerfilDetalle> perfil;
        try {
            perfil = usuarioDAO.buscarPerfilTutor(tutorId);
        } catch (RuntimeException e) {
            response.sendRedirect(request.getContextPath() + "/estudiante/buscar-tutor");
            return;
        }

        if (perfil.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/estudiante/buscar-tutor");
            return;
        }

        request.setAttribute("tutorPerfil", perfil.get());
        request.setAttribute("estudianteId", estudiante.getId());
        request.setAttribute("materiaPreseleccionada", trim(request.getParameter("codigoMateria")));

        request.getRequestDispatcher("/WEB-INF/estudiante/solicitar-mentoria.jsp")
                .forward(request, response);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
