package ec.edu.epn.util;

import ec.edu.epn.modelo.Carrera;
import ec.edu.epn.modelo.Semestre;
import ec.edu.epn.modelo.Usuario;
import jakarta.servlet.http.Part;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.regex.Pattern;

public final class MaterialValidacion {

    public static final int TITULO_MAX = 200;
    public static final int DESCRIPCION_MAX = 500;
    public static final int CATEGORIA_MAX = 100;
    public static final long ARCHIVO_MAX_BYTES = 25L * 1024 * 1024;

    private static final Pattern TITULO = Pattern.compile(
            "^[\\p{L}\\p{N}\\s.,;:!?¿¡()\\-\"'&/+]{1," + TITULO_MAX + "}$"
    );

    private static final Pattern DESCRIPCION = Pattern.compile(
            "^[\\p{L}\\p{N}\\s.,;:!?¿¡()\\-\"'\\n&/+]{1," + DESCRIPCION_MAX + "}$"
    );

    private MaterialValidacion() {
    }

    public static Optional<String> validarTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            return Optional.of("El título del material es obligatorio.");
        }
        String limpio = titulo.trim();
        if (limpio.length() > TITULO_MAX) {
            return Optional.of("El título no puede superar " + TITULO_MAX + " caracteres.");
        }
        if (!TITULO.matcher(limpio).matches()) {
            return Optional.of("El título contiene caracteres no permitidos.");
        }
        return Optional.empty();
    }

    public static Optional<String> validarDescripcion(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            return Optional.of("La descripción del material es obligatoria.");
        }
        String limpio = descripcion.trim();
        if (limpio.length() > DESCRIPCION_MAX) {
            return Optional.of("La descripción no puede superar " + DESCRIPCION_MAX + " caracteres.");
        }
        if (!DESCRIPCION.matcher(limpio).matches()) {
            return Optional.of("La descripción contiene caracteres no permitidos.");
        }
        return Optional.empty();
    }

    public static Optional<String> validarCosto(String costoParam) {
        if (costoParam == null || costoParam.isBlank()) {
            return Optional.of("El precio sugerido es obligatorio.");
        }
        try {
            BigDecimal costo = new BigDecimal(costoParam.trim().replace(',', '.'));
            if (costo.compareTo(BigDecimal.ZERO) < 0) {
                return Optional.of("El precio debe ser mayor o igual a 0.");
            }
            if (costo.scale() > 2) {
                return Optional.of("El precio solo puede tener hasta 2 decimales.");
            }
            if (costo.precision() - costo.scale() > 8) {
                return Optional.of("El precio indicado es demasiado alto.");
            }
        } catch (NumberFormatException e) {
            return Optional.of("El precio indicado no es válido.");
        }
        return Optional.empty();
    }

    public static BigDecimal parseCosto(String costoParam) {
        return new BigDecimal(costoParam.trim().replace(',', '.'));
    }

    public static Optional<String> validarCategoria(String categoria) {
        if (categoria == null || categoria.isBlank()) {
            return Optional.empty();
        }
        String limpio = categoria.trim();
        if (limpio.length() > CATEGORIA_MAX) {
            return Optional.of("La categoría académica no puede superar " + CATEGORIA_MAX + " caracteres.");
        }
        return Optional.empty();
    }

    public static Optional<String> validarCodigoMateriaTutor(Usuario tutor, String codigoMateria) {
        if (codigoMateria == null || codigoMateria.isBlank()) {
            return Optional.of("Debes seleccionar la materia a la que pertenece el material.");
        }
        String codigo = codigoMateria.trim();
        if (!MateriaUtil.contieneCodigo(MateriaUtil.parseCodigos(tutor.getMaterias()), codigo)) {
            return Optional.of("La materia seleccionada no está entre las que ofreces como tutor.");
        }
        Carrera carrera = tutor.getCarrera();
        Semestre semestre = tutor.getSemestre();
        if (carrera == null || semestre == null) {
            return Optional.of("Tu perfil de tutor no tiene carrera o semestre registrados.");
        }
        if (!MateriaTutorReglas.esMateriaPermitida(carrera, semestre, codigo)) {
            return Optional.of("La materia seleccionada no es válida según tu carrera y semestre.");
        }
        return Optional.empty();
    }

    public static Optional<String> validarArchivoPdf(Part archivo) {
        if (archivo == null || archivo.getSize() == 0) {
            return Optional.of("Debes adjuntar un archivo PDF.");
        }
        if (archivo.getSize() > ARCHIVO_MAX_BYTES) {
            return Optional.of("El archivo no puede superar 25 MB.");
        }

        String nombre = archivo.getSubmittedFileName();
        if (nombre == null || nombre.isBlank()) {
            return Optional.of("El archivo adjunto no es válido.");
        }
        if (!nombre.toLowerCase().endsWith(".pdf")) {
            return Optional.of("Solo se permiten archivos con extensión .pdf.");
        }

        String contentType = archivo.getContentType();
        if (contentType != null && !contentType.isBlank()
                && !"application/pdf".equalsIgnoreCase(contentType)
                && !"application/x-pdf".equalsIgnoreCase(contentType)) {
            return Optional.of("El archivo debe ser un documento PDF.");
        }

        return Optional.empty();
    }
}
