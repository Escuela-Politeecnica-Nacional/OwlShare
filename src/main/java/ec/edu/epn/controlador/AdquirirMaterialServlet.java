package ec.edu.epn.controlador;

import ec.edu.epn.dao.MaterialAdquisicionDAO;
import ec.edu.epn.dao.MaterialDAO;
import ec.edu.epn.modelo.Carrera;
import ec.edu.epn.modelo.Material;
import ec.edu.epn.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

public class AdquirirMaterialServlet extends HttpServlet {

    private final MaterialDAO materialDAO = new MaterialDAO();
    private final MaterialAdquisicionDAO adquisicionDAO = new MaterialAdquisicionDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario estudiante = EstudianteAuth.requerirEstudiante(request, response);
        if (estudiante == null) {
            return;
        }

        Carrera carrera = estudiante.getCarrera();
        if (carrera == null) {
            redirigirConError(request, response,
                    "Tu perfil no tiene una carrera registrada. No puedes adquirir materiales.");
            return;
        }

        Long idMaterial = parseId(request.getParameter("idMaterial"));
        if (idMaterial == null) {
            redirigirConError(request, response, "Material no válido.");
            return;
        }

        Optional<Material> materialOpt = materialDAO.buscarAprobadoPorIdYCarrera(idMaterial, carrera);
        if (materialOpt.isEmpty()) {
            redirigirConError(request, response, "El material no está disponible para tu carrera.");
            return;
        }

        Material material = materialOpt.get();
        if (adquisicionDAO.yaAdquirido(material.getId(), estudiante.getId())) {
            redirigirConExito(request, response, "Ya adquiriste este material.");
            return;
        }

        try {
            adquisicionDAO.registrar(material.getId(), estudiante.getId());
        } catch (RuntimeException e) {
            if (adquisicionDAO.yaAdquirido(material.getId(), estudiante.getId())) {
                redirigirConExito(request, response, "Ya adquiriste este material.");
                return;
            }
            redirigirConError(request, response, "No se pudo completar la adquisición. Intenta de nuevo.");
            return;
        }

        String mensaje = material.getCosto().signum() == 0
                ? "Material gratuito adquirido. Ya puedes descargarlo."
                : "Material adquirido correctamente. Ya puedes descargarlo.";
        redirigirConExito(request, response, mensaje);
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

    private void redirigirConExito(HttpServletRequest request, HttpServletResponse response, String mensaje)
            throws IOException {
        guardarFlash(request, "exito", mensaje);
        response.sendRedirect(request.getContextPath() + "/estudiante/biblioteca");
    }

    private void redirigirConError(HttpServletRequest request, HttpServletResponse response, String mensaje)
            throws IOException {
        guardarFlash(request, "error", mensaje);
        response.sendRedirect(request.getContextPath() + "/estudiante/biblioteca");
    }

    private void guardarFlash(HttpServletRequest request, String clave, String mensaje) {
        HttpSession session = request.getSession(true);
        session.setAttribute(clave, mensaje);
    }
}
