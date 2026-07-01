package ec.edu.epn.dao;

import ec.edu.epn.modelo.MaterialAdquisicion;
import ec.edu.epn.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.HashSet;
import java.util.Set;

public class MaterialAdquisicionDAO {

    public Set<Long> idsAdquiridosPorEstudiante(Long estudianteId) {
        if (estudianteId == null) {
            return Set.of();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return new HashSet<>(session.createQuery(
                    "select a.idMaterial from MaterialAdquisicion a where a.idEstudiante = :estudianteId",
                    Long.class
            ).setParameter("estudianteId", estudianteId).list());
        }
    }

    public boolean yaAdquirido(Long materialId, Long estudianteId) {
        if (materialId == null || estudianteId == null) {
            return false;
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long total = session.createQuery(
                    "select count(a.id) from MaterialAdquisicion a "
                            + "where a.idMaterial = :materialId and a.idEstudiante = :estudianteId",
                    Long.class
            )
                    .setParameter("materialId", materialId)
                    .setParameter("estudianteId", estudianteId)
                    .uniqueResult();
            return total != null && total > 0;
        }
    }

    public long contarAdquiridosPorEstudiante(Long estudianteId) {
        if (estudianteId == null) {
            return 0;
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long total = session.createQuery(
                    "select count(a.id) from MaterialAdquisicion a where a.idEstudiante = :estudianteId",
                    Long.class
            ).setParameter("estudianteId", estudianteId).uniqueResult();
            return total != null ? total : 0;
        }
    }

    public void registrar(Long materialId, Long estudianteId) {
        if (materialId == null || estudianteId == null) {
            throw new IllegalArgumentException("Material y estudiante son obligatorios.");
        }
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(new MaterialAdquisicion(materialId, estudianteId));
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
