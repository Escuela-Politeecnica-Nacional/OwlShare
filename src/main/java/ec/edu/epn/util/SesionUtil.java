package ec.edu.epn.util;

import ec.edu.epn.dao.UsuarioDAO;
import ec.edu.epn.modelo.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

public final class SesionUtil {

    public static final String ATTR_USUARIO = "usuarioLogueado";
    public static final String ATTR_USUARIO_ID = "usuarioId";

    private static final UsuarioDAO USUARIO_DAO = new UsuarioDAO();

    private SesionUtil() {
    }

    public static void iniciarSesion(HttpServletRequest request, Usuario usuario) {
        HttpSession sessionAnterior = request.getSession(false);
        if (sessionAnterior != null) {
            sessionAnterior.invalidate();
        }

        HttpSession session = request.getSession(true);
        session.setAttribute(ATTR_USUARIO_ID, usuario.getId());
        session.setAttribute(ATTR_USUARIO, usuario);
    }

    public static Usuario obtenerUsuario(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Object usuarioAttr = session.getAttribute(ATTR_USUARIO);
        if (usuarioAttr instanceof Usuario usuario) {
            return usuario;
        }

        Object idAttr = session.getAttribute(ATTR_USUARIO_ID);
        if (!(idAttr instanceof Long usuarioId)) {
            return null;
        }

        Optional<Usuario> usuarioOpt = USUARIO_DAO.buscarPorId(usuarioId);
        if (usuarioOpt.isEmpty()) {
            return null;
        }

        Usuario usuario = usuarioOpt.get();
        session.setAttribute(ATTR_USUARIO, usuario);
        return usuario;
    }

    public static void cerrarSesion(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
