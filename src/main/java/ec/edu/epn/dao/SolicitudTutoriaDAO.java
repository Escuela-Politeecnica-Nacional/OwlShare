package ec.edu.epn.dao;

import ec.edu.epn.modelo.EstadoSolicitud;
import ec.edu.epn.modelo.SolicitudTutoria;
import ec.edu.epn.util.HibernateUtil;
import ec.edu.epn.util.HorarioUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class SolicitudTutoriaDAO {

    public Optional<SolicitudTutoria> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<SolicitudTutoria> query = session.createQuery(
                    "select s from SolicitudTutoria s "
                            + "join fetch s.estudiante "
                            + "join fetch s.horario h "
                            + "join fetch h.tutor "
                            + "join fetch s.materia "
                            + "where s.id = :id",
                    SolicitudTutoria.class
            );
            query.setParameter("id", id);
            return query.uniqueResultOptional();
        }
    }

    public List<SolicitudTutoria> listarPorTutor(Long tutorId) {
        if (tutorId == null) {
            return List.of();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<SolicitudTutoria> query = session.createQuery(
                    "select s from SolicitudTutoria s "
                            + "join fetch s.estudiante "
                            + "join fetch s.horario h "
                            + "join fetch h.tutor t "
                            + "join fetch s.materia "
                            + "where t.id = :tutorId "
                            + "order by s.id desc",
                    SolicitudTutoria.class
            );
            query.setParameter("tutorId", tutorId);
            return query.list();
        }
    }

    public List<SolicitudTutoria> listarAgendadasPorTutor(Long tutorId) {
        if (tutorId == null) {
            return List.of();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<SolicitudTutoria> query = session.createQuery(
                    "select s from SolicitudTutoria s "
                            + "join fetch s.estudiante "
                            + "join fetch s.horario h "
                            + "join fetch h.tutor t "
                            + "join fetch s.materia "
                            + "where t.id = :tutorId and s.estado in (:estados) "
                            + "order by h.fecha asc, h.horaInicio asc",
                    SolicitudTutoria.class
            );
            query.setParameter("tutorId", tutorId);
            query.setParameter("estados", List.of(EstadoSolicitud.PENDIENTE, EstadoSolicitud.ACEPTADA));
            return query.list();
        }
    }

    public boolean existeConflictoHorarioTutor(Long tutorId, String fecha,
                                               String horaInicio, String horaFin) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<SolicitudTutoria> query = session.createQuery(
                    "from SolicitudTutoria s where s.horario.tutor.id = :tutorId "
                            + "and s.horario.fecha = :fecha and s.estado in (:estados)",
                    SolicitudTutoria.class
            );
            query.setParameter("tutorId", tutorId);
            query.setParameter("fecha", fecha);
            query.setParameter("estados", List.of(EstadoSolicitud.PENDIENTE, EstadoSolicitud.ACEPTADA));

            return query.list().stream()
                    .anyMatch(s -> HorarioUtil.haySolapamiento(
                            horaInicio, horaFin,
                            s.getHorario().getHoraInicio(), s.getHorario().getHoraFin()));
        }
    }

    public boolean horarioTieneSolicitudActiva(Long horarioId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                    "select count(s.id) from SolicitudTutoria s "
                            + "where s.horario.id = :horarioId and s.estado in (:estados)",
                    Long.class
            );
            query.setParameter("horarioId", horarioId);
            query.setParameter("estados", List.of(EstadoSolicitud.PENDIENTE, EstadoSolicitud.ACEPTADA));
            Long count = query.uniqueResult();
            return count != null && count > 0;
        }
    }

    public void guardar(SolicitudTutoria solicitud) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(solicitud);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public void actualizarEstado(Long solicitudId, EstadoSolicitud nuevoEstado) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            SolicitudTutoria solicitud = session.get(SolicitudTutoria.class, solicitudId);
            if (solicitud == null) {
                throw new IllegalArgumentException("Solicitud no encontrada.");
            }
            solicitud.setEstado(nuevoEstado);
            solicitud.getHorario().setDisponible(nuevoEstado != EstadoSolicitud.ACEPTADA);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
