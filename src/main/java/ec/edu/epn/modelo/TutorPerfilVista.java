package ec.edu.epn.modelo;

import ec.edu.epn.util.MateriaUtil;

import java.util.List;

/**
 * Datos del tutor expuestos a las vistas JSP del panel (dashboard, perfil, etc.).
 */
public class TutorPerfilVista {

    private final String nombre;
    private final String apellido;
    private final Semestre semestre;
    private final Carrera carrera;
    private final String descripcionProfesional;
    private final List<String> codigosMateriaRelacionadas;

    public TutorPerfilVista(String nombre, String apellido, Semestre semestre, Carrera carrera,
                             String descripcionProfesional, List<String> codigosMateriaRelacionadas) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.semestre = semestre;
        this.carrera = carrera;
        this.descripcionProfesional = descripcionProfesional;
        this.codigosMateriaRelacionadas = codigosMateriaRelacionadas;
    }

    public static TutorPerfilVista desde(Usuario usuario) {
        return new TutorPerfilVista(
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getSemestre(),
                usuario.getCarrera(),
                null,
                MateriaUtil.toList(usuario.getMaterias())
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

    public String getDescripcionProfesional() {
        return descripcionProfesional;
    }

    public List<String> getCodigosMateriaRelacionadas() {
        return codigosMateriaRelacionadas;
    }
}
