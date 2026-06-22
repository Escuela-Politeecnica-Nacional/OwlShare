package ec.edu.epn.util;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class MateriaUtil {

    private MateriaUtil() {
    }

    public static Set<String> parseCodigos(String materiasCsv) {
        Set<String> codigos = new LinkedHashSet<>();
        if (materiasCsv == null || materiasCsv.isBlank()) {
            return codigos;
        }
        Arrays.stream(materiasCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .forEach(codigos::add);
        return codigos;
    }

    public static List<String> toList(String materiasCsv) {
        return parseCodigos(materiasCsv).stream().toList();
    }

    public static boolean tutorImparteAlguna(Set<String> materiasTutor, Set<String> codigosBuscados) {
        for (String codigo : codigosBuscados) {
            if (contieneCodigo(materiasTutor, codigo)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contieneCodigo(Set<String> materiasTutor, String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return false;
        }
        String normalizado = codigo.trim();
        return materiasTutor.stream().anyMatch(m -> m.equalsIgnoreCase(normalizado));
    }
}
