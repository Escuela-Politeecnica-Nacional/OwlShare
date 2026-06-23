package ec.edu.epn.dao;

import ec.edu.epn.modelo.Rol;
import ec.edu.epn.modelo.TutorResumen;
import ec.edu.epn.modelo.Usuario;
import ec.edu.epn.util.CatalogoRegistro;
import ec.edu.epn.util.HibernateUtil;
import ec.edu.epn.util.MateriaUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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
            return query.uniqueResult() > 0;
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
        return null; // implementación pendiente
    public List<TutorResumen> buscarTutoresPorMateria(String termino) {
        if (termino == null || termino.isBlank()) {
            return List.of();
        }

        Set<String> codigosBuscados = new LinkedHashSet<>(
                CatalogoRegistro.codigosDeMaterias(CatalogoRegistro.buscarMateriasPorNombreOCodigo(termino))
        );
        codigosBuscados.add(termino.trim());

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Usuario> query = session.createQuery(
                    "from Usuario u where u.rol = :rol and u.materias is not null and u.materias <> ''",
                    Usuario.class
            );
            query.setParameter("rol", Rol.TUTOR);

            return query.list().stream()
                    .filter(u -> MateriaUtil.tutorImparteAlguna(MateriaUtil.parseCodigos(u.getMaterias()), codigosBuscados))
                    .map(this::toTutorResumen)
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
