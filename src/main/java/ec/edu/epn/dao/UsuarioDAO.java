package ec.edu.epn.dao;

import ec.edu.epn.modelo.Carrera;
import ec.edu.epn.modelo.Rol;
import ec.edu.epn.modelo.TutorPerfilDetalle;
import ec.edu.epn.modelo.TutorResumen;
import ec.edu.epn.modelo.Usuario;
import ec.edu.epn.util.CatalogoRegistro;
import ec.edu.epn.util.HibernateUtil;
import ec.edu.epn.util.MateriaUtil;
import ec.edu.epn.util.PasswordUtil;
import ec.edu.epn.util.TutorPerfilBuilder;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class UsuarioDAO {

    public boolean existePorEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                    "select count(u.id) from Usuario u where lower(u.email) = lower(:email)",
                    Long.class
            );
            query.setParameter("email", email.trim());
            Long count = query.uniqueResult();
            return count != null && count > 0;
        }
    }

    public void guardar(Usuario usuario) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(usuario);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public Usuario autenticar(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return null;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Usuario> query = session.createQuery(
                    "from Usuario u where lower(u.email) = lower(:email)",
                    Usuario.class
            );
            query.setParameter("email", email.trim());

            Usuario usuario = query.uniqueResult();
            if (usuario == null) {
                return null;
            }

            return PasswordUtil.hash(password).equals(usuario.getPassword()) ? usuario : null;
        }
    }

    public Optional<Usuario> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Usuario.class, id));
        }
    }

    public Optional<TutorPerfilDetalle> buscarPerfilTutor(Long id) {
        return buscarPorId(id)
                .filter(u -> u.getRol() == Rol.TUTOR)
                .map(TutorPerfilBuilder::construir);
    }

    public void actualizarMaterias(Long tutorId, String materiasCsv) {
        if (tutorId == null) {
            throw new IllegalArgumentException("El tutor es obligatorio.");
        }
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Usuario tutor = session.get(Usuario.class, tutorId);
            if (tutor == null || tutor.getRol() != Rol.TUTOR) {
                throw new IllegalStateException("Tutor no encontrado.");
            }
            tutor.setMaterias(materiasCsv);
            session.merge(tutor);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public List<TutorResumen> buscarTutoresPorMateria(String termino) {
        return buscarTutores(null, termino).stream()
                .map(this::toTutorResumen)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Usuario> buscarTutores(Carrera carrera, String terminoMateria) {
        boolean filtrarCarrera = carrera != null;
        boolean filtrarMateria = terminoMateria != null && !terminoMateria.isBlank();
        if (!filtrarCarrera && !filtrarMateria) {
            return List.of();
        }

        Set<String> codigosBuscados = new LinkedHashSet<>();
        if (filtrarMateria) {
            codigosBuscados.addAll(CatalogoRegistro.codigosDeMaterias(
                    CatalogoRegistro.buscarMateriasPorNombreOCodigo(terminoMateria)));
            codigosBuscados.add(terminoMateria.trim());
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Usuario> query = session.createQuery(
                    "from Usuario u where u.rol = :rol and u.materias is not null and u.materias <> ''",
                    Usuario.class
            );
            query.setParameter("rol", Rol.TUTOR);

            return query.list().stream()
                    .filter(u -> !filtrarCarrera || carrera == u.getCarrera())
                    .filter(u -> !filtrarMateria || MateriaUtil.tutorImparteAlguna(
                            MateriaUtil.parseCodigos(u.getMaterias()), codigosBuscados))
                    .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    private TutorResumen toTutorResumen(Usuario usuario) {
        String nombreCompleto = usuario.getNombre();
        if (usuario.getSegundoNombre() != null && !usuario.getSegundoNombre().isBlank()) {
            nombreCompleto += " " + usuario.getSegundoNombre();
        }
        nombreCompleto += " " + usuario.getApellido();
        if (usuario.getSegundoApellido() != null && !usuario.getSegundoApellido().isBlank()) {
            nombreCompleto += " " + usuario.getSegundoApellido();
        }

        String carrera = usuario.getCarrera() != null ? usuario.getCarrera().getNombre() : null;
        String semestre = usuario.getSemestre() != null ? usuario.getSemestre().getNombre() : null;

        return new TutorResumen(
                usuario.getId(),
                nombreCompleto.trim(),
                usuario.getEmail(),
                carrera,
                semestre,
                MateriaUtil.toList(usuario.getMaterias())
        );
    }
}
