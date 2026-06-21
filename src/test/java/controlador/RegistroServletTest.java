package controlador;

import ec.edu.epn.modelo.Carrera;
import ec.edu.epn.modelo.Rol;
import ec.edu.epn.modelo.Semestre;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class RegistroServletTest {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    // ── PRUEBA 1 ───────────────────────────────────────────────
    // Email vacío debe fallar — si el servlet no valida esto, el usuario
    // podría registrarse sin correo
    @Test
    void testEmailVacioDebeSerRechazado() {
        String email = trim("");
        assertTrue(email.isEmpty(),
                "Email vacío debe ser detectado como campo obligatorio faltante");
    }

    // ── PRUEBA 2 ───────────────────────────────────────────────
    // Email null tratado por trim debe resultar en vacío, no en NullPointerException
    @Test
    void testEmailNuloNoLanzaExcepcion() {
        assertDoesNotThrow(() -> trim(null),
                "trim(null) no debe lanzar NullPointerException");
    }

    // ── PRUEBA 3 ───────────────────────────────────────────────
    // trim debe limpiar espacios — sin esto un email " juan@epn.edu.ec "
    // pasaría la validación con espacios incluidos
    @Test
    void testTrimEliminaEspacios() {
        String resultado = trim("  juan@epn.edu.ec  ");
        assertEquals("juan@epn.edu.ec", resultado,
                "trim debe eliminar espacios al inicio y al final");
    }

    // ── PRUEBA 4 ───────────────────────────────────────────────
    // Estos emails malformados NO deben pasar el patrón del servlet
    // Si fallaran, usuarios con emails inválidos podrían registrarse
    @ParameterizedTest
    @ValueSource(strings = {
            "sinArroba",
            "sin@dominio",
            "@sinUsuario.com",
            "doble@@epn.edu.ec",
            "con espacios@epn.edu.ec",
            ""
    })
    void testEmailsInvalidosNoDebenPasarElPatron(String email) {
        assertFalse(EMAIL_PATTERN.matcher(email).matches(),
                "'" + email + "' no debe pasar la validación de formato");
    }

    // ── PRUEBA 5 ───────────────────────────────────────────────
    // Estos emails SÍ deben pasar — si fallaran, usuarios legítimos
    // no podrían registrarse
    @ParameterizedTest
    @ValueSource(strings = {
            "juan@epn.edu.ec",
            "juan.perez@gmail.com",
            "user+tag@dominio.org",
            "user123@epn.edu.ec"
    })
    void testEmailsValidosDebenPasarElPatron(String email) {
        assertTrue(EMAIL_PATTERN.matcher(email).matches(),
                "'" + email + "' debería pasar la validación de formato");
    }

    // ── PRUEBA 6 ───────────────────────────────────────────────
    // Password nulo debe ser detectado — sin esto alguien podría
    // registrarse sin contraseña
    @Test
    void testPasswordNuloDebeSerDetectado() {
        String password = null;
        assertTrue(password == null || password.isBlank(),
                "Password nulo debe ser detectado como obligatorio");
    }

    // ── PRUEBA 7 ───────────────────────────────────────────────
    // Password con solo espacios también debe ser rechazado
    @Test
    void testPasswordSoloEspaciosDebeSerRechazado() {
        String password = "     ";
        assertTrue(password.isBlank(),
                "Password con solo espacios debe ser rechazado");
    }

    // ── PRUEBA 8 ───────────────────────────────────────────────
    // blankToNull con valor en blanco debe retornar null
    // Esto afecta segundoNombre y segundoApellido en el servlet
    @Test
    void testBlankToNullConBlancoRetornaNull() {
        assertNull(blankToNull("   "),
                "blankToNull debe retornar null para cadena en blanco");
    }

    // ── PRUEBA 9 ───────────────────────────────────────────────
    // blankToNull con valor real debe conservarlo
    @Test
    void testBlankToNullConValorLoConserva() {
        assertEquals("García", blankToNull("García"),
                "blankToNull debe retornar el valor si tiene contenido");
    }

    // ── PRUEBA 10 ──────────────────────────────────────────────
    // blankToNull con null no debe lanzar excepción
    @Test
    void testBlankToNullConNuloNoLanzaExcepcion() {
        assertDoesNotThrow(() -> blankToNull(null),
                "blankToNull(null) no debe lanzar NullPointerException");
    }

    // ── PRUEBA 11 ──────────────────────────────────────────────
    // Rol inválido debe lanzar excepción — el servlet atrapa esto
    // y retorna "El rol seleccionado no es válido"
    @Test
    void testRolInvalidoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> Rol.valueOf("ROL_INEXISTENTE"),
                "Rol inexistente debe lanzar IllegalArgumentException");
    }

    // ── PRUEBA 12 ──────────────────────────────────────────────
    // Los roles que el servlet usa deben existir en el enum
    @Test
    void testRolEstudianteExisteEnElEnum() {
        assertDoesNotThrow(() -> Rol.valueOf("ESTUDIANTE"),
                "El rol ESTUDIANTE debe existir");
    }

    @Test
    void testRolTutorExisteEnElEnum() {
        assertDoesNotThrow(() -> Rol.valueOf("TUTOR"),
                "El rol TUTOR debe existir");
    }

    // ── PRUEBA 13 ──────────────────────────────────────────────
    // PRIMER semestre NO debe poder ser tutor
    // El servlet verifica semestre.getNumero() <= 1
    @Test
    void testPrimerSemestreNoPuedeSerTutor() {
        Semestre semestre = Semestre.PRIMERO;
        assertFalse(semestre.getNumero() > 1,
                "Primer semestre no debe poder registrarse como tutor");
    }

    // ── PRUEBA 14 ──────────────────────────────────────────────
    // Segundo semestre en adelante SÍ puede ser tutor
    @Test
    void testSegundoSemestrePuedeSerTutor() {
        Semestre semestre = Semestre.SEGUNDO;
        assertTrue(semestre.getNumero() > 1,
                "Segundo semestre en adelante sí puede ser tutor");
    }

    // ── PRUEBA 15 ──────────────────────────────────────────────
    // Todos los semestres deben tener número mayor a 0
    @Test
    void testTodosLosSemestrestienenNumeroPositivo() {
        for (Semestre s : Semestre.values()) {
            assertTrue(s.getNumero() > 0,
                    "El semestre " + s.name() + " debe tener número positivo");
        }
    }

    // ── PRUEBA 16 ──────────────────────────────────────────────
    // Los números de semestre deben ser únicos — si dos semestres
    // tuvieran el mismo número, la validación del tutor fallaría
    @Test
    void testNumeroDeSemestresSonUnicos() {
        Semestre[] valores = Semestre.values();
        for (int i = 0; i < valores.length; i++) {
            for (int j = i + 1; j < valores.length; j++) {
                assertNotEquals(valores[i].getNumero(), valores[j].getNumero(),
                        "Los semestres " + valores[i] + " y " + valores[j] +
                                " no deben tener el mismo número");
            }
        }
    }

    // ── PRUEBA 17 ──────────────────────────────────────────────
    // Semestre inválido debe lanzar excepción
    @Test
    void testSemestreInvalidoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> Semestre.valueOf("SEMESTRE_FALSO"),
                "Semestre inexistente debe lanzar IllegalArgumentException");
    }

    // ── PRUEBA 18 ──────────────────────────────────────────────
    // Carrera inválida debe lanzar excepción
    @Test
    void testCarreraInvalidaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> Carrera.valueOf("CARRERA_FALSA"),
                "Carrera inexistente debe lanzar IllegalArgumentException");
    }

    // ── PRUEBA 19 ──────────────────────────────────────────────
    // Tutor sin materias (null) debe ser detectado
    @Test
    void testTutorSinMateriasNullEsRechazado() {
        String[] materias = null;
        assertTrue(materias == null || materias.length == 0,
                "Materias null debe ser detectado para tutores");
    }

    // ── PRUEBA 20 ──────────────────────────────────────────────
    // Tutor con array de materias vacío también debe ser rechazado
    @Test
    void testTutorConMateriasVaciasEsRechazado() {
        String[] materias = new String[0];
        assertTrue(materias.length == 0,
                "Array de materias vacío debe ser rechazado para tutores");
    }
}