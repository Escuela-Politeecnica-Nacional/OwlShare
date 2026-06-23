package util;

import ec.edu.epn.modelo.Carrera;
import ec.edu.epn.modelo.Rol;
import ec.edu.epn.modelo.Semestre;
import ec.edu.epn.modelo.TrayectoriaSemestre;
import ec.edu.epn.modelo.Usuario;
import ec.edu.epn.util.HorarioUtil;
import ec.edu.epn.util.TutorPerfilBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TutorPerfilYHorarioTest {

    @Test
    void horarioValidoCuandoInicioEsAntesQueFin() {
        assertTrue(HorarioUtil.esRangoValido("09:00", "10:00"));
    }

    @Test
    void horarioInvalidoCuandoInicioEsIgualOFinal() {
        assertFalse(HorarioUtil.esRangoValido("10:00", "10:00"));
        assertFalse(HorarioUtil.esRangoValido("11:00", "09:00"));
    }

    @Test
    void detectaSolapamientoDeHorarios() {
        assertTrue(HorarioUtil.haySolapamiento("09:00", "10:00", "09:30", "10:30"));
        assertFalse(HorarioUtil.haySolapamiento("09:00", "10:00", "10:00", "11:00"));
    }

    @Test
    void trayectoriaMarcaSemestresCursadosYCursando() {
        Usuario tutor = new Usuario();
        tutor.setId(1L);
        tutor.setEmail("tutor@epn.edu.ec");
        tutor.setRol(Rol.TUTOR);
        tutor.setNombre("Ana");
        tutor.setApellido("López");
        tutor.setCarrera(Carrera.SOFTWARE);
        tutor.setSemestre(Semestre.QUINTO);
        tutor.setMaterias("ICCD144,MATD113");

        var perfil = TutorPerfilBuilder.construir(tutor);

        assertEquals(5, perfil.getTrayectoria().size());
        assertEquals("cursando", perfil.getTrayectoria().get(4).getEstado());
        assertEquals("cursado", perfil.getTrayectoria().get(0).getEstado());
        assertEquals(2, perfil.getMaterias().size());
        assertEquals("ICCD144", perfil.getMaterias().get(0).getCodigo());
    }

    @Test
    void trayectoriaVaciaSiNoTieneSemestre() {
        assertTrue(TutorPerfilBuilder.construirTrayectoria(0).isEmpty());
    }
}
