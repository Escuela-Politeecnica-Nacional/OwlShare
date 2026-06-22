package ec.edu.epn.modelo;

import java.util.List;

public class TutorResumen {

    private final Long id;
    private final String nombreCompleto;
    private final String email;
    private final String carrera;
    private final String semestre;
    private final List<String> materias;

    public TutorResumen(Long id, String nombreCompleto, String email,
                        String carrera, String semestre, List<String> materias) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.carrera = carrera;
        this.semestre = semestre;
        this.materias = materias;
    }

    public Long getId() {
        return id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public String getCarrera() {
        return carrera;
    }

    public String getSemestre() {
        return semestre;
    }

    public List<String> getMaterias() {
        return materias;
    }
}
