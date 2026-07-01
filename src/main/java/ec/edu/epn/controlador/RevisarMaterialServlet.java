package ec.edu.epn.controlador;

import ec.edu.epn.dao.MaterialDAO;
import ec.edu.epn.modelo.EstadoMaterial;
import ec.edu.epn.modelo.Usuario;
import ec.edu.epn.util.MaterialValidacion;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class RevisarMaterialServlet extends HttpServlet {

    private final MaterialDAO materialDAO = new MaterialDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        Usuario admin = AdminAuth.requerirAdmin(request, response);
        if (admin == null) {
            return;
        }

        Long idMaterial = parseId(request.getParameter("idMaterial"));
        String accion = trim(request.getParameter("accion"));
        String comentario = trim(request.getParameter("comentario"));

        if (idMaterial == null) {
            redirigirConError(request, response, "Material no válido.");
            return;
        }

        EstadoMaterial nuevoEstado;
        String mensajeExito;
        if ("aprobar".equalsIgnoreCase(accion)) {
            nuevoEstado = EstadoMaterial.APROBADO;
            mensajeExito = "Material aprobado. Ya está disponible para estudiantes.";
        } else if ("rechazar".equalsIgnoreCase(accion)) {
            String errorComentario = MaterialValidacion.validarComentarioRechazo(comentario).orElse(null);
            if (errorComentario != null) {
                redirigirConError(request, response, errorComentario);
                return;
            }
            nuevoEstado = EstadoMaterial.RECHAZADO;
            mensajeExito = "Material rechazado. El tutor verá el motivo en su listado.";
        } else {
            redirigirConError(request, response, "Acción no reconocida.");
            return;
        }

        try {
            materialDAO.revisarMaterial(
                    idMaterial,
                    nuevoEstado,
                    admin.getId(),
                    nuevoEstado == EstadoMaterial.RECHAZADO ? comentario : null
            );
        } catch (IllegalStateException e) {
            redirigirConError(request, response, e.getMessage());
            return;
        } catch (RuntimeException e) {
            redirigirConError(request, response, "No se pudo completar la revisión.");
            return;
        }

        redirigirConExito(request, response, mensajeExito);
    }

    private Long parseId(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(valor.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void redirigirConExito(HttpServletRequest request, HttpServletResponse response, String mensaje)
            throws IOException {
        guardarFlash(request, "exito", mensaje);
        response.sendRedirect(request.getContextPath() + "/admin/materiales");
    }

    private void redirigirConError(HttpServletRequest request, HttpServletResponse response, String mensaje)
            throws IOException {
        guardarFlash(request, "error", mensaje);
        response.sendRedirect(request.getContextPath() + "/admin/materiales");
    }

    private void guardarFlash(HttpServletRequest request, String clave, String mensaje) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute(clave, mensaje);
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
