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
        if (EstudianteAuth.requerirEstudiante(request, response) == null) {
            return;
        }

        request.getRequestDispatcher("/WEB-INF/estudiante/dashboard-estudiante.jsp")
                .forward(request, response);
    }
}
