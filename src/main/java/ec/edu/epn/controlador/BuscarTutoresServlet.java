package ec.edu.epn.controlador;

import ec.edu.epn.dao.UsuarioDAO;
import ec.edu.epn.modelo.Materia;
import ec.edu.epn.modelo.TutorResumen;
import ec.edu.epn.util.CatalogoRegistro;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class BuscarTutoresServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String materia = trim(request.getParameter("materia"));
        if (materia.isEmpty()) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "El parámetro 'materia' es obligatorio.");
            return;
        }

        List<Materia> materiasCatalogo = CatalogoRegistro.buscarMateriasPorNombreOCodigo(materia);
        List<TutorResumen> tutores;
        try {
            tutores = usuarioDAO.buscarTutoresPorMateria(materia);
        } catch (RuntimeException e) {
            responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No se pudo conectar a la base de datos.");
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try (PrintWriter writer = response.getWriter()) {
            writer.write(construirJson(materia, materiasCatalogo, tutores));
        }
    }

    private String construirJson(String consulta, List<Materia> materiasCatalogo, List<TutorResumen> tutores) {
        StringBuilder json = new StringBuilder();
        json.append('{');
        json.append("\"consulta\":\"").append(escapeJson(consulta)).append("\",");
        json.append("\"materiasEncontradas\":").append(materiasJson(materiasCatalogo)).append(',');
        json.append("\"total\":").append(tutores.size()).append(',');
        json.append("\"tutores\":").append(tutoresJson(tutores));
        json.append('}');
        return json.toString();
    }

    private String materiasJson(List<Materia> materias) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < materias.size(); i++) {
            Materia materia = materias.get(i);
            json.append("{\"codigo\":\"").append(escapeJson(materia.getCodigo()))
                    .append("\",\"nombre\":\"").append(escapeJson(materia.getNombre()))
                    .append("\",\"semestre\":").append(materia.getSemestre()).append('}');
            if (i < materias.size() - 1) {
                json.append(',');
            }
        }
        json.append(']');
        return json.toString();
    }

    private String tutoresJson(List<TutorResumen> tutores) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < tutores.size(); i++) {
            TutorResumen tutor = tutores.get(i);
            json.append('{');
            json.append("\"id\":").append(tutor.getId()).append(',');
            json.append("\"nombreCompleto\":\"").append(escapeJson(tutor.getNombreCompleto())).append("\",");
            json.append("\"email\":\"").append(escapeJson(tutor.getEmail())).append("\",");
            json.append("\"carrera\":").append(tutor.getCarrera() == null ? "null"
                    : "\"" + escapeJson(tutor.getCarrera()) + "\"").append(',');
            json.append("\"semestre\":").append(tutor.getSemestre() == null ? "null"
                    : "\"" + escapeJson(tutor.getSemestre()) + "\"").append(',');
            json.append("\"materias\":").append(listaJson(tutor.getMaterias()));
            json.append('}');
            if (i < tutores.size() - 1) {
                json.append(',');
            }
        }
        json.append(']');
        return json.toString();
    }

    private String listaJson(List<String> valores) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < valores.size(); i++) {
            json.append('"').append(escapeJson(valores.get(i))).append('"');
            if (i < valores.size() - 1) {
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
            writer.write("{\"error\":\"" + escapeJson(mensaje) + "\"}");
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
