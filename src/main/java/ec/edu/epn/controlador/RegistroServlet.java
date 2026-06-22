package ec.edu.epn.controlador;

import ec.edu.epn.dao.UsuarioDAO;
import ec.edu.epn.modelo.Carrera;
import ec.edu.epn.modelo.Rol;
import ec.edu.epn.modelo.Semestre;
import ec.edu.epn.modelo.Usuario;
import ec.edu.epn.util.CatalogoRegistro;
import ec.edu.epn.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@WebServlet(name = "RegistroServlet", urlPatterns = {"/registro"})
public class RegistroServlet extends HttpServlet {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        cargarCatalogo(request);
        request.getRequestDispatcher("/registro.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String email = trim(request.getParameter("email"));
        String password = request.getParameter("password");
        String nombre = trim(request.getParameter("nombre"));
        String segundoNombre = trim(request.getParameter("segundoNombre"));
        String apellido = trim(request.getParameter("apellido"));
        String segundoApellido = trim(request.getParameter("segundoApellido"));
        String rolParam = trim(request.getParameter("rol"));
        String semestreParam = trim(request.getParameter("semestre"));
        String carreraParam = trim(request.getParameter("carrera"));
        String[] materias = request.getParameterValues("materias");

        String error = validarCampos(email, password, nombre, apellido, rolParam, semestreParam, carreraParam, materias);
        if (error != null) {
            mostrarError(request, response, error);
            return;
        }

        try {
            if (usuarioDAO.existePorEmail(email)) {
                mostrarError(request, response, "El correo electrónico ya está registrado.");
                return;
            }
        } catch (RuntimeException e) {
            if (esErrorBaseDatos(e)) {
                mostrarError(request, response, mensajeErrorBaseDatos());
                return;
            }
            throw e;
        }

        Rol rol = Rol.valueOf(rolParam);
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword(PasswordUtil.hash(password));
        usuario.setNombre(nombre);
        usuario.setSegundoNombre(blankToNull(segundoNombre));
        usuario.setApellido(apellido);
        usuario.setSegundoApellido(blankToNull(segundoApellido));
        usuario.setRol(rol);

        if (rol == Rol.TUTOR) {
            usuario.setSemestre(Semestre.valueOf(semestreParam));
            usuario.setCarrera(Carrera.valueOf(carreraParam));
            usuario.setMaterias(Arrays.stream(materias)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.joining(",")));
        } else if (!semestreParam.isEmpty()) {
            usuario.setSemestre(Semestre.valueOf(semestreParam));
        }
        if (rol == Rol.ESTUDIANTE && !carreraParam.isEmpty()) {
            usuario.setCarrera(Carrera.valueOf(carreraParam));
        }

        try {
            usuarioDAO.guardar(usuario);
        } catch (RuntimeException e) {
            if (esViolacionEmailUnico(e)) {
                mostrarError(request, response, "El correo electrónico ya está registrado.");
                return;
            }
            if (esErrorBaseDatos(e)) {
                mostrarError(request, response, mensajeErrorBaseDatos());
                return;
            }
            throw e;
        }

        response.sendRedirect(request.getContextPath() + "/login?mensaje="
                + java.net.URLEncoder.encode("Cuenta creada exitosamente. Inicia sesión.", "UTF-8"));
    }

    private String validarCampos(String email, String password, String nombre, String apellido, String rolParam, String semestreParam, String carreraParam, String[] materias) {
        if (email.isEmpty()) {
            return "El correo electrónico es obligatorio.";
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "El formato del correo electrónico no es válido.";
        }
        if (password == null || password.isBlank()) {
            return "La contraseña es obligatoria.";
        }
        if (nombre.isEmpty()) {
            return "El primer nombre es obligatorio.";
        }
        if (apellido.isEmpty()) {
            return "El primer apellido es obligatorio.";
        }
        if (rolParam.isEmpty()) {
            return "Debes seleccionar un rol.";
        }

        Rol rol;
        try {
            rol = Rol.valueOf(rolParam);
        } catch (IllegalArgumentException e) {
            return "El rol seleccionado no es válido.";
        }

        if (rol == Rol.TUTOR) {
            if (semestreParam.isEmpty()) {
                return "El semestre es obligatorio para tutores.";
            }
            if (carreraParam.isEmpty()) {
                return "La carrera es obligatoria para tutores.";
            }
            if (materias == null || materias.length == 0) {
                return "Debes seleccionar al menos una materia.";
            }
            try {
                Semestre semestre = Semestre.valueOf(semestreParam);
                if (semestre.getNumero() <= 1) {
                    return "En primer semestre no puedes registrarte como tutor con materias.";
                }
            } catch (IllegalArgumentException e) {
                return "El semestre seleccionado no es válido.";
            }
            try {
                Carrera.valueOf(carreraParam);
            } catch (IllegalArgumentException e) {
                return "La carrera seleccionada no es válida.";
            }
        }

        return null;
    }

    private void mostrarError(HttpServletRequest request, HttpServletResponse response, String error)
            throws ServletException, IOException {
        request.setAttribute("error", error);
        cargarCatalogo(request);
        request.getRequestDispatcher("/registro.jsp").forward(request, response);
    }

    private void cargarCatalogo(HttpServletRequest request) {
        request.setAttribute("semestres", CatalogoRegistro.semestres());
        request.setAttribute("carreras", CatalogoRegistro.carreras());
        request.setAttribute("materiasPorCarreraJson", CatalogoRegistro.materiasPorCarreraJson());
    }

    private boolean esErrorBaseDatos(RuntimeException e) {
        Throwable cause = e;
        while (cause != null) {
            String message = cause.getMessage();
            if (message != null) {
                String lower = message.toLowerCase();
                if (lower.contains("connection refused")
                        || lower.contains("jdbcconnectionexception")
                        || lower.contains("jdbcenvironment")
                        || lower.contains("unable to create requested service")
                        || lower.contains("password authentication failed")
                        || lower.contains("sqlite")) {
                    return true;
                }
            }
            cause = cause.getCause();
        }
        return false;
    }

    private String mensajeErrorBaseDatos() {
        return "No se pudo conectar a la base de datos. "
                + "Verifica que no tengas DB_URL apuntando a PostgreSQL sin servidor activo. "
                + "Por defecto la app usa SQLite (archivo owlshare.db en la raíz del proyecto).";
    }

    private boolean esViolacionEmailUnico(RuntimeException e) {
        Throwable cause = e;
        while (cause != null) {
            String message = cause.getMessage();
            if (message != null && message.toLowerCase().contains("usuarios_email")) {
                return true;
            }
            if (message != null && message.toLowerCase().contains("duplicate") && message.toLowerCase().contains("email")) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
