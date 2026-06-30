package ec.edu.epn.dao;

import ec.edu.epn.modelo.Carrera;
import ec.edu.epn.modelo.EstadoMaterial;
import ec.edu.epn.modelo.Material;
import ec.edu.epn.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class MaterialDAO {

    public void guardar(Material material) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(material);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public List<Material> listarPorTutor(Long tutorId) {
        if (tutorId == null) {
            return List.of();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from Material m where m.idTutor = :tutorId order by m.fechaRegistro desc",
                    Material.class
            ).setParameter("tutorId", tutorId).list();
        }
    }

    public long contarPorTutorYEstado(Long tutorId, EstadoMaterial estado) {
        if (tutorId == null || estado == null) {
            return 0;
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long total = session.createQuery(
                    "select count(m.id) from Material m where m.idTutor = :tutorId and m.estado = :estado",
                    Long.class
            )
                    .setParameter("tutorId", tutorId)
                    .setParameter("estado", estado)
                    .uniqueResult();
            return total != null ? total : 0;
        }
    }

    public long contarPorTutor(Long tutorId) {
        if (tutorId == null) {
            return 0;
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long total = session.createQuery(
                    "select count(m.id) from Material m where m.idTutor = :tutorId",
                    Long.class
            ).setParameter("tutorId", tutorId).uniqueResult();
            return total != null ? total : 0;
        }
    }

    public Optional<Material> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Material.class, id));
        }
    }

    public List<Material> listarAprobados(Carrera carrera, String busqueda) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            StringBuilder hql = new StringBuilder(
                    "select m from Material m, MateriaCatalogo mc "
                            + "where m.codigoMateria = mc.codigo and m.estado = :estado");
            if (carrera != null) {
                hql.append(" and mc.carrera = :carrera");
            }
            if (busqueda != null && !busqueda.isBlank()) {
                hql.append(" and lower(m.titulo) like lower(:busqueda)");
            }
            hql.append(" order by m.fechaRegistro desc");

            Query<Material> query = session.createQuery(hql.toString(), Material.class);
            query.setParameter("estado", EstadoMaterial.APROBADO);
            if (carrera != null) {
                query.setParameter("carrera", carrera);
            }
            if (busqueda != null && !busqueda.isBlank()) {
                query.setParameter("busqueda", "%" + busqueda.trim() + "%");
            }
            return query.list();
        }
    }
}
