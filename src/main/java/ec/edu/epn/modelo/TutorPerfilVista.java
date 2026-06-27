package ec.edu.epn.modelo;

/**
 * Datos del tutor expuestos a las vistas JSP del panel (dashboard, perfil, etc.).
 */
public class TutorPerfilVista {

    private final String nombre;
    private final String apellido;

    public TutorPerfilVista(String nombre, String apellido) {
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public static TutorPerfilVista desde(Usuario usuario) {
        return new TutorPerfilVista(
                usuario.getNombre(),
                usuario.getApellido()
        );
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }
}
