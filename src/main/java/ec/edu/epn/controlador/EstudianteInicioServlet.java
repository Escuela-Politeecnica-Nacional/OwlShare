package ec.edu.epn.controlador;

import ec.edu.epn.dao.MaterialAdquisicionDAO;
import ec.edu.epn.dao.MaterialDAO;
import ec.edu.epn.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class EstudianteInicioServlet extends HttpServlet {

    private final MaterialDAO materialDAO = new MaterialDAO();
    private final MaterialAdquisicionDAO adquisicionDAO = new MaterialAdquisicionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario estudiante = EstudianteAuth.requerirEstudiante(request, response);
        if (estudiante == null) {
            return;
        }

        if (estudiante.getCarrera() != null) {
            request.setAttribute("carreraNombre", estudiante.getCarrera().getNombre());
            request.setAttribute("materialesDisponibles",
                    materialDAO.contarAprobadosPorCarrera(estudiante.getCarrera()));
        }
        request.setAttribute("materialesAdquiridos",
                adquisicionDAO.contarAdquiridosPorEstudiante(estudiante.getId()));

        request.getRequestDispatcher("/WEB-INF/estudiante/dashboard-estudiante.jsp")
                .forward(request, response);
    }
}
