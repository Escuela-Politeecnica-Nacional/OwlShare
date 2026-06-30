package ec.edu.epn.controlador;

import ec.edu.epn.dao.UsuarioDAO;
import ec.edu.epn.modelo.TutorPerfilVista;
import ec.edu.epn.modelo.Usuario;
import ec.edu.epn.util.MateriaTutorReglas;
import ec.edu.epn.util.MateriaUtil;
import ec.edu.epn.util.SesionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

public class PerfilTutorServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario tutor = TutorAuth.requerirTutor(request, response);
        if (tutor == null) {
            return;
        }

        cargarFormulario(request, tutor, MateriaUtil.toList(tutor.getMaterias()));
        transferirMensaje(request.getSession(false), request, "exito");
        transferirMensaje(request.getSession(false), request, "error");

        request.getRequestDispatcher("/WEB-INF/tutor/perfil-tutor.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        Usuario tutor = TutorAuth.requerirTutor(request, response);
        if (tutor == null) {
            return;
        }

        String[] materiasParam = request.getParameterValues("materias");
        List<String> codigosSeleccionados = materiasParam == null
                ? List.of()
                : Arrays.stream(materiasParam)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        String error = MateriaTutorReglas
                .validarCodigosSeleccionados(tutor.getCarrera(), tutor.getSemestre(), codigosSeleccionados)
                .orElse(null);
        if (error != null) {
            request.setAttribute("error", error);
            cargarFormulario(request, tutor, codigosSeleccionados);
            request.getRequestDispatcher("/WEB-INF/tutor/perfil-tutor.jsp").forward(request, response);
            return;
        }

        String materiasCsv = String.join(",", new LinkedHashSet<>(codigosSeleccionados));
        try {
            usuarioDAO.actualizarMaterias(tutor.getId(), materiasCsv);
        } catch (RuntimeException e) {
            request.setAttribute("error", "No se pudieron guardar las materias. Intenta de nuevo.");
            cargarFormulario(request, tutor, codigosSeleccionados);
            request.getRequestDispatcher("/WEB-INF/tutor/perfil-tutor.jsp").forward(request, response);
            return;
        }

        Usuario actualizado = usuarioDAO.buscarPorId(tutor.getId()).orElse(tutor);
        sincronizarSesion(request, actualizado);
        guardarMensajeFlash(request, "exito", "Materias actualizadas correctamente.");
        response.sendRedirect(request.getContextPath() + "/tutor/perfil");
    }

    private void cargarFormulario(HttpServletRequest request, Usuario tutor, List<String> codigosSeleccionados) {
        request.setAttribute("tutorPerfil", TutorPerfilVista.desde(tutor));
        if (tutor.getCarrera() == null || tutor.getSemestre() == null) {
            request.setAttribute("materiasPermitidasJson", "[]");
            request.setAttribute("materiasSeleccionadasJson", "[]");
            if (request.getAttribute("error") == null) {
                request.setAttribute("error",
                        "Tu perfil no tiene carrera o semestre configurados. No puedes editar materias.");
            }
            return;
        }
        request.setAttribute("materiasPermitidasJson",
                MateriaTutorReglas.materiasPermitidasJson(tutor.getCarrera(), tutor.getSemestre()));
        request.setAttribute("materiasSeleccionadasJson",
                MateriaTutorReglas.codigosSeleccionadosJson(codigosSeleccionados));
    }

    private void sincronizarSesion(HttpServletRequest request, Usuario usuario) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.setAttribute(SesionUtil.ATTR_USUARIO, usuario);
        session.setAttribute(SesionUtil.ATTR_USUARIO_ID, usuario.getId());
    }

    private void guardarMensajeFlash(HttpServletRequest request, String clave, String mensaje) {
        request.getSession(true).setAttribute(clave, mensaje);
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
