package ec.edu.epn.util;

import java.util.regex.Pattern;

public final class HorarioUtil {

    private static final Pattern HORA_PATTERN = Pattern.compile("^([01]?\\d|2[0-3]):[0-5]\\d$");

    private HorarioUtil() {
    }

    public static boolean esHoraValida(String hora) {
        return hora != null && HORA_PATTERN.matcher(hora.trim()).matches();
    }

    public static boolean esRangoValido(String horaInicio, String horaFin) {
        if (!esHoraValida(horaInicio) || !esHoraValida(horaFin)) {
            return false;
        }
        return aMinutos(horaInicio) < aMinutos(horaFin);
    }

    public static boolean haySolapamiento(String inicio1, String fin1, String inicio2, String fin2) {
        int a1 = aMinutos(inicio1);
        int b1 = aMinutos(fin1);
        int a2 = aMinutos(inicio2);
        int b2 = aMinutos(fin2);
        return a1 < b2 && a2 < b1;
    }

    public static boolean estaContenidoEn(String inicio, String fin, String franjaInicio, String franjaFin) {
        return aMinutos(inicio) >= aMinutos(franjaInicio) && aMinutos(fin) <= aMinutos(franjaFin);
    }

    static int aMinutos(String hora) {
        String[] partes = normalizarHora(hora).split(":");
        return Integer.parseInt(partes[0]) * 60 + Integer.parseInt(partes[1]);
    }

    public static String normalizarHora(String hora) {
        if (hora == null) {
            return "";
        }
        String limpia = hora.trim();
        if (!esHoraValida(limpia)) {
            return limpia;
        }
        String[] partes = limpia.split(":");
        return String.format("%02d:%02d", Integer.parseInt(partes[0]), Integer.parseInt(partes[1]));
    }
}
