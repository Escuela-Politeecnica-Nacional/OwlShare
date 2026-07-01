package ec.edu.epn.controlador;

import ec.edu.epn.dao.MaterialDAO;
import ec.edu.epn.dao.UsuarioDAO;
import ec.edu.epn.modelo.EstadoMaterial;
import ec.edu.epn.modelo.Material;
import ec.edu.epn.modelo.MaterialModeracionVista;
import ec.edu.epn.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

public class ModeracionMaterialesServlet extends HttpServlet {

    private final MaterialDAO materialDAO = new MaterialDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (AdminAuth.requerirAdmin(request, response) == null) {
            return;
        }

        List<MaterialModeracionVista> materiales;
        try {
            materiales = materialDAO.listarPorEstado(EstadoMaterial.PENDIENTE).stream()
                    .map(this::aVista)
                    .toList();
        } catch (RuntimeException e) {
            materiales = List.of();
            request.setAttribute("error", "No se pudieron cargar los materiales pendientes.");
        }

        request.setAttribute("materiales", materiales);
        request.setAttribute("totalPendientes", materiales.size());
        transferirMensaje(request.getSession(false), request, "exito");
        transferirMensaje(request.getSession(false), request, "error");

        request.getRequestDispatcher("/WEB-INF/admin/moderacion-materiales.jsp")
                .forward(request, response);
    }

    private MaterialModeracionVista aVista(Material material) {
        String nombreTutor = usuarioDAO.buscarPorId(material.getIdTutor())
                .map(this::nombreCompleto)
                .orElse("Tutor");
        return MaterialModeracionVista.desde(material, nombreTutor);
    }

    private String nombreCompleto(Usuario usuario) {
        StringBuilder nombre = new StringBuilder(usuario.getNombre());
        if (usuario.getApellido() != null && !usuario.getApellido().isBlank()) {
            nombre.append(' ').append(usuario.getApellido());
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
}
