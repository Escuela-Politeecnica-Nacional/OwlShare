package ec.edu.epn.controlador;

import ec.edu.epn.modelo.Rol;
import ec.edu.epn.modelo.Usuario;
import ec.edu.epn.util.SesionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

final class AdminAuth {

    private AdminAuth() {
    }

    static Usuario requerirAdmin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Usuario usuario = SesionUtil.obtenerUsuario(request);
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        if (usuario.getRol() != Rol.ADMIN) {
            response.sendRedirect(request.getContextPath() + destinoPorRol(usuario.getRol()));
            return null;
        }
        request.setAttribute("adminNombre", nombreCompleto(usuario));
        return usuario;
    }

    private static String nombreCompleto(Usuario usuario) {
        StringBuilder nombre = new StringBuilder(usuario.getNombre());
        if (usuario.getApellido() != null && !usuario.getApellido().isBlank()) {
            nombre.append(' ').append(usuario.getApellido());
        }
        return nombre.toString().trim();
    }

    private static String destinoPorRol(Rol rol) {
        return switch (rol) {
            case ESTUDIANTE -> "/estudiante/inicio";
            case TUTOR -> "/tutor/inicio";
            case ADMIN -> "/admin/inicio";
        };
    }
}
