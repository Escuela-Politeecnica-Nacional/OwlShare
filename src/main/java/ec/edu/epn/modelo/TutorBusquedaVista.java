package ec.edu.epn.modelo;

import ec.edu.epn.util.MateriaUtil;

import java.util.List;

/**
 * Datos de un tutor mostrados en la grilla de búsqueda del estudiante.
 */
public class TutorBusquedaVista {

    private final Long id;
    private final String nombre;
    private final Carrera carrera;
    private final String descripcionProfesional;
    private final List<String> codigosMateriaRelacionadas;

    public TutorBusquedaVista(Long id, String nombre, Carrera carrera,
                              String descripcionProfesional, List<String> codigosMateriaRelacionadas) {
        this.id = id;
        this.nombre = nombre;
        this.carrera = carrera;
        this.descripcionProfesional = descripcionProfesional;
        this.codigosMateriaRelacionadas = codigosMateriaRelacionadas;
    }

    public static TutorBusquedaVista desde(Usuario usuario) {
        String nombreMostrado = usuario.getNombre();
        if (usuario.getApellido() != null && !usuario.getApellido().isBlank()) {
            nombreMostrado = nombreMostrado + " " + usuario.getApellido();
        }
        return new TutorBusquedaVista(
                usuario.getId(),
                nombreMostrado.trim(),
                usuario.getCarrera(),
                null,
                MateriaUtil.toList(usuario.getMaterias())
        );
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
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
