package ec.edu.epn.controlador;

import ec.edu.epn.modelo.Rol;
import ec.edu.epn.modelo.TutorPerfilVista;
import ec.edu.epn.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class HorariosTutorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = session != null
                ? (Usuario) session.getAttribute("usuarioLogueado")
                : null;

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (usuario.getRol() != Rol.TUTOR) {
            response.sendRedirect(request.getContextPath() + destinoPorRol(usuario.getRol()));
            return;
        }

        request.setAttribute("tutorPerfil", TutorPerfilVista.desde(usuario));

        request.getRequestDispatcher("/WEB-INF/tutor/gestion-horarios-tutor.jsp").forward(request, response);
    }

    private String destinoPorRol(Rol rol) {
        return switch (rol) {
            case ESTUDIANTE -> "/estudiante/inicio";
            case TUTOR -> "/tutor/inicio";
            case ADMIN -> "/admin/inicio";
        };
    }
}
