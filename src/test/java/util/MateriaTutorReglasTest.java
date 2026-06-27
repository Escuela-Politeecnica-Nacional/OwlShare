package util;

import ec.edu.epn.modelo.Carrera;
import ec.edu.epn.modelo.Materia;
import ec.edu.epn.modelo.Semestre;
import ec.edu.epn.util.MateriaTutorReglas;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MateriaTutorReglasTest {
    // ── REGLA: semestre mínimo para ser tutor ──────────────────
    // Un estudiante de primer semestre no puede registrarse como
    // tutor porque no tiene materias anteriores que pueda enseñar.

    @Test
    void primerSemestreNoPuedeOfrecerMaterias() {
        assertFalse(MateriaTutorReglas.puedeOfrecerMaterias(Semestre.PRIMERO));
    }

    @Test
    void segundoSemestreEnAdelantePuedeOfrecerMaterias() {
        assertTrue(MateriaTutorReglas.puedeOfrecerMaterias(Semestre.SEGUNDO));
        assertTrue(MateriaTutorReglas.puedeOfrecerMaterias(Semestre.QUINTO));
    }

    // ── REGLA: materias visibles según semestre del tutor ─────
    // Un tutor solo puede ofrecer materias de semestres anteriores
    // al suyo, nunca del semestre actual ni de semestres superiores.
    @Test
    void tutorQuintoSemestreSoloVeMateriasDePrimerACuarto() {
        List<Materia> permitidas = MateriaTutorReglas.materiasPermitidas(Carrera.SOFTWARE, Semestre.QUINTO);

        assertFalse(permitidas.isEmpty());
        assertTrue(permitidas.stream().allMatch(m -> m.getSemestre() < 5));
        assertTrue(permitidas.stream().anyMatch(m -> "ICCD144".equals(m.getCodigo())));
        assertFalse(permitidas.stream().anyMatch(m -> m.getSemestre() >= 5));
    }

    @Test
    void tutorSegundoSemestreSoloVeMateriasDePrimerSemestre() {
        List<Materia> permitidas = MateriaTutorReglas.materiasPermitidas(Carrera.SOFTWARE, Semestre.SEGUNDO);

        assertFalse(permitidas.isEmpty());
        assertTrue(permitidas.stream().allMatch(m -> m.getSemestre() == 1));
    }


    // ── REGLA: validación de materia individual ────────────────
    // Antes de guardar el registro, cada código de materia se
    // valida individualmente para asegurarse de que el tutor
    // puede impartirla.
    @Test
    void aceptaMateriaValidaParaSemestreDelTutor() {
        assertTrue(MateriaTutorReglas.esMateriaPermitida(
                Carrera.SOFTWARE, Semestre.QUINTO, "ICCD144"));
    }

    @Test
    void rechazaMateriaDelMismoSemestreQueElTutor() {
        assertFalse(MateriaTutorReglas.esMateriaPermitida(
                Carrera.SOFTWARE, Semestre.QUINTO, "ISWD523"));
    }

    @Test
    void rechazaMateriaDeOtraCarrera() {
        assertFalse(MateriaTutorReglas.esMateriaPermitida(
                Carrera.SOFTWARE, Semestre.QUINTO, "IDSD513"));
    }

    // ── REGLA: validación del conjunto de materias seleccionadas
    // El servlet llama a validarCodigosSeleccionados con todos los
    // códigos que el usuario marcó en el formulario. Si alguno
    // es inválido, se retorna un mensaje de error descriptivo.
    @Test
    void validacionAceptaSeleccionCorrecta() {
        Optional<String> error = MateriaTutorReglas.validarCodigosSeleccionados(
                Carrera.SOFTWARE,
                Semestre.QUINTO,
                List.of("ICCD144", "MATD113")
        );

        assertTrue(error.isEmpty());
    }

    @Test
    void validacionRechazaMateriaDeSemestreActual() {
        Optional<String> error = MateriaTutorReglas.validarCodigosSeleccionados(
                Carrera.SOFTWARE,
                Semestre.QUINTO,
                List.of("ISWD523")
        );

        assertTrue(error.isPresent());
        assertTrue(error.get().contains("ISWD523"));
    }

    @Test
    void validacionRechazaMateriaAjenaALaCarrera() {
        Optional<String> error = MateriaTutorReglas.validarCodigosSeleccionados(
                Carrera.SOFTWARE,
                Semestre.QUINTO,
                List.of("IDSD513")
        );

        assertTrue(error.isPresent());
        assertTrue(error.get().contains("no pertenece"));
    }

    // ── REGLA: semestre máximo para ser tutor ─────────────────
    // El décimo semestre tampoco puede ser tutor (está en tesis/
    // etapa final). Solo son válidos del 2.º al 9.º semestre.
    @Test
    void decimoSemestreNoPuedeSerTutor() {
        assertFalse(MateriaTutorReglas.esSemestreValidoParaTutor(Semestre.DECIMO));
        assertFalse(MateriaTutorReglas.puedeOfrecerMaterias(Semestre.DECIMO));
    }

    @Test
    void novenoSemestrePuedeSerTutor() {
        assertTrue(MateriaTutorReglas.esSemestreValidoParaTutor(Semestre.NOVENO));
    }

    @Test
    void semestresPermitidosParaTutorVanDelSegundoAlNoveno() {
        var semestres = MateriaTutorReglas.semestresPermitidosParaTutor();

        assertEquals(8, semestres.size());
        assertEquals(Semestre.SEGUNDO, semestres.get(0));
        assertEquals(Semestre.NOVENO, semestres.get(semestres.size() - 1));
        assertFalse(semestres.contains(Semestre.PRIMERO));
        assertFalse(semestres.contains(Semestre.DECIMO));
    }

    // ── REGLA: mensajes de error específicos por semestre ─────
    // El mensaje que ve el usuario en pantalla debe ser claro
    // sobre por qué fue rechazado su registro.
    @Test
    void validacionRechazaDecimoSemestreComoTutor() {
        Optional<String> error = MateriaTutorReglas.validarCodigosSeleccionados(
                Carrera.SOFTWARE,
                Semestre.DECIMO,
                List.of("ICCD144")
        );

        assertTrue(error.isPresent());
        assertTrue(error.get().contains("9.º semestre"));
    }

    @Test
    void validacionRechazaPrimerSemestreComoTutor() {
        Optional<String> error = MateriaTutorReglas.validarCodigosSeleccionados(
                Carrera.SOFTWARE,
                Semestre.PRIMERO,
                List.of("ICCD144")
        );

        assertTrue(error.isPresent());
        assertTrue(error.get().toLowerCase().contains("primer semestre"));
    }
}
