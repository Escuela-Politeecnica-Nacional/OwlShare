package ec.edu.epn.util;

import ec.edu.epn.modelo.Carrera;
import ec.edu.epn.modelo.Materia;
import ec.edu.epn.modelo.Semestre;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Reglas de negocio para tutores (estudiantes activos, 2.º–9.º semestre):
 * solo pueden ofrecer materias de su carrera cuyo semestre curricular sea
 * estrictamente anterior al semestre que cursan.
 */
public final class MateriaTutorReglas {

    public static final int SEMESTRE_MINIMO_TUTOR = 2;
    public static final int SEMESTRE_MAXIMO_TUTOR = 9;

    private MateriaTutorReglas() {
    }

    public static boolean esSemestreValidoParaTutor(Semestre semestreTutor) {
        if (semestreTutor == null) {
            return false;
        }
        int numero = semestreTutor.getNumero();
        return numero >= SEMESTRE_MINIMO_TUTOR && numero <= SEMESTRE_MAXIMO_TUTOR;
    }

    public static List<Semestre> semestresPermitidosParaTutor() {
        List<Semestre> semestres = new ArrayList<>();
        for (Semestre semestre : Semestre.values()) {
            if (esSemestreValidoParaTutor(semestre)) {
                semestres.add(semestre);
            }
        }
        return semestres;
    }

    public static Optional<String> validarSemestreTutor(Semestre semestreTutor) {
        if (semestreTutor == null) {
            return Optional.of("El semestre es obligatorio para tutores.");
        }
        if (semestreTutor.getNumero() < SEMESTRE_MINIMO_TUTOR) {
            return Optional.of("En primer semestre no puedes registrarte como tutor.");
        }
        if (semestreTutor.getNumero() > SEMESTRE_MAXIMO_TUTOR) {
            return Optional.of("Solo estudiantes hasta 9.º semestre pueden registrarse como tutores.");
        }
        return Optional.empty();
    }

    public static boolean puedeOfrecerMaterias(Semestre semestreTutor) {
        return esSemestreValidoParaTutor(semestreTutor);
    }

    public static List<Materia> materiasPermitidas(Carrera carrera, Semestre semestreTutor) {
        if (carrera == null || !puedeOfrecerMaterias(semestreTutor)) {
            return List.of();
        }

        int semestreActual = semestreTutor.getNumero();
        List<Materia> permitidas = new ArrayList<>();
        for (Materia materia : CatalogoRegistro.materiasDeCarrera(carrera)) {
            if (materia.getSemestre() < semestreActual) {
                permitidas.add(materia);
            }
        }

        permitidas.sort(Comparator
                .comparingInt(Materia::getSemestre)
                .thenComparing(Materia::getNombre, String.CASE_INSENSITIVE_ORDER));
        return permitidas;
    }

    public static boolean esMateriaPermitida(Carrera carrera, Semestre semestreTutor, String codigoMateria) {
        if (carrera == null || codigoMateria == null || codigoMateria.isBlank()) {
            return false;
        }
        if (!puedeOfrecerMaterias(semestreTutor)) {
            return false;
        }

        Materia materia = CatalogoRegistro.buscarMateriaEnCarrera(carrera, codigoMateria);
        return materia != null && materia.getSemestre() < semestreTutor.getNumero();
    }

    public static Optional<String> validarCodigosSeleccionados(Carrera carrera, Semestre semestreTutor,
                                                               Iterable<String> codigosMateria) {
        Optional<String> errorSemestre = validarSemestreTutor(semestreTutor);
        if (errorSemestre.isPresent()) {
            return errorSemestre;
        }
        if (carrera == null) {
            return Optional.of("La carrera seleccionada no es válida.");
        }

        Set<String> codigos = new LinkedHashSet<>();
        for (String codigo : codigosMateria) {
            if (codigo != null && !codigo.isBlank()) {
                codigos.add(codigo.trim());
            }
        }

        if (codigos.isEmpty()) {
            return Optional.of("Debes seleccionar al menos una materia.");
        }

        for (String codigo : codigos) {
            Materia materia = CatalogoRegistro.buscarMateriaEnCarrera(carrera, codigo);
            if (materia == null) {
                return Optional.of("La materia " + codigo + " no pertenece a la carrera seleccionada.");
            }
            if (materia.getSemestre() >= semestreTutor.getNumero()) {
                return Optional.of("La materia " + codigo + " (" + materia.getNombre() + ") es del "
                        + materia.getSemestre() + ".º semestre y no puede ser ofrecida por un tutor de "
                        + semestreTutor.getNombre() + ". Solo puedes tutorizar materias de semestres anteriores.");
            }
        }

        return Optional.empty();
    }
}
