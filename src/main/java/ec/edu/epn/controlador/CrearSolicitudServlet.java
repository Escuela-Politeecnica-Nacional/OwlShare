package ec.edu.epn.controlador;

import ec.edu.epn.dao.SolicitudDAO;
import ec.edu.epn.dao.UsuarioDAO;
import ec.edu.epn.modelo.EstadoSolicitud;
import ec.edu.epn.modelo.Rol;
import ec.edu.epn.modelo.Solicitud;
import ec.edu.epn.modelo.Usuario;
import ec.edu.epn.util.CatalogoRegistro;
import ec.edu.epn.util.HorarioUtil;
import ec.edu.epn.util.JsonUtil;
import ec.edu.epn.util.MateriaUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Pattern;

public class CrearSolicitudServlet extends HttpServlet {

    private static final Pattern FECHA_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final SolicitudDAO solicitudDAO = new SolicitudDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String estudianteIdParam = trim(request.getParameter("estudianteId"));
        String tutorIdParam = trim(request.getParameter("tutorId"));
        String codigoMateria = trim(request.getParameter("codigoMateria"));
        String fecha = trim(request.getParameter("fecha"));
        String horaInicio = trim(request.getParameter("horaInicio"));
        String horaFin = trim(request.getParameter("horaFin"));
        String comentario = blankToNull(trim(request.getParameter("comentario")));

        String error = validarCampos(estudianteIdParam, tutorIdParam, codigoMateria,
                fecha, horaInicio, horaFin);
        if (error != null) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST, error);
            return;
        }

        long estudianteId = Long.parseLong(estudianteIdParam);
        long tutorId = Long.parseLong(tutorIdParam);

        Optional<Usuario> estudianteOpt = usuarioDAO.buscarPorId(estudianteId);
        if (estudianteOpt.isEmpty() || estudianteOpt.get().getRol() != Rol.ESTUDIANTE) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "El estudiante indicado no es válido.");
            return;
        }

        Optional<Usuario> tutorOpt = usuarioDAO.buscarPorId(tutorId);
        if (tutorOpt.isEmpty() || tutorOpt.get().getRol() != Rol.TUTOR) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "El tutor indicado no es válido.");
            return;
        }

        Usuario tutor = tutorOpt.get();
        if (!MateriaUtil.contieneCodigo(MateriaUtil.parseCodigos(tutor.getMaterias()), codigoMateria)) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "El tutor no ofrece la materia indicada.");
            return;
        }

        if (CatalogoRegistro.buscarMateriaPorCodigo(codigoMateria) == null) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "La materia indicada no existe en el catálogo.");
            return;
        }

        try {
            if (solicitudDAO.existeConflictoHorarioTutor(tutorId, fecha, horaInicio, horaFin)) {
                responderError(response, HttpServletResponse.SC_CONFLICT,
                        "El tutor no tiene disponibilidad en el horario solicitado.");
                return;
            }
        } catch (RuntimeException e) {
            responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No se pudo validar la disponibilidad del horario.");
            return;
        }

        Solicitud solicitud = new Solicitud();
        solicitud.setEstudianteId(estudianteId);
        solicitud.setTutorId(tutorId);
        solicitud.setCodigoMateria(codigoMateria);
        solicitud.setFecha(fecha);
        solicitud.setHoraInicio(horaInicio);
        solicitud.setHoraFin(horaFin);
        solicitud.setComentario(comentario);
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);

        try {
            solicitudDAO.guardar(solicitud);
        } catch (RuntimeException e) {
            responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No se pudo guardar la solicitud.");
            return;
        }

        response.setStatus(HttpServletResponse.SC_CREATED);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try (PrintWriter writer = response.getWriter()) {
            writer.write("{");
            writer.write("\"id\":" + solicitud.getId() + ",");
            writer.write("\"mensaje\":\"Solicitud creada exitosamente.\",");
            writer.write("\"estado\":\"" + EstadoSolicitud.PENDIENTE.name() + "\"");
            writer.write("}");
        }
    }

    private String validarCampos(String estudianteId, String tutorId, String codigoMateria,
                                 String fecha, String horaInicio, String horaFin) {
        if (estudianteId.isEmpty()) {
            return "El parámetro 'estudianteId' es obligatorio.";
        }
        if (tutorId.isEmpty()) {
            return "El parámetro 'tutorId' es obligatorio.";
        }
        if (codigoMateria.isEmpty()) {
            return "El parámetro 'codigoMateria' es obligatorio.";
        }
        if (fecha.isEmpty()) {
            return "El parámetro 'fecha' es obligatorio (formato yyyy-MM-dd).";
        }
        if (!FECHA_PATTERN.matcher(fecha).matches()) {
            return "El formato de 'fecha' debe ser yyyy-MM-dd.";
        }
        if (!HorarioUtil.esHoraValida(horaInicio)) {
            return "El parámetro 'horaInicio' debe tener formato HH:mm.";
        }
        if (!HorarioUtil.esHoraValida(horaFin)) {
            return "El parámetro 'horaFin' debe tener formato HH:mm.";
        }
        if (!HorarioUtil.esRangoValido(horaInicio, horaFin)) {
            return "La hora de inicio debe ser anterior a la hora de fin.";
        }
        try {
            Long.parseLong(estudianteId);
            Long.parseLong(tutorId);
        } catch (NumberFormatException e) {
            return "Los identificadores de estudiante y tutor deben ser numéricos.";
        }
        return null;
    }

    private void responderError(HttpServletResponse response, int status, String mensaje) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try (PrintWriter writer = response.getWriter()) {
            writer.write("{\"error\":\"" + JsonUtil.escape(mensaje) + "\"}");
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
