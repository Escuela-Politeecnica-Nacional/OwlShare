package ec.edu.epn.dao;

import ec.edu.epn.modelo.EstadoSolicitud;
import ec.edu.epn.modelo.Horario;
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
        if (id == null) return Optional.empty();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(SolicitudTutoria.class, id));
        }
    }

    /**
     * Lista todas las solicitudes recibidas por un tutor (a través de sus horarios),
     * ordenadas de más reciente a más antigua.
     */
    public List<SolicitudTutoria> listarPorTutor(Long tutorId) {
        if (tutorId == null) return List.of();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from SolicitudTutoria s where s.horario.tutor.id = :tutorId order by s.id desc",
                    SolicitudTutoria.class
            ).setParameter("tutorId", tutorId).list();
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
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    /**
     * Actualiza el estado (y comentario opcional) de una solicitud existente.
     * También marca el horario como no disponible si se acepta,
     * o lo libera si se rechaza.
     */
    public void actualizarEstado(SolicitudTutoria solicitud) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(solicitud);
            // Sincronizar disponibilidad del horario
            Horario horario = solicitud.getHorario();
            horario.setDisponible(solicitud.getEstado() != EstadoSolicitud.ACEPTADA);
            session.merge(horario);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
}