package controlador;

import ec.edu.epn.controlador.DetalleTutorServlet;
import ec.edu.epn.dao.UsuarioDAO;
import ec.edu.epn.modelo.MateriaDetalle;
import ec.edu.epn.modelo.TrayectoriaSemestre;
import ec.edu.epn.modelo.TutorPerfilDetalle;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DetalleTutorServletTest {

    @Mock
    private UsuarioDAO usuarioDAO;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private DetalleTutorServlet servlet;

    private StringWriter responseBody;

    @BeforeEach
    void setUp() throws Exception {
        responseBody = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseBody));
        when(request.getMethod()).thenReturn("GET");

        // Inyecta el mock aunque el campo sea final
        java.lang.reflect.Field field = DetalleTutorServlet.class.getDeclaredField("usuarioDAO");
        field.setAccessible(true);
        field.set(servlet, usuarioDAO);
    }

    // ── PRUEBA 1 ───────────────────────────────────────────────
    // Tutor encontrado
    // Debería: devolver JSON con el perfil completo
    @Test
    void testTutorEncontradoDevuelveJsonConPerfil() throws Exception {
        TutorPerfilDetalle perfil = new TutorPerfilDetalle(
                1L,
                "Carlos Pérez",
                "carlos@epn.edu.ec",
                "Ingeniería en Sistemas",
                "IS",
                "Quinto",
                5,
                List.of(new TrayectoriaSemestre(1, "Primer Semestre", "aprobado")),
                List.of(new MateriaDetalle("MAT101", "Cálculo", 2))
        );

        when(request.getParameter("id")).thenReturn("1");
        when(usuarioDAO.buscarPerfilTutor(1L)).thenReturn(Optional.of(perfil));

        servlet.service(request, response);

        String json = responseBody.toString();
        verify(response).setContentType("application/json");
        assertTrue(json.contains("\"id\":1"),
                "Debe incluir el id del tutor");
        assertTrue(json.contains("Carlos Pérez"),
                "Debe incluir el nombre del tutor");
        assertTrue(json.contains("MAT101"),
                "Debe incluir las materias del tutor");
    }

    // ── PRUEBA 2 ───────────────────────────────────────────────
    // Tutor no encontrado
    // Debería: devolver error 404
    @Test
    void testTutorNoEncontradoDevuelveError404() throws Exception {
        when(request.getParameter("id")).thenReturn("999");
        when(usuarioDAO.buscarPerfilTutor(999L)).thenReturn(Optional.empty());

        servlet.service(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        assertTrue(responseBody.toString().contains("\"error\""));
    }

    // ── PRUEBA 3 ───────────────────────────────────────────────
    // ID vacío
    // Debería: devolver error 400 sin llamar al DAO
    @Test
    void testIdVacioDevuelveError400() throws Exception {
        when(request.getParameter("id")).thenReturn("");

        servlet.service(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(usuarioDAO, never()).buscarPerfilTutor(anyLong());
    }

    // ── PRUEBA 4 ───────────────────────────────────────────────
    // ID no numérico
    // Debería: devolver error 400 sin llamar al DAO
    @Test
    void testIdNoNumericoDevuelveError400() throws Exception {
        when(request.getParameter("id")).thenReturn("abc");

        servlet.service(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(usuarioDAO, never()).buscarPerfilTutor(anyLong());
        assertTrue(responseBody.toString().contains("\"error\""));
    }

    // ── PRUEBA 5 ───────────────────────────────────────────────
    // Error de base de datos
    // Debería: devolver error 500 sin romper la aplicación
    @Test
    void testErrorDeBaseDatosDevuelveError500() throws Exception {
        when(request.getParameter("id")).thenReturn("1");
        when(usuarioDAO.buscarPerfilTutor(1L))
                .thenThrow(new RuntimeException("Conexión fallida"));

        servlet.service(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        assertTrue(responseBody.toString().contains("\"error\""));
    }
}