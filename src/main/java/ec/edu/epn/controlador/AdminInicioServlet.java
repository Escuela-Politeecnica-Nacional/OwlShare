package ec.edu.epn.controlador;

import ec.edu.epn.dao.MaterialDAO;
import ec.edu.epn.modelo.EstadoMaterial;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AdminInicioServlet extends HttpServlet {

    private final MaterialDAO materialDAO = new MaterialDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (AdminAuth.requerirAdmin(request, response) == null) {
            return;
        }

        request.setAttribute("materialesPendientes",
                materialDAO.contarPorEstado(EstadoMaterial.PENDIENTE));

        request.getRequestDispatcher("/WEB-INF/admin/inicio.jsp").forward(request, response);
    }
}
