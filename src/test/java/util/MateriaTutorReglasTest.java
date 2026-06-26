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

    @Test
    void primerSemestreNoPuedeOfrecerMaterias() {
        assertFalse(MateriaTutorReglas.puedeOfrecerMaterias(Semestre.PRIMERO));
    }

    @Test
    void segundoSemestreEnAdelantePuedeOfrecerMaterias() {
        assertTrue(MateriaTutorReglas.puedeOfrecerMaterias(Semestre.SEGUNDO));
        assertTrue(MateriaTutorReglas.puedeOfrecerMaterias(Semestre.QUINTO));
    }

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
