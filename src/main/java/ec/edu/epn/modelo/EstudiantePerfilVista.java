package ec.edu.epn.modelo;

/**
 * Datos del estudiante expuestos a las vistas JSP del panel (dashboard, búsqueda, etc.).
 */
public class EstudiantePerfilVista {

    private final String nombre;
    private final String apellido;
    private final Semestre semestre;
    private final Carrera carrera;

    public EstudiantePerfilVista(String nombre, String apellido, Semestre semestre, Carrera carrera) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.semestre = semestre;
        this.carrera = carrera;
    }

    public static EstudiantePerfilVista desde(Usuario usuario) {
        return new EstudiantePerfilVista(
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getSemestre(),
                usuario.getCarrera()
        );
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public Semestre getSemestre() {
        return semestre;
    }

    public Carrera getCarrera() {
        return carrera;
    }
}
