package ec.edu.epn.controlador;

import ec.edu.epn.modelo.Rol;
import ec.edu.epn.modelo.TutorPerfilVista;
import ec.edu.epn.modelo.Usuario;
import ec.edu.epn.util.SesionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

final class TutorAuth {

    private TutorAuth() {
    }

    static Usuario requerirTutor(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Usuario usuario = SesionUtil.obtenerUsuario(request);
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        if (usuario.getRol() != Rol.TUTOR) {
            response.sendRedirect(request.getContextPath() + destinoPorRol(usuario.getRol()));
            return null;
        }
        request.setAttribute("tutorPerfil", TutorPerfilVista.desde(usuario));
        return usuario;
    }

    private static String destinoPorRol(Rol rol) {
        return switch (rol) {
            case ESTUDIANTE -> "/estudiante/inicio";
            case TUTOR -> "/tutor/inicio";
            case ADMIN -> "/admin/inicio";
        };
    }
}
