package controlador;

import ec.edu.epn.controlador.LoginServlet;
import ec.edu.epn.dao.UsuarioDAO;
import ec.edu.epn.modelo.Rol;
import ec.edu.epn.modelo.Usuario;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LoginServletTest {

    @Mock
    private UsuarioDAO usuarioDAO;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    @InjectMocks
    private LoginServlet servlet;

    @BeforeEach
    void setUp() {
        when(request.getSession(false)).thenReturn(null);
        when(request.getSession(true)).thenReturn(session);
        when(request.getRequestDispatcher("/WEB-INF/auth/login.jsp")).thenReturn(dispatcher);
    }

    @Test
    void testLoginExitosoComoEstudiante() throws Exception {
        Usuario estudiante = new Usuario();
        estudiante.setEmail("estudiante@epn.edu.ec");
        estudiante.setRol(Rol.ESTUDIANTE);
        estudiante.setNombre("Ana");

        when(request.getParameter("email")).thenReturn("estudiante@epn.edu.ec");
        when(request.getParameter("password")).thenReturn("clave123");
        when(usuarioDAO.autenticar("estudiante@epn.edu.ec", "clave123"))
                .thenReturn(estudiante);

        when(request.getMethod()).thenReturn("POST");
        servlet.service(request, response);

        verify(session).setAttribute("usuarioLogueado", estudiante);
        verify(session).setAttribute("usuarioId", estudiante.getId());
        verify(response).sendRedirect(contains("/estudiante/inicio"));
    }

    @Test
    void testLoginExitosoComoTutor() throws Exception {
        Usuario tutor = new Usuario();
        tutor.setEmail("tutor@epn.edu.ec");
        tutor.setRol(Rol.TUTOR);
        tutor.setNombre("Carlos");

        when(request.getParameter("email")).thenReturn("tutor@epn.edu.ec");
        when(request.getParameter("password")).thenReturn("clave456");
        when(usuarioDAO.autenticar("tutor@epn.edu.ec", "clave456"))
                .thenReturn(tutor);

        when(request.getMethod()).thenReturn("POST");
        servlet.service(request, response);

        verify(session).setAttribute("usuarioLogueado", tutor);
        verify(session).setAttribute("usuarioId", tutor.getId());
        verify(response).sendRedirect(contains("/tutor/inicio"));
    }

    @Test
    void testLoginExitosoComoAdmin() throws Exception {
        Usuario admin = new Usuario();
        admin.setEmail("admin@epn.edu.ec");
        admin.setRol(Rol.ADMIN);
        admin.setNombre("Pedro");

        when(request.getParameter("email")).thenReturn("admin@epn.edu.ec");
        when(request.getParameter("password")).thenReturn("admin999");
        when(usuarioDAO.autenticar("admin@epn.edu.ec", "admin999"))
                .thenReturn(admin);

        when(request.getMethod()).thenReturn("POST");
        servlet.service(request, response);

        verify(session).setAttribute("usuarioLogueado", admin);
        verify(session).setAttribute("usuarioId", admin.getId());
        verify(response).sendRedirect(contains("/admin/inicio"));
    }

    @Test
    void testCredencialesInvalidasMuestranError() throws Exception {
        when(request.getParameter("email")).thenReturn("noexiste@epn.edu.ec");
        when(request.getParameter("password")).thenReturn("wrongpass");
        when(usuarioDAO.autenticar("noexiste@epn.edu.ec", "wrongpass"))
                .thenReturn(null);

        when(request.getMethod()).thenReturn("POST");
        servlet.service(request, response);

        verify(session, never()).setAttribute(eq("usuario"), any());
        verify(request).setAttribute(eq("error"), anyString());
        verify(dispatcher).forward(request, response);
    }
}