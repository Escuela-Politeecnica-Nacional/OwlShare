package ec.edu.epn.modelo;

/**
 * Franja de disponibilidad semanal para la vista del tutor.
 */
public class DisponibilidadTutorVista {

    private final Long id;
    private final String diaEtiqueta;
    private final String diaClave;
    private final int diaOrden;
    private final String horaInicio;
    private final String horaFin;
    private final boolean activo;

    public DisponibilidadTutorVista(Long id, String diaEtiqueta, String diaClave, int diaOrden,
                                    String horaInicio, String horaFin, boolean activo) {
        this.id = id;
        this.diaEtiqueta = diaEtiqueta;
        this.diaClave = diaClave;
        this.diaOrden = diaOrden;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.activo = activo;
    }

    public static DisponibilidadTutorVista desde(DisponibilidadTutor franja) {
        DiaSemana dia = franja.getDiaSemana();
        return new DisponibilidadTutorVista(
                franja.getId(),
                dia.getEtiqueta(),
                dia.name(),
                dia.getOrden(),
                franja.getHoraInicio(),
                franja.getHoraFin(),
                franja.isActivo()
        );
    }

    public Long getId() {
        return id;
    }

    public String getDiaEtiqueta() {
        return diaEtiqueta;
    }

    public String getDiaClave() {
        return diaClave;
    }

    public int getDiaOrden() {
        return diaOrden;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public boolean isActivo() {
        return activo;
    }
}
