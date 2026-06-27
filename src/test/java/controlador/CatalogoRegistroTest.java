package controlador;

import ec.edu.epn.modelo.Carrera;
import ec.edu.epn.modelo.Materia;
import ec.edu.epn.modelo.Semestre;
import ec.edu.epn.util.CatalogoRegistro;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CatalogoRegistroTest {

    // ── BÚSQUEDA CON RESULTADOS ────────────────────────────────
    // Verifican que el catálogo devuelve datos reales cuando se
    // busca con criterios válidos. Si fallan, el formulario de
    // registro no podría poblar los desplegables de materias.

    @Test
    void buscarMateriaExistentePorCodigoExactoRetornaMateria() {
        Materia materia = CatalogoRegistro.buscarMateriaPorCodigo("ICCD144");
        assertNotNull(materia);
        assertEquals("Programación I", materia.getNombre());
    }

    @Test
    void buscarMateriaEnCarreraConCodigoValidoRetornaMateria() {
        Materia materia = CatalogoRegistro.buscarMateriaEnCarrera(
                Carrera.SOFTWARE, "ICCD144");
        assertNotNull(materia);
        assertEquals("Programación I", materia.getNombre());
    }

    @Test
    void buscarMateriasPorNombreCompletoRetornaResultados() {
        List<Materia> materias =
                CatalogoRegistro.buscarMateriasPorNombreOCodigo("Programación I");
        assertFalse(materias.isEmpty());
    }

    @Test
    void buscarMateriasPorCodigoParcialRetornaResultados() {
        // Un prefijo como "ICCD" debe coincidir con varias materias
        List<Materia> materias =
                CatalogoRegistro.buscarMateriasPorNombreOCodigo("ICCD");
        assertFalse(materias.isEmpty());
    }

    // ── BÚSQUEDA SIN RESULTADOS ────────────────────────────────
    // Verifican que el catálogo no explota ni devuelve basura
    // cuando no hay coincidencias. El servlet depende de esto
    // para mostrar "no se encontraron materias" correctamente.

    @Test
    void buscarMateriaConCodigoInexistenteRetornaNull() {
        assertNull(CatalogoRegistro.buscarMateriaPorCodigo("XXXX999"));
    }

    @Test
    void buscarMateriaEnCarreraConCodigoInexistenteRetornaNull() {
        assertNull(CatalogoRegistro.buscarMateriaEnCarrera(
                Carrera.SOFTWARE, "XXXX999"));
    }

    @Test
    void buscarMateriasPorTerminoInexistenteRetornaListaVacia() {
        List<Materia> materias =
                CatalogoRegistro.buscarMateriasPorNombreOCodigo("XYZ_NO_EXISTE");
        assertTrue(materias.isEmpty());
    }

    // ── OTROS CASOS (entradas nulas o vacías) ──────────────────
    // El servlet recibe parámetros HTTP que pueden llegar nulos.
    // Estas pruebas aseguran que el catálogo los maneja sin lanzar
    // NullPointerException.

    @Test
    void buscarMateriaPorCodigoNuloNoLanzaExcepcion() {
        assertNull(CatalogoRegistro.buscarMateriaPorCodigo(null));
    }

    @Test
    void buscarMateriaPorCodigoVacioNoLanzaExcepcion() {
        assertNull(CatalogoRegistro.buscarMateriaPorCodigo(""));
    }

    @Test
    void buscarMateriaEnCarreraNulaNoLanzaExcepcion() {
        assertNull(CatalogoRegistro.buscarMateriaEnCarrera(null, "ICCD144"));
    }

    // ── CONSISTENCIA DEL CATÁLOGO ──────────────────────────────
    // El catálogo es la fuente de verdad de los desplegables del
    // formulario. Si no coincide con los enums, el usuario vería
    // opciones que no existen o le faltarían carreras/semestres.

    @Test
    void todasLasCarrerasTienenAlMenosUnaMateria() {
        for (Carrera carrera : Carrera.values()) {
            List<Materia> materias = CatalogoRegistro.materiasDeCarrera(carrera);
            assertNotNull(materias);
            assertFalse(materias.isEmpty(),
                    "La carrera " + carrera + " no tiene materias registradas");
        }
    }

    @Test
    void cantidadDeSemestresCoincidenConElEnum() {
        assertEquals(Semestre.values().length, CatalogoRegistro.semestres().length);
    }

    @Test
    void cantidadDeCarrerasCoincidenConElEnum() {
        assertEquals(Carrera.values().length, CatalogoRegistro.carreras().length);
    }

    @Test
    void jsonDeMateriasPorCarreraEsValidoYContieneCarreraSoftware() {
        // Este JSON se usa en el JS del formulario para filtrar
        // materias dinámicamente al cambiar la carrera seleccionada
        String json = CatalogoRegistro.materiasPorCarreraJson();
        assertNotNull(json);
        assertFalse(json.isBlank());
        assertTrue(json.contains("SOFTWARE"));
    }
}