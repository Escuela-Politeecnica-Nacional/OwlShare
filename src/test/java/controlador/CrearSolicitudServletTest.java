package controlador;

import ec.edu.epn.dao.DisponibilidadTutorDAO;
import ec.edu.epn.dao.HorarioDAO;
import ec.edu.epn.modelo.MateriaCatalogo;
import ec.edu.epn.dao.MateriaCatalogoDAO;
import ec.edu.epn.controlador.CrearSolicitudServlet;
import ec.edu.epn.dao.SolicitudTutoriaDAO;
import ec.edu.epn.dao.UsuarioDAO;
import ec.edu.epn.modelo.Horario;
import ec.edu.epn.modelo.Carrera;
import ec.edu.epn.modelo.Rol;
import ec.edu.epn.modelo.Semestre;
import ec.edu.epn.modelo.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CrearSolicitudServletTest {

    @Mock
    private HorarioDAO horarioDAO;

    @Mock
    private MateriaCatalogoDAO materiaCatalogoDAO;

    @Mock
    private UsuarioDAO usuarioDAO;

    @Mock
    private SolicitudTutoriaDAO solicitudDAO;

    @Mock
    private DisponibilidadTutorDAO disponibilidadDAO;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private CrearSolicitudServlet servlet;
    private StringWriter responseBody;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new CrearSolicitudServlet();

        // Inyectamos todos los DAOs por reflexión para no tocar el servlet
        Field fieldUsuario = CrearSolicitudServlet.class.getDeclaredField("usuarioDAO");
        fieldUsuario.setAccessible(true);
        fieldUsuario.set(servlet, usuarioDAO);

        Field fieldSolicitud = CrearSolicitudServlet.class.getDeclaredField("solicitudTutoriaDAO");
        fieldSolicitud.setAccessible(true);
        fieldSolicitud.set(servlet, solicitudDAO);

        Field fieldMateria = CrearSolicitudServlet.class.getDeclaredField("materiaCatalogoDAO");
        fieldMateria.setAccessible(true);
        fieldMateria.set(servlet, materiaCatalogoDAO);

        Field fieldHorario = CrearSolicitudServlet.class.getDeclaredField("horarioDAO");
        fieldHorario.setAccessible(true);
        fieldHorario.set(servlet, horarioDAO);

        Field fieldDisponibilidad = CrearSolicitudServlet.class.getDeclaredField("disponibilidadDAO");
        fieldDisponibilidad.setAccessible(true);
        fieldDisponibilidad.set(servlet, disponibilidadDAO);

        responseBody = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseBody));
        when(request.getMethod()).thenReturn("POST");

        // Parámetros válidos base que comparten todas las pruebas
        when(request.getParameter("estudianteId")).thenReturn("1");
        when(request.getParameter("tutorId")).thenReturn("2");
        when(request.getParameter("codigoMateria")).thenReturn("ICCD144");
        when(request.getParameter("fecha")).thenReturn("2026-07-10");
        when(request.getParameter("horaInicio")).thenReturn("09:00");
        when(request.getParameter("horaFin")).thenReturn("10:00");
        when(request.getParameter("comentario")).thenReturn("Necesito ayuda con derivadas parciales.");

        // Estudiante válido con rol ESTUDIANTE
        Usuario estudiante = new Usuario();
        estudiante.setId(1L);
        estudiante.setRol(Rol.ESTUDIANTE);
        when(usuarioDAO.buscarPorId(1L)).thenReturn(Optional.of(estudiante));

        // Tutor válido con rol TUTOR y la materia ICCD144
        Usuario tutor = new Usuario();
        tutor.setId(2L);
        tutor.setRol(Rol.TUTOR);
        tutor.setCarrera(Carrera.SOFTWARE);
        tutor.setSemestre(Semestre.QUINTO);
        tutor.setMaterias("ICCD144");
        when(usuarioDAO.buscarPorId(2L)).thenReturn(Optional.of(tutor));

        // Materia válida en el catálogo
        MateriaCatalogo materiaCatalogo = new MateriaCatalogo("ICCD144", Carrera.SOFTWARE, "Programación", 2);
        when(materiaCatalogoDAO.buscarPorCodigo("ICCD144"))
                .thenReturn(Optional.of(materiaCatalogo));
        when(materiaCatalogoDAO.obtenerOCrear("ICCD144", Carrera.SOFTWARE))
                .thenReturn(materiaCatalogo);

        when(disponibilidadDAO.cubreHorario(2L, "2026-07-10", "09:00", "10:00"))
                .thenReturn(true);

        when(solicitudDAO.crearSolicitudConHorario(
                1L, 2L, "ICCD144", "2026-07-10", "09:00", "10:00",
                "Necesito ayuda con derivadas parciales."))
                .thenReturn(new SolicitudTutoriaDAO.SolicitudCreada(99L, 10L));
    }

    // ── PRUEBA 1 ───────────────────────────────────────────────
    // Horario disponible (sin conflicto)
    // Debería: guardar la solicitud y devolver status 201 con estado PENDIENTE
    @Test
    void testHorarioDisponibleCreaLaSolicitud() throws Exception {
        when(solicitudDAO.existeConflictoHorarioTutor(2L, "2026-07-10", "09:00", "10:00"))
                .thenReturn(false);

        servlet.service(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(solicitudDAO).crearSolicitudConHorario(
                1L, 2L, "ICCD144", "2026-07-10", "09:00", "10:00",
                "Necesito ayuda con derivadas parciales.");
        assertTrue(responseBody.toString().contains("PENDIENTE"));
    }

    // ── PRUEBA 2 ───────────────────────────────────────────────
    // Horario ya tomado (hay conflicto con otra solicitud)
    // Debería: devolver error 409 sin guardar ninguna solicitud
    @Test
    void testHorarioYaTomadoDevuelveError409() throws Exception {
        when(solicitudDAO.existeConflictoHorarioTutor(2L, "2026-07-10", "09:00", "10:00"))
                .thenReturn(true);

        servlet.service(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
        verify(solicitudDAO, never()).crearSolicitudConHorario(
                anyLong(), anyLong(), anyString(), anyString(), anyString(), anyString(), anyString());
        verify(solicitudDAO, never()).crearSolicitud(anyLong(), anyLong(), anyString(), anyString());
        assertTrue(responseBody.toString().contains("\"error\""));
    }

    // ── PRUEBA 3 ───────────────────────────────────────────────
    // Hora fin anterior a hora inicio (validación de rango)
    // Debería: devolver error 400 sin llegar a consultar el DAO
    @Test
    void testHoraFinAnteriorAInicioDevuelveError400() throws Exception {
        when(request.getParameter("horaInicio")).thenReturn("10:00");
        when(request.getParameter("horaFin")).thenReturn("09:00");

        servlet.service(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(solicitudDAO, never()).existeConflictoHorarioTutor(
                anyLong(), anyString(), anyString(), anyString());
    }
}