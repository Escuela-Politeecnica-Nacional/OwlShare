package ec.edu.epn.modelo;

public class TrayectoriaSemestre {

    private final int numero;
    private final String nombre;
    private final String estado;

    public TrayectoriaSemestre(int numero, String nombre, String estado) {
        this.numero = numero;
        this.nombre = nombre;
        this.estado = estado;
    }

    public int getNumero() {
        return numero;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEstado() {
        return estado;
    }
}
