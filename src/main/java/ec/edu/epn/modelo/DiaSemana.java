package ec.edu.epn.modelo;

import java.time.DayOfWeek;
import java.time.LocalDate;

public enum DiaSemana {
    LUNES(1, "Lunes", DayOfWeek.MONDAY),
    MARTES(2, "Martes", DayOfWeek.TUESDAY),
    MIERCOLES(3, "Miércoles", DayOfWeek.WEDNESDAY),
    JUEVES(4, "Jueves", DayOfWeek.THURSDAY),
    VIERNES(5, "Viernes", DayOfWeek.FRIDAY),
    SABADO(6, "Sábado", DayOfWeek.SATURDAY),
    DOMINGO(7, "Domingo", DayOfWeek.SUNDAY);

    private final int orden;
    private final String etiqueta;
    private final DayOfWeek dayOfWeek;

    DiaSemana(int orden, String etiqueta, DayOfWeek dayOfWeek) {
        this.orden = orden;
        this.etiqueta = etiqueta;
        this.dayOfWeek = dayOfWeek;
    }

    public int getOrden() {
        return orden;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public static DiaSemana desdeFechaIso(String fechaIso) {
        LocalDate fecha = LocalDate.parse(fechaIso);
        return desdeDayOfWeek(fecha.getDayOfWeek());
    }

    public static DiaSemana desdeDayOfWeek(DayOfWeek dayOfWeek) {
        for (DiaSemana dia : values()) {
            if (dia.dayOfWeek == dayOfWeek) {
                return dia;
            }
        }
        throw new IllegalArgumentException("Día no reconocido: " + dayOfWeek);
    }

    public static DiaSemana parse(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Día de la semana obligatorio.");
        }
        return DiaSemana.valueOf(valor.trim().toUpperCase());
    }
}
