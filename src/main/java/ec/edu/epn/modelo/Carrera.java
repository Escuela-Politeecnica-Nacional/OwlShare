package ec.edu.epn.modelo;

public enum Carrera {
    SOFTWARE("Software"),
    COMPUTACION("Computación"),
    CIENCIA_DATOS("Ciencia de Datos e Inteligencia Artificial"),
    SISTEMAS_INFORMACION("Sistemas de Información");

    private final String nombre;

    Carrera(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
