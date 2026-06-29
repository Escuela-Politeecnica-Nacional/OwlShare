package ec.edu.epn.modelo;

/**
 * Datos de una solicitud expuestos en la bandeja del tutor.
 */
public class SolicitudTutoriaVista {

    private final Long id;
    private final String nombreEstudiante;
    private final String materiaNombre;
    private final String materiaCodigo;
    private final String fecha;
    private final String horaInicio;
    private final String horaFin;
    private final String comentario;
    private final EstadoSolicitud estado;

    public SolicitudTutoriaVista(Long id, String nombreEstudiante, String materiaNombre,
                                 String materiaCodigo, String fecha, String horaInicio,
                                 String horaFin, String comentario, EstadoSolicitud estado) {
        this.id = id;
        this.nombreEstudiante = nombreEstudiante;
        this.materiaNombre = materiaNombre;
        this.materiaCodigo = materiaCodigo;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.comentario = comentario;
        this.estado = estado;
    }

    public static SolicitudTutoriaVista desde(SolicitudTutoria solicitud) {
        Usuario estudiante = solicitud.getEstudiante();
        Horario horario = solicitud.getHorario();
        MateriaCatalogo materia = solicitud.getMateria();

        return new SolicitudTutoriaVista(
                solicitud.getId(),
                nombreCompleto(estudiante),
                materia != null ? materia.getNombre() : "—",
                materia != null ? materia.getCodigo() : "—",
                horario.getFecha(),
                horario.getHoraInicio(),
                horario.getHoraFin(),
                solicitud.getComentario(),
                solicitud.getEstado()
        );
    }

    private static String nombreCompleto(Usuario usuario) {
        StringBuilder nombre = new StringBuilder(usuario.getNombre());
        if (usuario.getSegundoNombre() != null && !usuario.getSegundoNombre().isBlank()) {
            nombre.append(' ').append(usuario.getSegundoNombre());
        }
        nombre.append(' ').append(usuario.getApellido());
        if (usuario.getSegundoApellido() != null && !usuario.getSegundoApellido().isBlank()) {
            nombre.append(' ').append(usuario.getSegundoApellido());
        }
        return nombre.toString().trim();
    }

    public Long getId() {
        return id;
    }

    public String getNombreEstudiante() {
        return nombreEstudiante;
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

    public String getComentario() {
        return comentario;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public String getEstadoClave() {
        return estado.name().toLowerCase();
    }

    public String getEstadoEtiqueta() {
        return switch (estado) {
            case PENDIENTE -> "Pendiente";
            case ACEPTADA -> "Aceptada";
            case RECHAZADA -> "Rechazada";
            case CANCELADA -> "Cancelada";
        };
    }
}
