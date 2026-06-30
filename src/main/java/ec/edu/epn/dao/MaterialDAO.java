package ec.edu.epn.dao;

import ec.edu.epn.modelo.EstadoMaterial;
import ec.edu.epn.modelo.Material;
import ec.edu.epn.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

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
}
