package ec.edu.epn.controlador;

import ec.edu.epn.dao.MaterialAdquisicionDAO;
import ec.edu.epn.dao.MaterialDAO;
import ec.edu.epn.dao.UsuarioDAO;
import ec.edu.epn.modelo.Carrera;
import ec.edu.epn.modelo.MaterialVista;
import ec.edu.epn.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Biblioteca del estudiante: materiales aprobados filtrados automáticamente
 * por la carrera registrada en su perfil.
 */
public class EstudianteBibliotecaServlet extends HttpServlet {

    private final MaterialDAO materialDAO = new MaterialDAO();
    private final MaterialAdquisicionDAO adquisicionDAO = new MaterialAdquisicionDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario estudiante = EstudianteAuth.requerirEstudiante(request, response);
        if (estudiante == null) {
            return;
        }

        Carrera carrera = estudiante.getCarrera();
        String busqueda = trim(request.getParameter("busqueda"));

        List<MaterialVista> materiales;
        if (carrera == null) {
            materiales = List.of();
            request.setAttribute("sinCarrera", true);
            request.setAttribute("error",
                    "Tu perfil no tiene una carrera registrada. No podemos mostrarte materiales relevantes.");
        } else {
            try {
                Set<Long> adquiridos = adquisicionDAO.idsAdquiridosPorEstudiante(estudiante.getId());
                materiales = materialDAO.listarAprobados(carrera, busqueda).stream()
                        .map(m -> MaterialVista.paraBiblioteca(
                                m,
                                nombreTutor(m.getIdTutor()),
                                adquiridos.contains(m.getId())))
                        .toList();
            } catch (RuntimeException e) {
                materiales = List.of();
                request.setAttribute("error", "No se pudo cargar la biblioteca de materiales.");
            }
            request.setAttribute("carreraEstudiante", carrera);
            request.setAttribute("carreraFiltrada", carrera.getNombre());
        }

        request.setAttribute("materiales", materiales);
        request.setAttribute("busquedaActiva", !busqueda.isEmpty());

        transferirMensaje(request.getSession(false), request, "exito");
        transferirMensaje(request.getSession(false), request, "error");

        request.getRequestDispatcher("/WEB-INF/estudiante/biblioteca-estudiante.jsp")
                .forward(request, response);
    }

    private String nombreTutor(Long tutorId) {
        return usuarioDAO.buscarPorId(tutorId)
                .map(this::nombreCompleto)
                .orElse("Tutor");
    }

    private String nombreCompleto(Usuario usuario) {
        StringBuilder nombre = new StringBuilder(usuario.getNombre());
        if (usuario.getSegundoNombre() != null && !usuario.getSegundoNombre().isBlank()) {
            nombre.append(' ').append(usuario.getSegundoNombre());
        }
        nombre.append(' ').append(usuario.getApellido());
        if (usuario.getSegundoApellido() != null && !usuario.getSegundoApellido().isBlank()) {
            nombre.append(' ').append(usuario.getSegundoApellido());
        }
        return nombre.toString().trim();
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

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
