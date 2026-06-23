package ec.edu.epn.dao;

import ec.edu.epn.modelo.EstadoSolicitud;
import ec.edu.epn.modelo.Solicitud;
import ec.edu.epn.util.HibernateUtil;
import ec.edu.epn.util.HorarioUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class SolicitudDAO {

    public boolean existeConflictoHorarioTutor(Long tutorId, String fecha,
                                               String horaInicio, String horaFin) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Solicitud> query = session.createQuery(
                    "from Solicitud s where s.tutorId = :tutorId and s.fecha = :fecha "
                            + "and s.estado in (:estados)",
                    Solicitud.class
            );
            query.setParameter("tutorId", tutorId);
            query.setParameter("fecha", fecha);
            query.setParameter("estados", List.of(EstadoSolicitud.PENDIENTE, EstadoSolicitud.ACEPTADA));

            return query.list().stream()
                    .anyMatch(s -> HorarioUtil.haySolapamiento(
                            horaInicio, horaFin, s.getHoraInicio(), s.getHoraFin()));
        }
    }

    public void guardar(Solicitud solicitud) {
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
