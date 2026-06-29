package ec.edu.epn.dao;

import ec.edu.epn.modelo.Horario;
import ec.edu.epn.modelo.MateriaCatalogo;
import ec.edu.epn.modelo.Usuario;
import ec.edu.epn.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
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
        String inicio = HorarioUtil.normalizarHora(horaInicio);
        String fin = HorarioUtil.normalizarHora(horaFin);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Horario> query = session.createQuery(
                    "from Horario h where h.tutor.id = :tutorId and h.fecha = :fecha "
                            + "and h.horaInicio = :horaInicio and h.horaFin = :horaFin",
                    Horario.class
            );
            query.setParameter("tutorId", tutorId);
            query.setParameter("fecha", fecha);
            query.setParameter("horaInicio", inicio);
            query.setParameter("horaFin", fin);
            return query.uniqueResultOptional();
        }
    }

    public Horario crear(Long tutorId, String fecha, String horaInicio, String horaFin) {
        String inicio = HorarioUtil.normalizarHora(horaInicio);
        String fin = HorarioUtil.normalizarHora(horaFin);

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Horario horario = new Horario();
            horario.setTutor(session.getReference(Usuario.class, tutorId));
            horario.setFecha(fecha);
            horario.setHoraInicio(inicio);
            horario.setHoraFin(fin);
            horario.setDisponible(true);
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

    /** @deprecated Usar {@link #crear(Long, String, String, String)}. */
    @Deprecated
    public Horario crear(Usuario tutor, String fecha, String horaInicio, String horaFin) {
        return crear(tutor.getId(), fecha, horaInicio, horaFin);
    }

    /** Compatibilidad con código legado; la materia ya no se asocia al bloque horario. */
    public Horario crear(Usuario tutor, MateriaCatalogo materia, String fecha,
                         String horaInicio, String horaFin) {
        return crear(tutor, fecha, horaInicio, horaFin);
    }

    /** Devuelve todos los horarios de un tutor, ordenados por fecha y hora de inicio. */
    public List<Horario> listarPorTutor(Long tutorId) {
        if (tutorId == null) {
            return List.of();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from Horario h where h.tutor.id = :tutorId order by h.fecha asc, h.horaInicio asc",
                    Horario.class
            ).setParameter("tutorId", tutorId).list();
        }
    }

    /**
     * Elimina un horario solo si pertenece al tutor indicado.
     *
     * @return true si se eliminó, false si no existe o no pertenece al tutor.
     */
    public boolean eliminarSiEsPropietario(Long horarioId, Long tutorId) {
        if (horarioId == null || tutorId == null) {
            return false;
        }
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Horario horario = session.get(Horario.class, horarioId);
            if (horario == null || !horario.getTutor().getId().equals(tutorId)) {
                transaction.rollback();
                return false;
            }
            session.remove(horario);
            transaction.commit();
            return true;
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }
}