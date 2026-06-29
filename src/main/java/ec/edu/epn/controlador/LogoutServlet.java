package ec.edu.epn.controlador;

import ec.edu.epn.util.SesionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SesionUtil.cerrarSesion(request);
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
