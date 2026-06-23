package ec.edu.epn.modelo;

import java.util.List;

public class TutorPerfilDetalle {

    private final Long id;
    private final String nombreCompleto;
    private final String email;
    private final String carrera;
    private final String carreraCodigo;
    private final String semestreActual;
    private final int semestreNumero;
    private final List<TrayectoriaSemestre> trayectoria;
    private final List<MateriaDetalle> materias;

    public TutorPerfilDetalle(Long id, String nombreCompleto, String email,
                              String carrera, String carreraCodigo,
                              String semestreActual, int semestreNumero,
                              List<TrayectoriaSemestre> trayectoria,
                              List<MateriaDetalle> materias) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.carrera = carrera;
        this.carreraCodigo = carreraCodigo;
        this.semestreActual = semestreActual;
        this.semestreNumero = semestreNumero;
        this.trayectoria = trayectoria;
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

    public String getCarreraCodigo() {
        return carreraCodigo;
    }

    public String getSemestreActual() {
        return semestreActual;
    }

    public int getSemestreNumero() {
        return semestreNumero;
    }

    public List<TrayectoriaSemestre> getTrayectoria() {
        return trayectoria;
    }

    public List<MateriaDetalle> getMaterias() {
        return materias;
    }
}
