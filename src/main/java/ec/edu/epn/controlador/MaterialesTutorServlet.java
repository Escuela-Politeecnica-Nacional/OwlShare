package ec.edu.epn.controlador;

import ec.edu.epn.dao.MaterialDAO;
import ec.edu.epn.modelo.EstadoMaterial;
import ec.edu.epn.modelo.MaterialVista;
import ec.edu.epn.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

public class MaterialesTutorServlet extends HttpServlet {

    private final MaterialDAO materialDAO = new MaterialDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario tutor = TutorAuth.requerirTutor(request, response);
        if (tutor == null) {
            return;
        }

        List<MaterialVista> materiales;
        try {
            materiales = materialDAO.listarPorTutor(tutor.getId()).stream()
                    .map(MaterialVista::paraTutor)
                    .toList();
        } catch (RuntimeException e) {
            materiales = List.of();
            request.setAttribute("error", "No se pudieron cargar tus materiales.");
        }

        request.setAttribute("materiales", materiales);
        request.setAttribute("totalMateriales", materialDAO.contarPorTutor(tutor.getId()));
        request.setAttribute("materialesAprobados",
                materialDAO.contarPorTutorYEstado(tutor.getId(), EstadoMaterial.APROBADO));
        request.setAttribute("materialesEnRevision",
                materialDAO.contarPorTutorYEstado(tutor.getId(), EstadoMaterial.PENDIENTE));

        transferirMensaje(request.getSession(false), request, "flashMensaje");

        request.getRequestDispatcher("/WEB-INF/tutor/gestiona-material.jsp")
                .forward(request, response);
    }

    private void transferirMensaje(HttpSession session, HttpServletRequest request, String clave) {
        if (session == null) {
            return;
        }
        Object valor = session.getAttribute(clave);
        if (valor != null) {
            request.setAttribute(clave, valor);
            session.removeAttribute(clave);
        }
    }
}
