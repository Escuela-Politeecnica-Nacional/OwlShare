package ec.edu.epn.modelo;

/**
 * Sesión de tutoría agendada en la agenda personal del tutor.
 */
public class SesionAgendadaVista {

    private final Long solicitudId;
    private final String estudiante;
    private final String materiaNombre;
    private final String materiaCodigo;
    private final String fecha;
    private final String horaInicio;
    private final String horaFin;
    private final String estado;
    private final String estadoEtiqueta;

    public SesionAgendadaVista(Long solicitudId, String estudiante, String materiaNombre,
                               String materiaCodigo, String fecha, String horaInicio,
                               String horaFin, EstadoSolicitud estado) {
        this.solicitudId = solicitudId;
        this.estudiante = estudiante;
        this.materiaNombre = materiaNombre;
        this.materiaCodigo = materiaCodigo;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado.name().toLowerCase();
        this.estadoEtiqueta = etiquetaEstado(estado);
    }

    public static SesionAgendadaVista desde(SolicitudTutoria solicitud) {
        Usuario estudiante = solicitud.getEstudiante();
        Horario horario = solicitud.getHorario();
        MateriaCatalogo materia = solicitud.getMateria();

        return new SesionAgendadaVista(
                solicitud.getId(),
                nombreCompleto(estudiante),
                materia != null ? materia.getNombre() : "—",
                materia != null ? materia.getCodigo() : "—",
                horario.getFecha(),
                horario.getHoraInicio(),
                horario.getHoraFin(),
                solicitud.getEstado()
        );
    }

    private static String nombreCompleto(Usuario usuario) {
        StringBuilder nombre = new StringBuilder(usuario.getNombre());
        if (usuario.getApellido() != null && !usuario.getApellido().isBlank()) {
            nombre.append(' ').append(usuario.getApellido());
        }
        return nombre.toString().trim();
    }

    private static String etiquetaEstado(EstadoSolicitud estado) {
        return switch (estado) {
            case PENDIENTE -> "Pendiente";
            case ACEPTADA -> "Confirmada";
            case RECHAZADA -> "Rechazada";
            case CANCELADA -> "Cancelada";
        };
    }

    public Long getSolicitudId() {
        return solicitudId;
    }

    public String getEstudiante() {
        return estudiante;
    }

    public String getMateriaNombre() {
        return materiaNombre;
    }

    public String getMateriaCodigo() {
        return materiaCodigo;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public String getEstado() {
        return estado;
    }

    public String getEstadoEtiqueta() {
        return estadoEtiqueta;
    }
}
