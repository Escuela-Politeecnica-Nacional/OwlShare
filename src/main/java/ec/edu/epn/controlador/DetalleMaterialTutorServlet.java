package ec.edu.epn.controlador;

import ec.edu.epn.dao.MaterialDAO;
import ec.edu.epn.modelo.Material;
import ec.edu.epn.modelo.MaterialVista;
import ec.edu.epn.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class DetalleMaterialTutorServlet extends HttpServlet {

    private final MaterialDAO materialDAO = new MaterialDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario tutor = TutorAuth.requerirTutor(request, response);
        if (tutor == null) {
            return;
        }

        Long idMaterial = parseId(request.getParameter("id"));
        if (idMaterial == null) {
            response.sendRedirect(request.getContextPath() + "/tutor/materiales");
            return;
        }

        Material material = materialDAO.buscarPorIdYTutor(idMaterial, tutor.getId()).orElse(null);
        if (material == null) {
            response.sendRedirect(request.getContextPath() + "/tutor/materiales");
            return;
        }

        request.setAttribute("material", MaterialVista.paraTutor(material));
        request.getRequestDispatcher("/WEB-INF/tutor/detalle-material.jsp").forward(request, response);
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
}
