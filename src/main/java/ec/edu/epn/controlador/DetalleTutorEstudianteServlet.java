package ec.edu.epn.controlador;

import ec.edu.epn.dao.UsuarioDAO;
import ec.edu.epn.modelo.TutorPerfilDetalle;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

public class DetalleTutorEstudianteServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

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
        request.getRequestDispatcher("/WEB-INF/estudiante/detalle-tutor.jsp").forward(request, response);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
