package ec.edu.epn.controlador;

import ec.edu.epn.dao.UsuarioDAO;
import ec.edu.epn.modelo.Carrera;
import ec.edu.epn.modelo.Materia;
import ec.edu.epn.modelo.TutorBusquedaVista;
import ec.edu.epn.util.CatalogoRegistro;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class BuscarTutorServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (EstudianteAuth.requerirEstudiante(request, response) == null) {
            return;
        }
        procesar(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (EstudianteAuth.requerirEstudiante(request, response) == null) {
            return;
        }
        procesar(request, response);
    }

    private void procesar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String carreraParam = trim(request.getParameter("carrera"));
        String materiaParam = trim(request.getParameter("materia"));

        Carrera carrera = parseCarrera(carreraParam);
        List<Materia> materiasFormulario = materiasParaFormulario(carrera);

        request.setAttribute("carreras", CatalogoRegistro.carreras());
        request.setAttribute("materias", materiasFormulario);
        request.setAttribute("carreraSeleccionada", carreraParam);
        request.setAttribute("materiaSeleccionada", materiaParam);

        boolean busquedaRealizada = !carreraParam.isEmpty() || !materiaParam.isEmpty();
        request.setAttribute("busquedaRealizada", busquedaRealizada);

        if (busquedaRealizada) {
            List<TutorBusquedaVista> tutores;
            try {
                tutores = usuarioDAO.buscarTutores(carrera, materiaParam).stream()
                        .map(TutorBusquedaVista::desde)
                        .toList();
            } catch (RuntimeException e) {
                request.setAttribute("error", "No se pudo realizar la búsqueda. Intenta de nuevo.");
                tutores = List.of();
            }
            request.setAttribute("tutores", tutores);
        }

        request.getRequestDispatcher("/WEB-INF/estudiante/buscar-tutor.jsp").forward(request, response);
    }

    private List<Materia> materiasParaFormulario(Carrera carrera) {
        if (carrera != null) {
            return CatalogoRegistro.materiasDeCarrera(carrera);
        }
        return CatalogoRegistro.todasLasMaterias();
    }

    private Carrera parseCarrera(String carreraParam) {
        if (carreraParam.isEmpty()) {
            return null;
        }
        try {
            return Carrera.valueOf(carreraParam);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
