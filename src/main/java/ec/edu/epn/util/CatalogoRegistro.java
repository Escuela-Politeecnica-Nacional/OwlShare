package ec.edu.epn.util;

import ec.edu.epn.catalogo.MateriasCatalogo;
import ec.edu.epn.modelo.Carrera;
import ec.edu.epn.modelo.Materia;
import ec.edu.epn.modelo.Semestre;

import java.util.List;

/**
 * Fachada de compatibilidad hacia {@link MateriasCatalogo}.
 */
public final class CatalogoRegistro {

    private CatalogoRegistro() {
    }

    public static List<Materia> materiasDeCarrera(Carrera carrera) {
        return MateriasCatalogo.materiasDeCarrera(carrera);
    }

    public static Materia buscarMateriaEnCarrera(Carrera carrera, String codigo) {
        return MateriasCatalogo.buscarEnCarrera(carrera, codigo);
    }

    public static Semestre[] semestres() {
        return MateriasCatalogo.semestres();
    }

    public static Carrera[] carreras() {
        return MateriasCatalogo.carreras();
    }

    public static List<Materia> buscarMateriasPorNombreOCodigo(String termino) {
        return MateriasCatalogo.buscarPorNombreOCodigo(termino);
    }

    public static List<String> codigosDeMaterias(List<Materia> materias) {
        return MateriasCatalogo.codigosDeMaterias(materias);
    }

    public static Materia buscarMateriaPorCodigo(String codigo) {
        return MateriasCatalogo.buscarPorCodigo(codigo);
    }

    public static List<Materia> materiasPorCodigos(Iterable<String> codigos) {
        return MateriasCatalogo.materiasPorCodigos(codigos);
    }

    public static String materiasPorCarreraJson() {
        return MateriasCatalogo.materiasPorCarreraJson();
    }

    public static List<Materia> todasLasMaterias() {
        return MateriasCatalogo.todasLasMaterias();
    }
}
