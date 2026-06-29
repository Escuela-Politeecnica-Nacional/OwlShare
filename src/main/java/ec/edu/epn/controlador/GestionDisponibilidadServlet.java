package ec.edu.epn.controlador;

import ec.edu.epn.dao.DisponibilidadTutorDAO;
import ec.edu.epn.modelo.DiaSemana;
import ec.edu.epn.modelo.Usuario;
import ec.edu.epn.util.HorarioUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * El tutor administra sus franjas semanales de disponibilidad.
 */
public class GestionDisponibilidadServlet extends HttpServlet {

    private final DisponibilidadTutorDAO disponibilidadDAO = new DisponibilidadTutorDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        Usuario tutor = TutorAuth.requerirTutor(request, response);
        if (tutor == null) {
            return;
        }

        String accion = trim(request.getParameter("accion"));
        if ("crear".equalsIgnoreCase(accion)) {
            procesarCrear(request, response, tutor);
            return;
        }
        if ("eliminar".equalsIgnoreCase(accion)) {
            procesarEliminar(request, response, tutor);
            return;
        }
        redirigirConError(request, response, "Acción no reconocida.");
    }

    private void procesarCrear(HttpServletRequest request, HttpServletResponse response, Usuario tutor)
            throws IOException {
        String diaParam = trim(request.getParameter("diaSemana"));
        String horaInicio = trim(request.getParameter("horaInicio"));
        String horaFin = trim(request.getParameter("horaFin"));

        if (diaParam.isEmpty() || horaInicio.isEmpty() || horaFin.isEmpty()) {
            redirigirConError(request, response, "Día y horario son obligatorios.");
            return;
        }
        if (!HorarioUtil.esRangoValido(horaInicio, horaFin)) {
            redirigirConError(request, response, "La hora de inicio debe ser anterior a la hora de fin.");
            return;
        }

        DiaSemana diaSemana;
        try {
            diaSemana = DiaSemana.parse(diaParam);
        } catch (IllegalArgumentException e) {
            redirigirConError(request, response, "El día de la semana no es válido.");
            return;
        }

        try {
            disponibilidadDAO.guardar(tutor, diaSemana, horaInicio, horaFin);
        } catch (IllegalArgumentException e) {
            redirigirConError(request, response, e.getMessage());
            return;
        } catch (RuntimeException e) {
            redirigirConError(request, response, "No se pudo guardar la franja de disponibilidad.");
            return;
        }

        redirigirConExito(request, response, "Franja de disponibilidad agregada.");
    }

    private void procesarEliminar(HttpServletRequest request, HttpServletResponse response, Usuario tutor)
            throws IOException {
        String idParam = trim(request.getParameter("idFranja"));
        if (idParam.isEmpty()) {
            redirigirConError(request, response, "Falta el identificador de la franja.");
            return;
        }

        long idFranja;
        try {
            idFranja = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            redirigirConError(request, response, "El identificador de la franja no es válido.");
            return;
        }

        try {
            if (!disponibilidadDAO.eliminarSiEsPropietario(idFranja, tutor.getId())) {
                redirigirConError(request, response, "No se encontró la franja o no tienes permiso para eliminarla.");
                return;
            }
        } catch (RuntimeException e) {
            redirigirConError(request, response, "No se pudo eliminar la franja.");
            return;
        }

        redirigirConExito(request, response, "Franja de disponibilidad eliminada.");
    }

    private void redirigirConExito(HttpServletRequest request, HttpServletResponse response, String mensaje)
            throws IOException {
        guardarMensajeFlash(request, "exito", mensaje);
        response.sendRedirect(request.getContextPath() + "/tutor/horarios");
    }

    private void redirigirConError(HttpServletRequest request, HttpServletResponse response, String mensaje)
            throws IOException {
        guardarMensajeFlash(request, "error", mensaje);
        response.sendRedirect(request.getContextPath() + "/tutor/horarios");
    }

    private void guardarMensajeFlash(HttpServletRequest request, String clave, String mensaje) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute(clave, mensaje);
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
