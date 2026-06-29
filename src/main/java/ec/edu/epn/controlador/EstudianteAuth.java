package ec.edu.epn.controlador;

import ec.edu.epn.modelo.EstudiantePerfilVista;
import ec.edu.epn.modelo.Rol;
import ec.edu.epn.modelo.Usuario;
import ec.edu.epn.util.SesionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

final class EstudianteAuth {

    private EstudianteAuth() {
    }

    static Usuario requerirEstudiante(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Usuario usuario = SesionUtil.obtenerUsuario(request);
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        if (usuario.getRol() != Rol.ESTUDIANTE) {
            response.sendRedirect(request.getContextPath() + destinoPorRol(usuario.getRol()));
            return null;
        }
        request.setAttribute("estudiantePerfil", EstudiantePerfilVista.desde(usuario));
        sincronizarSesion(request, usuario);
        return usuario;
    }

    private static void sincronizarSesion(HttpServletRequest request, Usuario usuario) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.setAttribute(SesionUtil.ATTR_USUARIO, usuario);
        session.setAttribute(SesionUtil.ATTR_USUARIO_ID, usuario.getId());
    }

    private static String destinoPorRol(Rol rol) {
        return switch (rol) {
            case ESTUDIANTE -> "/estudiante/inicio";
            case TUTOR -> "/tutor/inicio";
            case ADMIN -> "/admin/inicio";
        };
    }
}
