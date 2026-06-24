package ec.edu.epn.dao;

import ec.edu.epn.modelo.Horario;
import ec.edu.epn.modelo.MateriaCatalogo;
import ec.edu.epn.modelo.Usuario;
import ec.edu.epn.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Optional;

public class HorarioDAO {

    public Optional<Horario> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Horario.class, id));
        }
    }

    public Optional<Horario> buscarPorTutorFechaYHoras(Long tutorId, String fecha,
                                                       String horaInicio, String horaFin) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Horario> query = session.createQuery(
                    "from Horario h where h.tutor.id = :tutorId and h.fecha = :fecha "
                            + "and h.horaInicio = :horaInicio and h.horaFin = :horaFin",
                    Horario.class
            );
            query.setParameter("tutorId", tutorId);
            query.setParameter("fecha", fecha);
            query.setParameter("horaInicio", horaInicio);
            query.setParameter("horaFin", horaFin);
            return query.uniqueResultOptional();
        }
    }

    public Horario crear(Usuario tutor, MateriaCatalogo materia, String fecha,
                         String horaInicio, String horaFin) {
        Horario horario = new Horario();
        horario.setTutor(tutor);
        horario.setMateria(materia);
        horario.setFecha(fecha);
        horario.setHoraInicio(horaInicio);
        horario.setHoraFin(horaFin);
        horario.setDisponible(true);

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(horario);
            transaction.commit();
            return horario;
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
