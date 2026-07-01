package ec.edu.epn.controlador;

import ec.edu.epn.dao.MaterialDAO;
import ec.edu.epn.modelo.EstadoMaterial;
import ec.edu.epn.modelo.MaterialResumenTutor;
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

        EstadoMaterial filtroEstado = parseEstadoFiltro(request.getParameter("estado"));
        MaterialResumenTutor resumen;
        List<MaterialVista> materiales;

        try {
            resumen = materialDAO.resumenPorTutor(tutor.getId());
            materiales = materialDAO.listarPorTutor(tutor.getId(), filtroEstado).stream()
                    .map(MaterialVista::paraTutor)
                    .toList();
        } catch (RuntimeException e) {
            resumen = MaterialResumenTutor.vacio();
            materiales = List.of();
            request.setAttribute("error", "No se pudieron cargar tus materiales.");
        }

        request.setAttribute("resumenMateriales", resumen);
        request.setAttribute("materiales", materiales);
        request.setAttribute("estadoFiltro", filtroEstado != null ? filtroEstado.name().toLowerCase() : "todos");
        request.setAttribute("totalMateriales", resumen.getTotal());
        request.setAttribute("materialesAprobados", resumen.getAprobados());
        request.setAttribute("materialesEnRevision", resumen.getPendientes());
        request.setAttribute("materialesRechazados", resumen.getRechazados());

        transferirMensaje(request.getSession(false), request, "flashMensaje");
        transferirMensaje(request.getSession(false), request, "error");

        request.getRequestDispatcher("/WEB-INF/tutor/gestiona-material.jsp")
                .forward(request, response);
    }

    private EstadoMaterial parseEstadoFiltro(String valor) {
        if (valor == null || valor.isBlank() || "todos".equalsIgnoreCase(valor.trim())) {
            return null;
        }
        try {
            return EstadoMaterial.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
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
