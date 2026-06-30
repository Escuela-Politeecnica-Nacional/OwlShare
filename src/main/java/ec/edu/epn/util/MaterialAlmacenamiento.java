package ec.edu.epn.util;

import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public final class MaterialAlmacenamiento {

    private static final byte[] PDF_MAGIC = new byte[]{0x25, 0x50, 0x44, 0x46}; // %PDF

    private MaterialAlmacenamiento() {
    }

    public static Path directorioBase() {
        String configurado = System.getenv("MATERIAL_UPLOAD_DIR");
        if (configurado != null && !configurado.isBlank()) {
            return Path.of(configurado);
        }
        return Path.of(System.getProperty("user.home"), "owlshare", "uploads", "materiales");
    }

    /**
     * Guarda el PDF del tutor y devuelve la ruta relativa persistida en BD.
     */
    public static String guardarPdf(Long tutorId, Part archivo) throws IOException {
        validarCabeceraPdf(archivo);

        String nombreOriginal = Path.of(archivo.getSubmittedFileName()).getFileName().toString();
        String nombreSeguro = UUID.randomUUID() + "_" + sanitizarNombre(nombreOriginal);

        Path carpetaTutor = directorioBase().resolve(String.valueOf(tutorId));
        Files.createDirectories(carpetaTutor);

        Path destino = carpetaTutor.resolve(nombreSeguro);
        try (InputStream input = archivo.getInputStream()) {
            Files.copy(input, destino);
        }

        return tutorId + "/" + nombreSeguro;
    }

    public static void eliminarSiExiste(String rutaRelativa) {
        if (rutaRelativa == null || rutaRelativa.isBlank()) {
            return;
        }
        try {
            Path archivo = directorioBase().resolve(rutaRelativa);
            Files.deleteIfExists(archivo);
        } catch (IOException ignored) {
        }
    }

    private static void validarCabeceraPdf(Part archivo) throws IOException {
        try (InputStream input = archivo.getInputStream()) {
            byte[] cabecera = input.readNBytes(PDF_MAGIC.length);
            if (cabecera.length < PDF_MAGIC.length) {
                throw new IOException("El archivo no es un PDF válido.");
            }
            for (int i = 0; i < PDF_MAGIC.length; i++) {
                if (cabecera[i] != PDF_MAGIC[i]) {
                    throw new IOException("El archivo no es un PDF válido.");
                }
            }
        }
    }

    private static String sanitizarNombre(String nombre) {
        String limpio = nombre.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (limpio.isBlank()) {
            return "material.pdf";
        }
        if (!limpio.toLowerCase().endsWith(".pdf")) {
            return limpio + ".pdf";
        }
        return limpio;
    }
}
