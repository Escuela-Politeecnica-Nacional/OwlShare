package ec.edu.epn.modelo;

/**
 * Conteo de materiales del tutor agrupados por estado de revisión.
 */
public class MaterialResumenTutor {

    private final long total;
    private final long aprobados;
    private final long pendientes;
    private final long rechazados;

    public MaterialResumenTutor(long total, long aprobados, long pendientes, long rechazados) {
        this.total = total;
        this.aprobados = aprobados;
        this.pendientes = pendientes;
        this.rechazados = rechazados;
    }

    public static MaterialResumenTutor vacio() {
        return new MaterialResumenTutor(0, 0, 0, 0);
    }

    public long getTotal() {
        return total;
    }

    public long getAprobados() {
        return aprobados;
    }

    public long getPendientes() {
        return pendientes;
    }

    public long getRechazados() {
        return rechazados;
    }
}
