package ec.edu.epn.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "materias_catalogo")
public class MateriaCatalogo {

    @Id
    @Column(length = 20)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Carrera carrera;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(nullable = false)
    private int semestre;

    public MateriaCatalogo() {
    }

    public MateriaCatalogo(String codigo, Carrera carrera, String nombre, int semestre) {
        this.codigo = codigo;
        this.carrera = carrera;
        this.nombre = nombre;
        this.semestre = semestre;
    }

    public static MateriaCatalogo desde(Materia materia, Carrera carrera) {
        return new MateriaCatalogo(
                materia.getCodigo(),
                carrera,
                materia.getNombre(),
                materia.getSemestre()
        );
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getSemestre() {
        return semestre;
    }

    public void setSemestre(int semestre) {
        this.semestre = semestre;
    }
}
