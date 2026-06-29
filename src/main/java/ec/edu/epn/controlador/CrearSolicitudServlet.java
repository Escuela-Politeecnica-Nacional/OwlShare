package ec.edu.epn.controlador;

import ec.edu.epn.dao.DisponibilidadTutorDAO;
import ec.edu.epn.dao.HorarioDAO;
import ec.edu.epn.dao.MateriaCatalogoDAO;
import ec.edu.epn.dao.SolicitudTutoriaDAO;
import ec.edu.epn.dao.UsuarioDAO;
import ec.edu.epn.modelo.EstadoSolicitud;
import ec.edu.epn.modelo.Rol;
import ec.edu.epn.modelo.Horario;
import ec.edu.epn.modelo.MateriaCatalogo;
import ec.edu.epn.modelo.Usuario;
import ec.edu.epn.catalogo.MateriasCatalogo;
import ec.edu.epn.util.HorarioUtil;
import ec.edu.epn.util.InputValidacion;
import ec.edu.epn.util.JsonUtil;
import ec.edu.epn.util.MateriaTutorReglas;
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
    private final MateriaCatalogoDAO materiaCatalogoDAO = new MateriaCatalogoDAO();
    private final DisponibilidadTutorDAO disponibilidadDAO = new DisponibilidadTutorDAO();
    private final HorarioDAO horarioDAO = new HorarioDAO();
    private final SolicitudTutoriaDAO solicitudTutoriaDAO = new SolicitudTutoriaDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            procesarSolicitud(request, response);
        } catch (RuntimeException e) {
            responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No se pudo procesar la solicitud.");
        }
    }

    private void procesarSolicitud(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String estudianteIdParam = trim(request.getParameter("estudianteId"));
        String tutorIdParam = trim(request.getParameter("tutorId"));
        String horarioIdParam = trim(request.getParameter("horarioId"));
        String codigoMateria = trim(request.getParameter("codigoMateria"));
        String fecha = trim(request.getParameter("fecha"));
        String horaInicio = trim(request.getParameter("horaInicio"));
        String horaFin = trim(request.getParameter("horaFin"));
        String comentario = trim(request.getParameter("comentario"));

        String error = validarCampos(estudianteIdParam, tutorIdParam, horarioIdParam,
                codigoMateria, fecha, horaInicio, horaFin, comentario);
        if (error != null) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST, error);
            return;
        }

        comentario = comentario.trim();
        horaInicio = HorarioUtil.normalizarHora(horaInicio);
        horaFin = HorarioUtil.normalizarHora(horaFin);

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
        if (tutor.getCarrera() == null || tutor.getSemestre() == null) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "El tutor no tiene carrera o semestre registrados.");
            return;
        }
        if (!MateriaTutorReglas.esSemestreValidoParaTutor(tutor.getSemestre())) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "El tutor no tiene un semestre válido para ofrecer tutorías (2.º–9.º).");
            return;
        }
        if (!MateriaUtil.contieneCodigo(MateriaUtil.parseCodigos(tutor.getMaterias()), codigoMateria)) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "El tutor no ofrece la materia indicada.");
            return;
        }
        if (!MateriaTutorReglas.esMateriaPermitida(tutor.getCarrera(), tutor.getSemestre(), codigoMateria)) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "La materia no está permitida según el semestre y la carrera del tutor.");
            return;
        }

        if (MateriasCatalogo.buscarEnCarrera(tutor.getCarrera(), codigoMateria) == null) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "La materia indicada no existe en el catálogo de la carrera del tutor.");
            return;
        }

        MateriaCatalogo materia;
        try {
            materia = materiaCatalogoDAO.obtenerOCrear(codigoMateria, tutor.getCarrera());
        } catch (IllegalArgumentException e) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        } catch (RuntimeException e) {
            responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No se pudo registrar la materia.");
            return;
        }

        Horario horario;
        if (!horarioIdParam.isEmpty()) {
            long horarioId = Long.parseLong(horarioIdParam);
            Optional<Horario> horarioOpt = horarioDAO.buscarPorId(horarioId);
            if (horarioOpt.isEmpty()) {
                responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "El horario indicado no existe.");
                return;
            }
            horario = horarioOpt.get();
            if (!horario.getTutor().getId().equals(tutorId)) {
                responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "El horario no pertenece al tutor indicado.");
                return;
            }
            if (!horario.isDisponible()) {
                responderError(response, HttpServletResponse.SC_CONFLICT,
                        "El horario ya no está disponible.");
                return;
            }
            try {
                if (solicitudTutoriaDAO.horarioTieneSolicitudActiva(horarioId)) {
                    responderError(response, HttpServletResponse.SC_CONFLICT,
                            "El horario ya tiene una solicitud activa.");
                    return;
                }
            } catch (RuntimeException e) {
                responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "No se pudo validar la disponibilidad del horario.");
                return;
            }
        } else {
            try {
                if (!disponibilidadDAO.cubreHorario(tutorId, fecha, horaInicio, horaFin)) {
                    responderError(response, HttpServletResponse.SC_CONFLICT,
                            "El horario solicitado no está dentro de la disponibilidad del tutor.");
                    return;
                }
                if (solicitudTutoriaDAO.existeConflictoHorarioTutor(tutorId, fecha, horaInicio, horaFin)) {
                    responderError(response, HttpServletResponse.SC_CONFLICT,
                            "El tutor ya tiene una tutoría agendada en ese horario.");
                    return;
                }
            } catch (RuntimeException e) {
                responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "No se pudo validar la disponibilidad del horario.");
                return;
            }

            horario = horarioDAO.buscarPorTutorFechaYHoras(tutorId, fecha, horaInicio, horaFin)
                    .orElseGet(() -> horarioDAO.crear(tutorId, fecha, horaInicio, horaFin));
        }

        Long solicitudId;
        try {
            solicitudId = solicitudTutoriaDAO.crearSolicitud(
                    estudianteId, horario.getId(), materia.getCodigo(), comentario);
        } catch (IllegalArgumentException e) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
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
            writer.write("\"id\":" + solicitudId + ",");
            writer.write("\"horarioId\":" + horario.getId() + ",");
            writer.write("\"codigoMateria\":\"" + JsonUtil.escape(materia.getCodigo()) + "\",");
            writer.write("\"mensaje\":\"Solicitud creada exitosamente.\",");
            writer.write("\"estado\":\"" + EstadoSolicitud.PENDIENTE.name() + "\"");
            writer.write("}");
        }
    }

    private String validarCampos(String estudianteId, String tutorId, String horarioId,
                                 String codigoMateria, String fecha, String horaInicio,
                                 String horaFin, String comentario) {
        String errorComentario = InputValidacion.validarComentarioMentoria(comentario).orElse(null);
        if (errorComentario != null) {
            return errorComentario;
        }
        if (estudianteId.isEmpty()) {
            return "El parámetro 'estudianteId' es obligatorio.";
        }
        if (tutorId.isEmpty()) {
            return "El parámetro 'tutorId' es obligatorio.";
        }
        if (codigoMateria.isEmpty()) {
            return "El parámetro 'codigoMateria' es obligatorio.";
        }
        if (horarioId.isEmpty()) {
            if (fecha.isEmpty()) {
                return "Debe indicar 'horarioId' o 'fecha' con 'horaInicio' y 'horaFin'.";
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
        } else {
            try {
                Long.parseLong(horarioId);
            } catch (NumberFormatException e) {
                return "El parámetro 'horarioId' debe ser numérico.";
            }
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
}
