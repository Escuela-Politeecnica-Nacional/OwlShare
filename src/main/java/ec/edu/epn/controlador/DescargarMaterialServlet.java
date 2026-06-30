package ec.edu.epn.controlador;

import ec.edu.epn.dao.MaterialAdquisicionDAO;
import ec.edu.epn.dao.MaterialDAO;
import ec.edu.epn.modelo.EstadoMaterial;
import ec.edu.epn.modelo.Material;
import ec.edu.epn.modelo.Usuario;
import ec.edu.epn.util.MaterialAlmacenamiento;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class DescargarMaterialServlet extends HttpServlet {

    private final MaterialDAO materialDAO = new MaterialDAO();
    private final MaterialAdquisicionDAO adquisicionDAO = new MaterialAdquisicionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario estudiante = EstudianteAuth.requerirEstudiante(request, response);
        if (estudiante == null) {
            return;
        }

        Long idMaterial = parseId(request.getParameter("idMaterial"));
        if (idMaterial == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Material no válido.");
            return;
        }

        Optional<Material> materialOpt = materialDAO.buscarPorId(idMaterial);
        if (materialOpt.isEmpty() || materialOpt.get().getEstado() != EstadoMaterial.APROBADO) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Material no encontrado.");
            return;
        }

        Material material = materialOpt.get();
        if (!adquisicionDAO.yaAdquirido(material.getId(), estudiante.getId())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Debes adquirir el material antes de descargarlo.");
            return;
        }

        Path archivo = MaterialAlmacenamiento.resolverArchivo(material.getRutaAlmacenamiento());
        if (!Files.isRegularFile(archivo)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "El archivo no está disponible.");
            return;
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + sanitizarNombreDescarga(material.getNombreArchivo()) + "\"");
        response.setContentLengthLong(Files.size(archivo));

        try (OutputStream output = response.getOutputStream()) {
            Files.copy(archivo, output);
        }
    }

    private Long parseId(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(valor.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String sanitizarNombreDescarga(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "material.pdf";
        }
        return nombre.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
