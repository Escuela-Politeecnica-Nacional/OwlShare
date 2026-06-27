package ec.edu.epn.modelo;

public enum Semestre {
    PRIMERO(1, "1.º Semestre"),
    SEGUNDO(2, "2.º Semestre"),
    TERCERO(3, "3.º Semestre"),
    CUARTO(4, "4.º Semestre"),
    QUINTO(5, "5.º Semestre"),
    SEXTO(6, "6.º Semestre"),
    SEPTIMO(7, "7.º Semestre"),
    OCTAVO(8, "8.º Semestre"),
    NOVENO(9, "9.º Semestre"),
    DECIMO(10, "10.º Semestre");

    private final int numero;
    private final String nombre;

    Semestre(int numero, String nombre) {
        this.numero = numero;
        this.nombre = nombre;
    }

    public int getNumero() {
        return numero;
    }

    public String getNombre() {
        return nombre;
    }

    public static Semestre porNumero(int numero) {
        for (Semestre semestre : values()) {
            if (semestre.numero == numero) {
                return semestre;
            }
        }
        throw new IllegalArgumentException("No existe semestre con número: " + numero);
    }

    public static Semestre porNumeroOpcional(int numero) {
        for (Semestre semestre : values()) {
            if (semestre.numero == numero) {
                return semestre;
            }
        }
        return null;
    }
}
