package ec.edu.epn.util;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Reglas de validación para campos de texto de formularios.
 */
public final class InputValidacion {

    public static final int EMAIL_MAX = 254;
    public static final int NOMBRE_MAX = 50;
    public static final int PASSWORD_MIN = 8;
    public static final int PASSWORD_MAX = 72;
    public static final int COMENTARIO_MENTORIA_MAX = 300;

    private static final Pattern EMAIL = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern NOMBRE = Pattern.compile(
            "^[\\p{L}][\\p{L}\\s'-]{0," + (NOMBRE_MAX - 1) + "}$"
    );

    private static final Pattern COMENTARIO_MENTORIA = Pattern.compile(
            "^[\\p{L}\\p{N}\\s.,;:!?¿¡()\\-\"'\\n]{1," + COMENTARIO_MENTORIA_MAX + "}$"
    );

    private InputValidacion() {
    }

    public static Optional<String> validarEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.of("El correo electrónico es obligatorio.");
        }
        String valor = email.trim();
        if (valor.length() > EMAIL_MAX) {
            return Optional.of("El correo no puede superar " + EMAIL_MAX + " caracteres.");
        }
        if (!EMAIL.matcher(valor).matches()) {
            return Optional.of("El formato del correo electrónico no es válido.");
        }
        return Optional.empty();
    }

    public static Optional<String> validarPassword(String password) {
        if (password == null || password.isBlank()) {
            return Optional.of("La contraseña es obligatoria.");
        }
        if (password.length() < PASSWORD_MIN) {
            return Optional.of("La contraseña debe tener al menos " + PASSWORD_MIN + " caracteres.");
        }
        if (password.length() > PASSWORD_MAX) {
            return Optional.of("La contraseña no puede superar " + PASSWORD_MAX + " caracteres.");
        }
        if (password.chars().anyMatch(Character::isWhitespace)) {
            return Optional.of("La contraseña no puede contener espacios.");
        }
        return Optional.empty();
    }

    public static Optional<String> validarNombre(String valor, String etiqueta, boolean obligatorio) {
        if (valor == null || valor.isBlank()) {
            return obligatorio
                    ? Optional.of("El " + etiqueta + " es obligatorio.")
                    : Optional.empty();
        }
        String limpio = valor.trim();
        if (!NOMBRE.matcher(limpio).matches()) {
            return Optional.of("El " + etiqueta + " solo puede contener letras, espacios, guiones o apóstrofes.");
        }
        return Optional.empty();
    }

    public static Optional<String> validarComentarioMentoria(String comentario) {
        if (comentario == null || comentario.isBlank()) {
            return Optional.of("Debes describir tu duda o motivo.");
        }
        String limpio = comentario.trim();
        if (limpio.length() > COMENTARIO_MENTORIA_MAX) {
            return Optional.of("El mensaje no puede superar " + COMENTARIO_MENTORIA_MAX + " caracteres.");
        }
        if (!COMENTARIO_MENTORIA.matcher(limpio).matches()) {
            return Optional.of("El mensaje contiene caracteres no permitidos.");
        }
        return Optional.empty();
    }
}
