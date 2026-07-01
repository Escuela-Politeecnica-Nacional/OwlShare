package ec.edu.epn.controlador;

import ec.edu.epn.dao.MaterialDAO;
import ec.edu.epn.modelo.EstadoMaterial;
import ec.edu.epn.modelo.Material;
import ec.edu.epn.modelo.MaterialVista;
import ec.edu.epn.modelo.Rol;
import ec.edu.epn.modelo.TutorPerfilVista;
import ec.edu.epn.modelo.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Carga los materiales del tutor autenticado y los envía a visualizar-material.jsp.
 *
 *  GET /tutor/mis-materiales
 *
 * Atributos que deja en el request:
 *   - materiales        List<MaterialVista>  lista completa del tutor
 *   - totalMateriales   long                 conteo total
 *   - materialesAprobados   long             conteo aprobados
 *   - materialesEnRevision  long             conteo pendientes
 *   - tutorPerfil       TutorPerfilVista     datos del tutor para el header
 */
@WebServlet("/tutor/mis-materiales")
public class VisualizarMaterialServlet extends HttpServlet {

    private MaterialDAO materialDAO;

    @Override
    public void init() {
        materialDAO = new MaterialDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ── Autenticación y rol ───────────────────────────────────────────────
        HttpSession session = request.getSession(false);
        Usuario usuario = session != null
                ? (Usuario) session.getAttribute("usuarioLogueado")
                : null;

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (usuario.getRol() != Rol.TUTOR) {
            response.sendRedirect(request.getContextPath() + "/inicio");
            return;
        }

        // ── Cargar materiales del tutor ───────────────────────────────────────
        List<Material> materiales = materialDAO.listarPorTutor(usuario.getId());

        // Mapear a vista
        List<MaterialVista> vistas = materiales.stream()
                .map(MaterialVista::paraTutor)
                .toList();

        // Estadísticas que el JSP muestra en las tarjetas bento
        long aprobados   = materiales.stream()
                .filter(m -> m.getEstado() == EstadoMaterial.APROBADO).count();
        long enRevision  = materiales.stream()
                .filter(m -> m.getEstado() == EstadoMaterial.PENDIENTE).count();

        // ── Atributos al request ──────────────────────────────────────────────
        request.setAttribute("materiales",           vistas);
        request.setAttribute("totalMateriales",      materiales.size());
        request.setAttribute("materialesAprobados",  aprobados);
        request.setAttribute("materialesEnRevision", enRevision);
        request.setAttribute("tutorPerfil",          TutorPerfilVista.desde(usuario));

        request.getRequestDispatcher("/WEB-INF/tutor/visualizar-material.jsp")
               .forward(request, response);
    }
}