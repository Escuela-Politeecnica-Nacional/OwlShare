package ec.edu.epn.util;

import ec.edu.epn.modelo.DiaSemana;
import ec.edu.epn.modelo.DisponibilidadTutor;

import java.util.Comparator;
import java.util.List;

public final class DisponibilidadUtil {

    private DisponibilidadUtil() {
    }

    public static boolean horarioCubierto(String fecha, String horaInicio, String horaFin,
                                         List<DisponibilidadTutor> franjas) {
        if (franjas == null || franjas.isEmpty()) {
            return false;
        }
        DiaSemana dia;
        try {
            dia = DiaSemana.desdeFechaIso(fecha);
        } catch (RuntimeException e) {
            return false;
        }

        return franjas.stream()
                .filter(DisponibilidadTutor::isActivo)
                .filter(franja -> franja.getDiaSemana() == dia)
                .anyMatch(franja -> HorarioUtil.estaContenidoEn(
                        horaInicio, horaFin, franja.getHoraInicio(), franja.getHoraFin()));
    }

    public static boolean haySolapamientoConFranjas(DiaSemana dia, String horaInicio, String horaFin,
                                                    List<DisponibilidadTutor> franjas, Long excluirId) {
        return franjas.stream()
                .filter(franja -> excluirId == null || !excluirId.equals(franja.getId()))
                .filter(franja -> franja.getDiaSemana() == dia)
                .filter(DisponibilidadTutor::isActivo)
                .anyMatch(franja -> HorarioUtil.haySolapamiento(
                        horaInicio, horaFin, franja.getHoraInicio(), franja.getHoraFin()));
    }

    public static Comparator<DisponibilidadTutor> ordenPorDiaYHora() {
        return Comparator
                .comparing((DisponibilidadTutor f) -> f.getDiaSemana().getOrden())
                .thenComparing(DisponibilidadTutor::getHoraInicio);
    }
}
