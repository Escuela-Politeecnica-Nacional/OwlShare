package ec.edu.epn.controlador;

import ec.edu.epn.dao.UsuarioDAO;
import ec.edu.epn.modelo.Rol;
import ec.edu.epn.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login", "/"})
public class LoginServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = trim(request.getParameter("email"));
        String password = request.getParameter("password");

        if (email.isEmpty() || password == null || password.isBlank()) {
            request.setAttribute("error", "Debes ingresar correo electrónico y contraseña.");
            request.getRequestDispatcher("/WEB-INF/auth/login.jsp").forward(request, response);
            return;
        }

        Usuario usuario = usuarioDAO.autenticar(email, password);
        if (usuario == null) {
            request.setAttribute("error", "Credenciales inválidas.");
            request.getRequestDispatcher("/WEB-INF/auth/login.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute("usuarioLogueado", usuario);

        response.sendRedirect(request.getContextPath() + destinoPorRol(usuario.getRol()));
    }

    private String destinoPorRol(Rol rol) {
        return switch (rol) {
            case ESTUDIANTE -> "/estudiante/inicio";
            case TUTOR -> "/tutor/inicio";
            case ADMIN -> "/admin/inicio";
        };
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
