package controlador;

import ec.edu.epn.controlador.BuscarTutoresServlet;
import ec.edu.epn.dao.UsuarioDAO;
import ec.edu.epn.modelo.TutorResumen;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BuscarTutoresServletTest {

    @Mock
    private UsuarioDAO usuarioDAO;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private BuscarTutoresServlet servlet;

    private StringWriter responseBody;

    @BeforeEach
    void setUp() throws Exception {
        responseBody = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseBody));

        // Inyecta el mock aunque el campo sea final
        java.lang.reflect.Field field = BuscarTutoresServlet.class.getDeclaredField("usuarioDAO");
        field.setAccessible(true);
        field.set(servlet, usuarioDAO);
    }

    // ── PRUEBA 1 ───────────────────────────────────────────────
    // Búsqueda con resultados
    // Debería: devolver JSON con el tutor encontrado y total > 0
    @Test
    void testBusquedaConResultadosDevuelveTutores() throws Exception {
        TutorResumen tutor = new TutorResumen(
                1L, "Ana García", "ana@epn.edu.ec",
                "Ingeniería", "Segundo",
                List.of("MAT101")
        );

        when(request.getParameter("materia")).thenReturn("MAT101");
        when(usuarioDAO.buscarTutoresPorMateria("MAT101")).thenReturn(List.of(tutor));

        when(request.getMethod()).thenReturn("GET");
        servlet.service(request, response);

        String json = responseBody.toString();
        assertTrue(json.contains("\"total\":1"),
                "Debe indicar que encontró 1 tutor");
        assertTrue(json.contains("Ana García"),
                "Debe incluir el nombre del tutor");
    }

    // ── PRUEBA 2 ───────────────────────────────────────────────
    // Búsqueda sin resultados
    // Debería: devolver JSON con lista vacía y total 0
    @Test
    void testBusquedaSinResultadosDevuelveListaVacia() throws Exception {
        when(request.getParameter("materia")).thenReturn("MATERIAQUENOEXISTE");
        when(usuarioDAO.buscarTutoresPorMateria("MATERIAQUENOEXISTE"))
                .thenReturn(List.of());

        when(request.getMethod()).thenReturn("GET");
        servlet.service(request, response);

        String json = responseBody.toString();
        assertTrue(json.contains("\"total\":0"),
                "Debe indicar que no encontró tutores");
        assertTrue(json.contains("\"tutores\":[]"),
                "La lista de tutores debe estar vacía");
    }

    // ── PRUEBA 3 ───────────────────────────────────────────────
    // Parámetro materia vacío
    // Debería: devolver error 400 sin llamar al DAO
    @Test
    void testMateriaVaciaDevuelveError400() throws Exception {
        when(request.getParameter("materia")).thenReturn("");

        when(request.getMethod()).thenReturn("GET");
        servlet.service(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(usuarioDAO, never()).buscarTutoresPorMateria(anyString());
    }

    // ── PRUEBA 4 ───────────────────────────────────────────────
    // Error de base de datos
    // Debería: devolver error 500 sin romper la aplicación
    @Test
    void testErrorDeBaseDatosDevuelveError500() throws Exception {
        when(request.getParameter("materia")).thenReturn("MAT101");
        when(usuarioDAO.buscarTutoresPorMateria("MAT101"))
                .thenThrow(new RuntimeException("Conexión fallida"));

        when(request.getMethod()).thenReturn("GET");
        servlet.service(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    // ── PRUEBA 5 ───────────────────────────────────────────────
    // Parámetro materia nulo (parámetro no enviado)
    // Debería: devolver error 400 sin llamar al DAO
    @Test
    void testMateriaNulaDevuelveError400() throws Exception {
        when(request.getParameter("materia")).thenReturn(null);

        when(request.getMethod()).thenReturn("GET");
        servlet.service(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(usuarioDAO, never()).buscarTutoresPorMateria(anyString());
    }
}