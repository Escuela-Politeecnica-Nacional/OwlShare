package ec.edu.epn.modelo;

public class MateriaDetalle {

    private final String codigo;
    private final String nombre;
    private final int semestre;

    public MateriaDetalle(String codigo, String nombre, int semestre) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.semestre = semestre;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getSemestre() {
        return semestre;
    }
}
