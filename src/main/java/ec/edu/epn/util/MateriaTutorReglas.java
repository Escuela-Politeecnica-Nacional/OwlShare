package ec.edu.epn.util;

import ec.edu.epn.catalogo.MateriasCatalogo;
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
 * Reglas de tutoría:
 * <ul>
 *   <li>Tutor: estudiante del 2.º al 9.º semestre.</li>
 *   <li>Materias: de su carrera, semestre curricular estrictamente anterior al que cursa
 *       (un tutor de 9.º solo puede ofrecer materias del 1.º al 8.º).</li>
 *   <li>Excluidas: prácticas, titulación e integración curricular.</li>
 * </ul>
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
        for (Materia materia : MateriasCatalogo.materiasDeCarrera(carrera)) {
            if (cumpleReglasTutoria(materia, semestreActual)) {
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

        Materia materia = MateriasCatalogo.buscarEnCarrera(carrera, codigoMateria);
        return materia != null && cumpleReglasTutoria(materia, semestreTutor.getNumero());
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
            Materia materia = MateriasCatalogo.buscarEnCarrera(carrera, codigo);
            if (materia == null) {
                return Optional.of("La materia " + codigo + " no pertenece a la carrera seleccionada.");
            }
            if (!MateriasCatalogo.esMateriaTutorable(materia)) {
                return Optional.of("La materia " + codigo + " (" + materia.getNombre()
                        + ") no está habilitada para tutorías (prácticas o titulación).");
            }
            if (materia.getSemestre() >= semestreTutor.getNumero()) {
                return Optional.of("La materia " + codigo + " (" + materia.getNombre() + ") es del "
                        + materia.getSemestre() + ".º semestre y no puede ser ofrecida por un tutor de "
                        + semestreTutor.getNombre() + ". Solo puedes tutorizar materias de semestres anteriores.");
            }
        }

        return Optional.empty();
    }

    public static String materiasPermitidasJson(Carrera carrera, Semestre semestreTutor) {
        List<Materia> permitidas = materiasPermitidas(carrera, semestreTutor);
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < permitidas.size(); i++) {
            Materia materia = permitidas.get(i);
            if (i > 0) {
                json.append(',');
            }
            json.append("{\"codigo\":\"").append(escapeJson(materia.getCodigo()))
                    .append("\",\"nombre\":\"").append(escapeJson(materia.getNombre()))
                    .append("\",\"semestre\":").append(materia.getSemestre()).append('}');
        }
        json.append(']');
        return json.toString();
    }

    public static String codigosSeleccionadosJson(Iterable<String> codigos) {
        StringBuilder json = new StringBuilder("[");
        boolean primero = true;
        for (String codigo : codigos) {
            if (codigo == null || codigo.isBlank()) {
                continue;
            }
            if (!primero) {
                json.append(',');
            }
            json.append('"').append(escapeJson(codigo.trim())).append('"');
            primero = false;
        }
        json.append(']');
        return json.toString();
    }

    private static String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private static boolean cumpleReglasTutoria(Materia materia, int semestreTutor) {
        return MateriasCatalogo.esMateriaTutorable(materia)
                && materia.getSemestre() < semestreTutor;
    }
}
