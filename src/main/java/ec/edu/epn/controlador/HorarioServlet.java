package ec.edu.epn.controlador;

import ec.edu.epn.dao.HorarioDAO;
import ec.edu.epn.dao.MateriaCatalogoDAO;
import ec.edu.epn.dao.UsuarioDAO;
import ec.edu.epn.modelo.Carrera;
import ec.edu.epn.modelo.Horario;
import ec.edu.epn.modelo.MateriaCatalogo;
import ec.edu.epn.modelo.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * CRUD de horarios propios del tutor autenticado.
 *
 *  GET  /horario                  → lista los horarios del tutor en sesión
 *  POST /horario?accion=crear     → crea un nuevo horario
 *  POST /horario?accion=eliminar  → elimina un horario propio
 *
 * Requiere en sesión:
 *   - "idUsuario" (Long)  → id del tutor
 *   - "carrera"   (Carrera) → carrera del tutor (necesaria para resolver MateriaCatalogo)
 */
@WebServlet("/horario")
public class HorarioServlet extends HttpServlet {

    private HorarioDAO         horarioDAO;
    private MateriaCatalogoDAO materiaCatalogoDAO;
    private UsuarioDAO         usuarioDAO;

    @Override
    public void init() {
        horarioDAO         = new HorarioDAO();
        materiaCatalogoDAO = new MateriaCatalogoDAO();
        usuarioDAO         = new UsuarioDAO();
    }

    // ── GET: listar ───────────────────────────────────────────────────────────

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long idTutor = tutorEnSesion(request, response);
        if (idTutor == null) return;

        List<Horario> horarios = horarioDAO.listarPorTutor(idTutor);
        request.setAttribute("horarios", horarios);
        request.getRequestDispatcher("/horario.jsp").forward(request, response);
    }

    // ── POST: crear o eliminar ────────────────────────────────────────────────

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long idTutor = tutorEnSesion(request, response);
        if (idTutor == null) return;

        String accion = request.getParameter("accion");

        if ("crear".equalsIgnoreCase(accion)) {
            procesarCrear(request, response, idTutor);
        } else if ("eliminar".equalsIgnoreCase(accion)) {
            procesarEliminar(request, response, idTutor);
        } else {
            request.setAttribute("error", "Acción no reconocida.");
            doGet(request, response);
        }
    }

    // ── Crear ─────────────────────────────────────────────────────────────────

    private void procesarCrear(HttpServletRequest request, HttpServletResponse response,
                                Long idTutor) throws ServletException, IOException {

        String codigoMateria = request.getParameter("codigoMateria");
        String fecha         = request.getParameter("fecha");
        String horaInicio    = request.getParameter("horaInicio");
        String horaFin       = request.getParameter("horaFin");

        if (estaVacio(codigoMateria) || estaVacio(fecha)
                || estaVacio(horaInicio) || estaVacio(horaFin)) {
            request.setAttribute("error", "Todos los campos son obligatorios para crear un horario.");
            doGet(request, response);
            return;
        }

        // Verificar duplicado
        Optional<Horario> duplicado = horarioDAO.buscarPorTutorFechaYHoras(
                idTutor, fecha.trim(), horaInicio.trim(), horaFin.trim());
        if (duplicado.isPresent()) {
            request.setAttribute("error", "Ya tienes un horario registrado en esa fecha y franja horaria.");
            doGet(request, response);
            return;
        }

        // Resolver tutor
        Optional<Usuario> tutor = usuarioDAO.buscarPorId(idTutor);
        if (tutor.isEmpty()) {
            request.setAttribute("error", "No se encontró el tutor en sesión.");
            doGet(request, response);
            return;
        }

        // Obtener carrera desde la sesión (se guarda al hacer login)
        Carrera carrera = (Carrera) request.getSession(false).getAttribute("carrera");
        if (carrera == null) {
            request.setAttribute("error", "No se pudo determinar tu carrera. Vuelve a iniciar sesión.");
            doGet(request, response);
            return;
        }

        // Resolver MateriaCatalogo (crea el registro si aún no existe en la tabla)
        MateriaCatalogo materia;
        try {
            materia = materiaCatalogoDAO.obtenerOCrear(codigoMateria.trim(), carrera);
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "La materia seleccionada no existe en el catálogo de tu carrera.");
            doGet(request, response);
            return;
        }

        try {
            horarioDAO.crear(tutor.get(), fecha.trim(), horaInicio.trim(), horaFin.trim());
            request.setAttribute("exito", "Horario creado correctamente.");
        } catch (RuntimeException e) {
            request.setAttribute("error", "Error al guardar el horario: " + e.getMessage());
        }

        doGet(request, response);
    }

    // ── Eliminar ──────────────────────────────────────────────────────────────

    private void procesarEliminar(HttpServletRequest request, HttpServletResponse response,
                                   Long idTutor) throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (estaVacio(idParam)) {
            request.setAttribute("error", "Debes indicar el horario a eliminar.");
            doGet(request, response);
            return;
        }

        long horarioId;
        try {
            horarioId = Long.parseLong(idParam.trim());
        } catch (NumberFormatException e) {
            request.setAttribute("error", "El id del horario no es válido.");
            doGet(request, response);
            return;
        }

        boolean eliminado = horarioDAO.eliminarSiEsPropietario(horarioId, idTutor);
        if (eliminado) {
            request.setAttribute("exito", "Horario eliminado correctamente.");
        } else {
            request.setAttribute("error", "No se encontró el horario o no tienes permiso para eliminarlo.");
        }

        doGet(request, response);
    }

    // ── Utilidades ────────────────────────────────────────────────────────────

    private Long tutorEnSesion(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("idUsuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        return (Long) session.getAttribute("idUsuario");
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.isBlank();
    }
}