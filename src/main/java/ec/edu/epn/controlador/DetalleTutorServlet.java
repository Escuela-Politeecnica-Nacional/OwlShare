package ec.edu.epn.controlador;

import ec.edu.epn.dao.UsuarioDAO;
import ec.edu.epn.modelo.MateriaDetalle;
import ec.edu.epn.modelo.TrayectoriaSemestre;
import ec.edu.epn.modelo.TutorPerfilDetalle;
import ec.edu.epn.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class DetalleTutorServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String idParam = trim(request.getParameter("id"));
        if (idParam.isEmpty()) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "El parámetro 'id' es obligatorio.");
            return;
        }

        long id;
        try {
            id = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "El parámetro 'id' debe ser numérico.");
            return;
        }

        Optional<TutorPerfilDetalle> perfil;
        try {
            perfil = usuarioDAO.buscarPerfilTutor(id);
        } catch (RuntimeException e) {
            responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No se pudo consultar la base de datos.");
            return;
        }

        if (perfil.isEmpty()) {
            responderError(response, HttpServletResponse.SC_NOT_FOUND,
                    "No se encontró un tutor con el id indicado.");
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try (PrintWriter writer = response.getWriter()) {
            writer.write(construirJson(perfil.get()));
        }
    }

    private String construirJson(TutorPerfilDetalle perfil) {
        StringBuilder json = new StringBuilder("{");
        json.append("\"id\":").append(perfil.getId()).append(',');
        json.append("\"nombreCompleto\":\"").append(JsonUtil.escape(perfil.getNombreCompleto())).append("\",");
        json.append("\"email\":\"").append(JsonUtil.escape(perfil.getEmail())).append("\",");
        json.append("\"carrera\":").append(JsonUtil.nullableString(perfil.getCarrera())).append(',');
        json.append("\"carreraCodigo\":").append(JsonUtil.nullableString(perfil.getCarreraCodigo())).append(',');
        json.append("\"semestreActual\":").append(JsonUtil.nullableString(perfil.getSemestreActual())).append(',');
        json.append("\"semestreNumero\":").append(perfil.getSemestreNumero()).append(',');
        json.append("\"trayectoria\":").append(trayectoriaJson(perfil)).append(',');
        json.append("\"materias\":").append(materiasJson(perfil));
        json.append('}');
        return json.toString();
    }

    private String trayectoriaJson(TutorPerfilDetalle perfil) {
        StringBuilder json = new StringBuilder("[");
        var trayectoria = perfil.getTrayectoria();
        for (int i = 0; i < trayectoria.size(); i++) {
            TrayectoriaSemestre item = trayectoria.get(i);
            json.append('{');
            json.append("\"numero\":").append(item.getNumero()).append(',');
            json.append("\"nombre\":\"").append(JsonUtil.escape(item.getNombre())).append("\",");
            json.append("\"estado\":\"").append(JsonUtil.escape(item.getEstado())).append('"');
            json.append('}');
            if (i < trayectoria.size() - 1) {
                json.append(',');
            }
        }
        json.append(']');
        return json.toString();
    }

    private String materiasJson(TutorPerfilDetalle perfil) {
        StringBuilder json = new StringBuilder("[");
        var materias = perfil.getMaterias();
        for (int i = 0; i < materias.size(); i++) {
            MateriaDetalle materia = materias.get(i);
            json.append('{');
            json.append("\"codigo\":\"").append(JsonUtil.escape(materia.getCodigo())).append("\",");
            json.append("\"nombre\":\"").append(JsonUtil.escape(materia.getNombre())).append("\",");
            json.append("\"semestre\":").append(materia.getSemestre());
            json.append('}');
            if (i < materias.size() - 1) {
                json.append(',');
            }
        }
        json.append(']');
        return json.toString();
    }

    private void responderError(HttpServletResponse response, int status, String mensaje) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try (PrintWriter writer = response.getWriter()) {
            writer.write("{\"error\":\"" + JsonUtil.escape(mensaje) + "\"}");
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
