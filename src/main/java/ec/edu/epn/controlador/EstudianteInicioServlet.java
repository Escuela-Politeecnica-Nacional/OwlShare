package ec.edu.epn.controlador;

import ec.edu.epn.modelo.EstudiantePerfilVista;
import ec.edu.epn.modelo.Rol;
import ec.edu.epn.modelo.Usuario;
import ec.edu.epn.util.SesionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class EstudianteInicioServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario usuario = SesionUtil.obtenerUsuario(request);

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (usuario.getRol() != Rol.ESTUDIANTE) {
            response.sendRedirect(request.getContextPath() + destinoPorRol(usuario.getRol()));
            return;
        }

        request.setAttribute("estudiantePerfil", EstudiantePerfilVista.desde(usuario));

        request.getRequestDispatcher("/WEB-INF/estudiante/dashboard-estudiante.jsp")
                .forward(request, response);
    }

    private String destinoPorRol(Rol rol) {
        return switch (rol) {
            case ESTUDIANTE -> "/estudiante/inicio";
            case TUTOR -> "/tutor/inicio";
            case ADMIN -> "/admin/inicio";
        };
    }
}
