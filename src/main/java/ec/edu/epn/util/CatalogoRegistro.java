package ec.edu.epn.util;

import ec.edu.epn.modelo.Carrera;
import ec.edu.epn.modelo.Materia;
import ec.edu.epn.modelo.Semestre;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Catálogo académico basado en las mallas curriculares publicadas por la FIS-EPN:
 * <ul>
 *   <li>Software</li>
 *   <li>Computación</li>
 *   <li>Ciencia de Datos e Inteligencia Artificial</li>
 *   <li>Sistemas de Información</li>
 * </ul>
 */
public final class CatalogoRegistro {

    private static final Map<Carrera, List<Materia>> MATERIAS_POR_CARRERA = new EnumMap<>(Carrera.class);

    static {
        MATERIAS_POR_CARRERA.put(Carrera.SOFTWARE, List.of(
                mat("MATD113", "Álgebra Lineal", 1),
                mat("MATD123", "Cálculo en una Variable", 1),
                mat("FISD134", "Mecánica Newtoniana", 1),
                mat("ICCD144", "Programación I", 1),
                mat("CSHD111", "Comunicación Oral y Escrita", 1),
                mat("MATD213", "Ecuaciones Diferenciales Ordinarias", 2),
                mat("ICCD224", "Matemáticas Computacionales y Teoría de la Computación", 2),
                mat("ICCD233", "Fundamentos de Electrónica para Computación", 2),
                mat("ICCD244", "Programación II", 2),
                mat("CSHD211", "Análisis Socioeconómico y Político del Ecuador", 2),
                mat("MATD223", "Probabilidad y Estadísticas Básicas", 3),
                mat("ICCD323", "Sistemas Operativos", 3),
                mat("ICCD332", "Arquitectura de Computadores", 3),
                mat("ICCD343", "Estructura de Datos y Algoritmos I", 3),
                mat("ICCD353", "Fundamentos de Redes y Conectividad", 3),
                mat("CSHD300", "Asignatura de Artes y Humanidades", 3),
                mat("ISWD414", "Ingeniería de Software y Requerimientos", 4),
                mat("ICCD422", "Compiladores y Lenguajes", 4),
                mat("ISWD433", "Fundamentos de Sistemas de Información", 4),
                mat("ICCD442", "Estructura de Datos y Algoritmos II", 4),
                mat("ISWD453", "Fundamentos de Bases de Datos", 4),
                mat("CDHD400", "Asignatura de Economía y Sociedad", 4),
                mat("ADMD511", "Gestión Organizacional", 5),
                mat("ISWD523", "Diseño de Software", 5),
                mat("ICCD533", "Computación Gráfica", 5),
                mat("ISWD543", "Inteligencia Artificial y Aprendizaje Automático", 5),
                mat("ISWD553", "Bases de Datos Distribuidas", 5),
                mat("PSCD202", "Prácticas de Servicio Comunitario", 5),
                mat("ISWD613", "Aplicaciones Web", 6),
                mat("ISWD622", "Metodologías Ágiles", 6),
                mat("ISWD633", "Construcción y Evolución de Software", 6),
                mat("ISWD643", "Tecnologías de Seguridad", 6),
                mat("ISWD652", "Calidad del Software", 6),
                mat("ADMD611", "Gestión de Procesos y Calidad", 6),
                mat("ADMD711", "Ingeniería Financiera", 6),
                mat("ISWD713", "Aplicaciones Móviles", 7),
                mat("ISWD723", "Interacción Humano-Computador", 7),
                mat("ISWD732", "Usabilidad y Accesibilidad", 7),
                mat("ISWD743", "Business Intelligence", 7),
                mat("ISWD752", "Verificación y Validación de Software", 7),
                mat("ISWD762", "Automatización de Procesos", 7),
                mat("ISWD813", "Aplicaciones Web Avanzadas", 8),
                mat("ISWD823", "Desarrollo de Juegos Interactivos", 8),
                mat("ICCD833", "Auditoría Informática", 8),
                mat("ICCD842", "Profesionalismo en Informática", 8),
                mat("ISWD853", "Desarrollo de Software Seguro", 8),
                mat("ISWD861", "Diseño de Trabajo de Integración Curricular", 8),
                mat("ISWD913", "Sistemas Embebidos", 9),
                mat("ISWD922", "Gestión de Proyectos de Software", 9),
                mat("PRLD105", "Prácticas Laborales", 9),
                mat("TITD201", "Trabajo de Integración Curricular / Examen Complexivo", 9)
        ));

        MATERIAS_POR_CARRERA.put(Carrera.COMPUTACION, List.of(
                mat("MATD113", "Álgebra Lineal", 1),
                mat("MATD123", "Cálculo en una Variable", 1),
                mat("FISD134", "Mecánica Newtoniana", 1),
                mat("ICCD144", "Programación I", 1),
                mat("CSHD111", "Comunicación Oral y Escrita", 1),
                mat("MATD213", "Ecuaciones Diferenciales Ordinarias", 2),
                mat("ICCD224", "Matemáticas Computacionales y Teoría de la Computación", 2),
                mat("ICCD233", "Fundamentos de Electrónica para Computación", 2),
                mat("ICCD244", "Programación II", 2),
                mat("CSHD211", "Análisis Socioeconómico y Político del Ecuador", 2),
                mat("MATD223", "Probabilidad y Estadísticas Básicas", 3),
                mat("ICCD323", "Sistemas Operativos", 3),
                mat("ICCD332", "Arquitectura de Computadores", 3),
                mat("ICCD343", "Estructura de Datos y Algoritmos I", 3),
                mat("ICCD353", "Fundamentos de Redes y Conectividad", 3),
                mat("CSHD300", "Asignatura de Artes y Humanidades", 3),
                mat("ICCD412", "Métodos Numéricos", 4),
                mat("ICCD422", "Compiladores y Lenguajes", 4),
                mat("ICCD432", "Multiprocesamiento y Arquitecturas Alternativas", 4),
                mat("ICCD442", "Estructura de Datos y Algoritmos II", 4),
                mat("ICCD453", "Fundamentos de Bases de Datos", 4),
                mat("ICCD463", "Redes de Computadoras I", 4),
                mat("CDHD400", "Asignatura de Economía y Sociedad", 4),
                mat("ICCD512", "Ingeniería de Software I", 5),
                mat("ICCD523", "Inteligencia Artificial", 5),
                mat("ICCD533", "Computación Gráfica", 5),
                mat("ADMD511", "Gestión Organizacional", 5),
                mat("ISWD553", "Bases de Datos Distribuidas", 5),
                mat("ICCD563", "Redes de Computadoras II", 5),
                mat("ISWD613", "Aplicaciones Web", 6),
                mat("ICCD623", "Data Mining y Machine Learning", 6),
                mat("ICCD632", "Ingeniería de Software II", 6),
                mat("ISWD643", "Tecnologías de Seguridad", 6),
                mat("ICCD654", "Computación Distribuida", 6),
                mat("ISWD713", "Aplicaciones Móviles", 7),
                mat("ISWD723", "Interacción Humano-Computador", 7),
                mat("ICCD733", "Seguridad Informática", 7),
                mat("ISWD743", "Business Intelligence", 7),
                mat("ICCD753", "Recuperación de la Información", 7),
                mat("ICCD814", "Modelos y Simulación", 8),
                mat("ICCD823", "Cloud Computing", 8),
                mat("ICCD833", "Auditoría Informática", 8),
                mat("ICCD842", "Profesionalismo en Informática", 8),
                mat("ADMD711", "Ingeniería Financiera", 8),
                mat("ADMD611", "Gestión de Procesos y Calidad", 8),
                mat("TITD104", "Diseño de Trabajo de Integración Curricular", 8),
                mat("TITD201", "Trabajo de Integración Curricular / Examen Complexivo", 9),
                mat("PRLD105", "Prácticas Laborales", 9),
                mat("PSCD202", "Prácticas de Servicio Comunitario", 9),
                mat("ICCD943", "Gestión de Tecnologías de la Información y Comunicación", 9)
        ));

        MATERIAS_POR_CARRERA.put(Carrera.CIENCIA_DATOS, List.of(
                mat("MATD113", "Álgebra Lineal", 1),
                mat("MATD123", "Cálculo de una Variable", 1),
                mat("FISD134", "Mecánica Newtoniana", 1),
                mat("ICCD144", "Programación I", 1),
                mat("CSHD111", "Comunicación Oral y Escrita", 1),
                mat("MATD213", "Ecuaciones Diferenciales Ordinarias", 2),
                mat("ISID223", "Introducción a los Sistemas de Información", 2),
                mat("ISID232", "Fundamentos de Ciencias de la Computación", 2),
                mat("ICCD244", "Programación II", 2),
                mat("ICCD332", "Arquitectura de Computadores", 2),
                mat("CSHD211", "Análisis Socioeconómico y Político del Ecuador", 2),
                mat("ICCD353", "Fundamentos de Redes y Conectividad", 3),
                mat("MATD223", "Probabilidad y Estadísticas Básicas", 3),
                mat("ICCD323", "Sistemas Operativos", 3),
                mat("ICCD343", "Estructura de Datos y Algoritmos I", 3),
                mat("ISWD453", "Fundamentos de Bases de Datos", 3),
                mat("ISID413", "Fundamentos de Big Data", 4),
                mat("IDSD422", "Estadística y Programación para Ciencias de Datos I", 4),
                mat("ICCD422", "Compiladores y Lenguajes", 4),
                mat("ICCD442", "Estructura de Datos y Algoritmos II", 4),
                mat("ISWD553", "Bases de Datos Distribuidas", 4),
                mat("CSHD300", "Asignatura de Artes y Humanidades", 4),
                mat("PSCD202", "Prácticas de Servicio Comunitario", 4),
                mat("IDSD513", "Infraestructura para Big Data", 5),
                mat("IDSD522", "Estadística y Programación para Ciencias de Datos II", 5),
                mat("IDSD533", "Computación Numérica", 5),
                mat("ICCD523", "Inteligencia Artificial", 5),
                mat("ISID432", "Desarrollo y Mantenimiento de Software", 5),
                mat("CSHD400", "Asignatura de Economía y Sociedad", 5),
                mat("ADMD511", "Gestión Organizacional", 5),
                mat("IDSD613", "Almacenamiento de Datos Masivos", 6),
                mat("IDSD623", "Computación Paralela para Big Data", 6),
                mat("IDSD633", "Aprendizaje Automático I", 6),
                mat("ISWD613", "Aplicaciones Web", 6),
                mat("ICCD643", "Tecnologías de Seguridad", 6),
                mat("IDSD713", "Procesamiento de Datos Masivos", 7),
                mat("IDSD723", "Aprendizaje Automático II", 7),
                mat("IDSD733", "Minería de Datos I", 7),
                mat("IDSD742", "Seguridad y Privacidad de Datos", 7),
                mat("ISID642", "User Experience", 7),
                mat("ADMD611", "Gestión de Procesos y Calidad", 7),
                mat("ADMD711", "Ingeniería Financiera", 7),
                mat("IDSD813", "Visualización de Datos", 8),
                mat("IDSD823", "Servicios en la Nube para Big Data", 8),
                mat("IDSD833", "Minería de Datos II", 8),
                mat("IDSD843", "Analítica Avanzada", 8),
                mat("ICCD842", "Profesionalismo en Informática", 8),
                mat("TITD104", "Diseño de Trabajo de Integración Curricular", 8),
                mat("TITD201", "Trabajo de Integración Curricular / Examen Complexivo", 9),
                mat("PRLD105", "Prácticas Laborales", 9),
                mat("IDSD933", "Aplicaciones Emergentes de Ciencia de Datos", 9),
                mat("IDSD942", "Administración de Proyectos de Ciencia de Datos", 9)
        ));

        MATERIAS_POR_CARRERA.put(Carrera.SISTEMAS_INFORMACION, List.of(
                mat("MATD113", "Álgebra Lineal", 1),
                mat("MATD123", "Cálculo de una Variable", 1),
                mat("FISD134", "Mecánica Newtoniana", 1),
                mat("ICCD144", "Programación I", 1),
                mat("CSHD111", "Comunicación Oral y Escrita", 1),
                mat("MATD213", "Ecuaciones Diferenciales Ordinarias", 2),
                mat("ISID223", "Introducción a los Sistemas de Información", 2),
                mat("ISID232", "Fundamentos de Ciencias de la Computación", 2),
                mat("ICCD244", "Programación II", 2),
                mat("ICCD332", "Arquitectura de Computadores", 2),
                mat("CSHD211", "Análisis Socioeconómico y Político del Ecuador", 2),
                mat("ICCD353", "Fundamentos de Redes y Conectividad", 3),
                mat("MATD223", "Probabilidad y Estadísticas Básicas", 3),
                mat("ICCD323", "Sistemas Operativos", 3),
                mat("ICCD343", "Estructura de Datos y Algoritmos I", 3),
                mat("ISWD453", "Fundamentos de Bases de Datos", 3),
                mat("ISID413", "Administración de la Información y Datos", 4),
                mat("ISID423", "Análisis y Diseño de Sistemas de Información", 4),
                mat("ISID432", "Desarrollo y Mantenimiento de Software", 4),
                mat("ICCD442", "Estructura de Datos y Algoritmos II", 4),
                mat("ISWD553", "Bases de Datos Distribuidas", 4),
                mat("CSHD300", "Asignatura de Artes y Humanidades", 4),
                mat("CSHD400", "Asignatura de Economía y Sociedad", 4),
                mat("ICCD533", "Computación Gráfica", 5),
                mat("ISWD732", "Usabilidad y Accesibilidad", 5),
                mat("ISID533", "Arquitectura Empresarial", 5),
                mat("ISWD723", "Interacción Humano-Computador", 5),
                mat("ISID553", "Infraestructura de Tecnologías de Información", 5),
                mat("ADMD511", "Gestión Organizacional", 5),
                mat("ICCD753", "Recuperación de Información", 6),
                mat("ISID623", "Automatización de Procesos de Negocio", 6),
                mat("ISID633", "Gestión del Conocimiento", 6),
                mat("ISID642", "User Experience", 6),
                mat("ISID653", "Fundamentos de Ciencia de Datos", 6),
                mat("ADMD611", "Gestión de Procesos y Calidad", 6),
                mat("ICCD523", "Inteligencia Artificial", 7),
                mat("ISID723", "Analítica Predictiva", 7),
                mat("ICCD733", "Seguridad Informática", 7),
                mat("ISID743", "Gobernanza y Calidad de Datos", 7),
                mat("ISID752", "Liderazgo y Comunicación", 7),
                mat("ADMD711", "Ingeniería Financiera", 7),
                mat("ICCD823", "Cloud Computing", 8),
                mat("ISWD743", "Business Intelligence", 8),
                mat("ICCD833", "Auditoría Informática", 8),
                mat("ISID843", "Gestión de Proyectos de Sistemas de Información", 8),
                mat("ISID852", "Internet of Things", 8),
                mat("TITD104", "Diseño de Trabajo de Integración Curricular", 8),
                mat("TITD201", "Trabajo de Integración Curricular / Examen Complexivo", 9),
                mat("PRLD105", "Prácticas Laborales", 9),
                mat("PSCD202", "Prácticas de Servicio Comunitario", 9),
                mat("ISID943", "Sistemas Empresariales", 9)
        ));
    }

    private CatalogoRegistro() {
    }

    private static Materia mat(String codigo, String nombre, int semestre) {
        return new Materia(codigo, nombre, semestre);
    }

    public static Semestre[] semestres() {
        return Semestre.values();
    }

    public static Carrera[] carreras() {
        return Carrera.values();
    }

    /**
     * Busca materias del catálogo por código exacto o por coincidencia parcial en el nombre.
     */
    public static List<Materia> buscarMateriasPorNombreOCodigo(String termino) {
        if (termino == null || termino.isBlank()) {
            return List.of();
        }

        String terminoLimpio = termino.trim();
        String terminoLower = terminoLimpio.toLowerCase();
        Set<String> vistos = new LinkedHashSet<>();
        List<Materia> resultados = new ArrayList<>();

        for (List<Materia> materias : MATERIAS_POR_CARRERA.values()) {
            for (Materia materia : materias) {
                if (vistos.add(materia.getCodigo()) && coincideMateria(materia, terminoLimpio, terminoLower)) {
                    resultados.add(materia);
                }
            }
        }

        return resultados;
    }

    public static List<String> codigosDeMaterias(List<Materia> materias) {
        return materias.stream().map(Materia::getCodigo).toList();
    }

    private static boolean coincideMateria(Materia materia, String terminoLimpio, String terminoLower) {
        return materia.getCodigo().equalsIgnoreCase(terminoLimpio)
                || materia.getCodigo().toLowerCase().contains(terminoLower)
                || materia.getNombre().toLowerCase().contains(terminoLower);
    }

    public static String materiasPorCarreraJson() {
        StringBuilder json = new StringBuilder("{");
        Carrera[] carreras = Carrera.values();
        for (int i = 0; i < carreras.length; i++) {
            Carrera carrera = carreras[i];
            json.append('"').append(carrera.name()).append("\":[");
            List<Materia> materias = MATERIAS_POR_CARRERA.get(carrera);
            for (int j = 0; j < materias.size(); j++) {
                Materia materia = materias.get(j);
                json.append("{\"codigo\":\"").append(escapeJson(materia.getCodigo()))
                        .append("\",\"nombre\":\"").append(escapeJson(materia.getNombre()))
                        .append("\",\"semestre\":").append(materia.getSemestre()).append('}');
                if (j < materias.size() - 1) {
                    json.append(',');
                }
            }
            json.append(']');
            if (i < carreras.length - 1) {
                json.append(',');
            }
        }
        json.append('}');
        return json.toString();
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
