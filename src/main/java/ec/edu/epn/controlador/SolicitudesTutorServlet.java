package ec.edu.epn.controlador;

import ec.edu.epn.dao.SolicitudTutoriaDAO;
import ec.edu.epn.modelo.Rol;
import ec.edu.epn.modelo.SolicitudTutoria;
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

@WebServlet("/solicitudes-tutor")
public class SolicitudesTutorServlet extends HttpServlet {

    private SolicitudTutoriaDAO solicitudDAO;

    @Override
    public void init() {
        solicitudDAO = new SolicitudTutoriaDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuario = session != null
                ? (Usuario) session.getAttribute("usuarioLogueado")
                : null;

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (usuario.getRol() != Rol.TUTOR) {
            response.sendRedirect(request.getContextPath() + destinoPorRol(usuario.getRol()));
            return;
        }

        // Cargar solicitudes entrantes del tutor
        List<SolicitudTutoria> solicitudes = solicitudDAO.listarPorTutor(usuario.getId());
        request.setAttribute("solicitudes", solicitudes);
        request.setAttribute("tutorPerfil", TutorPerfilVista.desde(usuario));

        // Pasar mensajes de éxito/error que dejó GestionSolicitudServlet en sesión
        transferirMensaje(session, request, "exito");
        transferirMensaje(session, request, "error");

        request.getRequestDispatcher("/WEB-INF/tutor/bandeja-solicitudes-tutor.jsp")
               .forward(request, response);
    }

    /**
     * Mueve un atributo de la sesión al request (flash message).
     * Lo elimina de la sesión para que no reaparezca en la siguiente carga.
     */
    private void transferirMensaje(HttpSession session, HttpServletRequest request, String clave) {
        Object valor = session.getAttribute(clave);
        if (valor != null) {
            request.setAttribute(clave, valor);
            session.removeAttribute(clave);
        }
    }

    private String destinoPorRol(Rol rol) {
        return switch (rol) {
            case ESTUDIANTE -> "/estudiante/inicio";
            case TUTOR      -> "/tutor/inicio";
            case ADMIN      -> "/admin/inicio";
        };
    }
}