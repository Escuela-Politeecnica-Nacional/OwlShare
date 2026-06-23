package ec.edu.epn.util;

import ec.edu.epn.modelo.MateriaDetalle;
import ec.edu.epn.modelo.Semestre;
import ec.edu.epn.modelo.TrayectoriaSemestre;
import ec.edu.epn.modelo.TutorPerfilDetalle;
import ec.edu.epn.modelo.Usuario;

import java.util.ArrayList;
import java.util.List;

public final class TutorPerfilBuilder {

    private TutorPerfilBuilder() {
    }

    public static TutorPerfilDetalle construir(Usuario usuario) {
        String nombreCompleto = nombreCompleto(usuario);
        String carreraNombre = usuario.getCarrera() != null ? usuario.getCarrera().getNombre() : null;
        String carreraCodigo = usuario.getCarrera() != null ? usuario.getCarrera().name() : null;
        int semestreNumero = usuario.getSemestre() != null ? usuario.getSemestre().getNumero() : 0;
        String semestreNombre = usuario.getSemestre() != null ? usuario.getSemestre().getNombre() : null;

        List<MateriaDetalle> materias = CatalogoRegistro
                .materiasPorCodigos(MateriaUtil.toList(usuario.getMaterias()))
                .stream()
                .map(m -> new MateriaDetalle(m.getCodigo(), m.getNombre(), m.getSemestre()))
                .toList();

        return new TutorPerfilDetalle(
                usuario.getId(),
                nombreCompleto,
                usuario.getEmail(),
                carreraNombre,
                carreraCodigo,
                semestreNombre,
                semestreNumero,
                construirTrayectoria(semestreNumero),
                materias
        );
    }

    public static List<TrayectoriaSemestre> construirTrayectoria(int semestreActual) {
        List<TrayectoriaSemestre> trayectoria = new ArrayList<>();
        for (Semestre semestre : Semestre.values()) {
            if (semestre.getNumero() > semestreActual) {
                continue;
            }
            String estado = semestre.getNumero() == semestreActual ? "cursando" : "cursado";
            trayectoria.add(new TrayectoriaSemestre(
                    semestre.getNumero(),
                    semestre.getNombre(),
                    estado
            ));
        }
        return trayectoria;
    }

    private static String nombreCompleto(Usuario usuario) {
        StringBuilder nombre = new StringBuilder(usuario.getNombre());
        if (usuario.getSegundoNombre() != null && !usuario.getSegundoNombre().isBlank()) {
            nombre.append(' ').append(usuario.getSegundoNombre());
        }
        nombre.append(' ').append(usuario.getApellido());
        if (usuario.getSegundoApellido() != null && !usuario.getSegundoApellido().isBlank()) {
            nombre.append(' ').append(usuario.getSegundoApellido());
        }
        return nombre.toString().trim();
    }
}
