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

        Long usuarioId = parseId(session.getAttribute(ATTR_USUARIO_ID));
        if (usuarioId != null) {
            Optional<Usuario> usuarioOpt = USUARIO_DAO.buscarPorId(usuarioId);
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                session.setAttribute(ATTR_USUARIO, usuario);
                session.setAttribute(ATTR_USUARIO_ID, usuario.getId());
                return usuario;
            }
            return null;
        }

        Object usuarioAttr = session.getAttribute(ATTR_USUARIO);
        if (usuarioAttr instanceof Usuario usuario) {
            if (usuario.getId() != null) {
                session.setAttribute(ATTR_USUARIO_ID, usuario.getId());
            }
            return usuario;
        }

        return null;
    }

    private static Long parseId(Object idAttr) {
        if (idAttr instanceof Long id) {
            return id;
        }
        if (idAttr instanceof Number number) {
            return number.longValue();
        }
        return null;
    }

    public static void cerrarSesion(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
