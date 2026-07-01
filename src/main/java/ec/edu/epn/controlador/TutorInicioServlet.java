package ec.edu.epn.controlador;

import ec.edu.epn.dao.MaterialDAO;
import ec.edu.epn.modelo.MaterialResumenTutor;
import ec.edu.epn.modelo.Rol;
import ec.edu.epn.modelo.TutorPerfilVista;
import ec.edu.epn.modelo.Usuario;
import ec.edu.epn.util.SesionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class TutorInicioServlet extends HttpServlet {

    private final MaterialDAO materialDAO = new MaterialDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario usuario = SesionUtil.obtenerUsuario(request);

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (usuario.getRol() != Rol.TUTOR) {
            response.sendRedirect(request.getContextPath() + destinoPorRol(usuario.getRol()));
            return;
        }

        request.setAttribute("tutorPerfil", TutorPerfilVista.desde(usuario));
        MaterialResumenTutor resumen = materialDAO.resumenPorTutor(usuario.getId());
        request.setAttribute("materialesAprobados", resumen.getAprobados());
        request.setAttribute("materialesPendientes", resumen.getPendientes());
        request.setAttribute("materialesRechazados", resumen.getRechazados());
        request.setAttribute("sesiones", List.of());

        request.getRequestDispatcher("/WEB-INF/tutor/dashboard-tutor.jsp").forward(request, response);
    }

    private String destinoPorRol(Rol rol) {
        return switch (rol) {
            case ESTUDIANTE -> "/estudiante/inicio";
            case TUTOR -> "/tutor/inicio";
            case ADMIN -> "/admin/inicio";
        };
    }
}
