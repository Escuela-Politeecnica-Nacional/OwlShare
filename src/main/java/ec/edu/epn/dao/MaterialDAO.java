package ec.edu.epn.dao;

import ec.edu.epn.modelo.Carrera;
import ec.edu.epn.modelo.EstadoMaterial;
import ec.edu.epn.modelo.Material;
import ec.edu.epn.modelo.MaterialResumenTutor;
import ec.edu.epn.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDateTime;
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
        return listarPorTutor(tutorId, null);
    }

    public List<Material> listarPorTutor(Long tutorId, EstadoMaterial estado) {
        if (tutorId == null) {
            return List.of();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            StringBuilder hql = new StringBuilder(
                    "from Material m where m.idTutor = :tutorId");
            if (estado != null) {
                hql.append(" and m.estado = :estado");
            }
            hql.append(" order by m.fechaRegistro desc");

            Query<Material> query = session.createQuery(hql.toString(), Material.class);
            query.setParameter("tutorId", tutorId);
            if (estado != null) {
                query.setParameter("estado", estado);
            }
            return query.list();
        }
    }

    public MaterialResumenTutor resumenPorTutor(Long tutorId) {
        if (tutorId == null) {
            return MaterialResumenTutor.vacio();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Object[]> filas = session.createQuery(
                    "select m.estado, count(m.id) from Material m "
                            + "where m.idTutor = :tutorId group by m.estado",
                    Object[].class
            ).setParameter("tutorId", tutorId).list();

            long aprobados = 0;
            long pendientes = 0;
            long rechazados = 0;
            for (Object[] fila : filas) {
                EstadoMaterial estado = (EstadoMaterial) fila[0];
                long cantidad = fila[1] != null ? (Long) fila[1] : 0L;
                switch (estado) {
                    case APROBADO -> aprobados = cantidad;
                    case PENDIENTE -> pendientes = cantidad;
                    case RECHAZADO -> rechazados = cantidad;
                }
            }
            return new MaterialResumenTutor(
                    aprobados + pendientes + rechazados,
                    aprobados,
                    pendientes,
                    rechazados
            );
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
        return resumenPorTutor(tutorId).getTotal();
    }

    public Optional<Material> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Material.class, id));
        }
    }

    public Optional<Material> buscarPorIdYTutor(Long id, Long tutorId) {
        if (id == null || tutorId == null) {
            return Optional.empty();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Material material = session.get(Material.class, id);
            if (material == null || !tutorId.equals(material.getIdTutor())) {
                return Optional.empty();
            }
            return Optional.of(material);
        }
    }

    public List<Material> listarPorEstado(EstadoMaterial estado) {
        if (estado == null) {
            return List.of();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from Material m where m.estado = :estado order by m.fechaRegistro asc",
                    Material.class
            ).setParameter("estado", estado).list();
        }
    }

    public long contarPorEstado(EstadoMaterial estado) {
        if (estado == null) {
            return 0;
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long total = session.createQuery(
                    "select count(m.id) from Material m where m.estado = :estado",
                    Long.class
            ).setParameter("estado", estado).uniqueResult();
            return total != null ? total : 0;
        }
    }

    public void revisarMaterial(Long materialId, EstadoMaterial nuevoEstado, Long adminId, String comentario) {
        if (materialId == null || nuevoEstado == null || adminId == null) {
            throw new IllegalArgumentException("Datos de revisión incompletos.");
        }
        if (nuevoEstado != EstadoMaterial.APROBADO && nuevoEstado != EstadoMaterial.RECHAZADO) {
            throw new IllegalArgumentException("Estado de revisión no válido.");
        }

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Material material = session.get(Material.class, materialId);
            if (material == null) {
                throw new IllegalStateException("Material no encontrado.");
            }
            if (material.getEstado() != EstadoMaterial.PENDIENTE) {
                throw new IllegalStateException("El material ya fue revisado.");
            }
            material.setEstado(nuevoEstado);
            material.setIdAdminRevisor(adminId);
            material.setFechaRevision(LocalDateTime.now());
            material.setComentarioAdmin(
                    nuevoEstado == EstadoMaterial.RECHAZADO ? comentario : null
            );
            session.merge(material);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public Optional<String> eliminarPorTutor(Long materialId, Long tutorId) {
        if (materialId == null || tutorId == null) {
            return Optional.empty();
        }
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Material material = session.get(Material.class, materialId);
            if (material == null || !tutorId.equals(material.getIdTutor())) {
                if (transaction != null) {
                    transaction.rollback();
                }
                return Optional.empty();
            }
            if (material.getEstado() == EstadoMaterial.APROBADO) {
                throw new IllegalStateException("No se puede eliminar un material aprobado.");
            }
            String ruta = material.getRutaAlmacenamiento();
            session.remove(material);
            transaction.commit();
            return Optional.ofNullable(ruta);
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
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
