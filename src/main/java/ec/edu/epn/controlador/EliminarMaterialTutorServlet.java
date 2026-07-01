package ec.edu.epn.controlador;

import ec.edu.epn.dao.MaterialDAO;
import ec.edu.epn.modelo.Usuario;
import ec.edu.epn.util.MaterialAlmacenamiento;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class EliminarMaterialTutorServlet extends HttpServlet {

    private final MaterialDAO materialDAO = new MaterialDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        Usuario tutor = TutorAuth.requerirTutor(request, response);
        if (tutor == null) {
            return;
        }

        Long idMaterial = parseId(request.getParameter("idMaterial"));
        if (idMaterial == null) {
            redirigirConError(request, response, "Material no válido.");
            return;
        }

        try {
            var rutaOpt = materialDAO.eliminarPorTutor(idMaterial, tutor.getId());
            if (rutaOpt.isEmpty()) {
                redirigirConError(request, response, "No se encontró el material o no puedes eliminarlo.");
                return;
            }
            MaterialAlmacenamiento.eliminarSiExiste(rutaOpt.get());
        } catch (IllegalStateException e) {
            redirigirConError(request, response, e.getMessage());
            return;
        } catch (RuntimeException e) {
            redirigirConError(request, response, "No se pudo eliminar el material.");
            return;
        }

        redirigirConExito(request, response, "Material eliminado correctamente.");
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
        guardarFlash(request, "flashMensaje", mensaje);
        response.sendRedirect(request.getContextPath() + "/tutor/materiales");
    }

    private void redirigirConError(HttpServletRequest request, HttpServletResponse response, String mensaje)
            throws IOException {
        guardarFlash(request, "error", mensaje);
        response.sendRedirect(request.getContextPath() + "/tutor/materiales");
    }

    private void guardarFlash(HttpServletRequest request, String clave, String mensaje) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute(clave, mensaje);
        }
    }
}
