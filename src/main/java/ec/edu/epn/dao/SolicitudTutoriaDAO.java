package ec.edu.epn.dao;

import ec.edu.epn.modelo.EstadoSolicitud;
import ec.edu.epn.modelo.SolicitudTutoria;
import ec.edu.epn.util.HibernateUtil;
import ec.edu.epn.util.HorarioUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class SolicitudTutoriaDAO {

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
}
