package ec.edu.epn.dao;

import ec.edu.epn.modelo.EstadoSolicitud;
import ec.edu.epn.modelo.Horario;
import ec.edu.epn.modelo.MateriaCatalogo;
import ec.edu.epn.modelo.SolicitudTutoria;
import ec.edu.epn.modelo.Usuario;
import ec.edu.epn.util.HibernateUtil;
import ec.edu.epn.util.HorarioUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class SolicitudTutoriaDAO {

    public record SolicitudCreada(Long solicitudId, Long horarioId) {
    }

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
                            HorarioUtil.normalizarHora(horaInicio),
                            HorarioUtil.normalizarHora(horaFin),
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

    public SolicitudCreada crearSolicitudConHorario(Long estudianteId, Long tutorId, String codigoMateria,
                                                    String fecha, String horaInicio, String horaFin,
                                                    String comentario) {
        String inicio = HorarioUtil.normalizarHora(horaInicio);
        String fin = HorarioUtil.normalizarHora(horaFin);

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            if (session.get(Usuario.class, estudianteId) == null) {
                throw new IllegalArgumentException("Estudiante no encontrado.");
            }
            if (session.get(Usuario.class, tutorId) == null) {
                throw new IllegalArgumentException("Tutor no encontrado.");
            }
            if (session.get(MateriaCatalogo.class, codigoMateria) == null) {
                throw new IllegalArgumentException("Materia no encontrada.");
            }

            Horario horario = session.createQuery(
                    "from Horario h where h.tutor.id = :tutorId and h.fecha = :fecha "
                            + "and h.horaInicio = :horaInicio and h.horaFin = :horaFin",
                    Horario.class
            )
                    .setParameter("tutorId", tutorId)
                    .setParameter("fecha", fecha)
                    .setParameter("horaInicio", inicio)
                    .setParameter("horaFin", fin)
                    .uniqueResult();

            if (horario == null) {
                horario = new Horario();
                horario.setTutor(session.getReference(Usuario.class, tutorId));
                horario.setFecha(fecha);
                horario.setHoraInicio(inicio);
                horario.setHoraFin(fin);
                horario.setMateria(session.getReference(MateriaCatalogo.class, codigoMateria));
                horario.setDisponible(true);
                session.persist(horario);
            }

            SolicitudTutoria solicitud = new SolicitudTutoria();
            solicitud.setEstudiante(session.getReference(Usuario.class, estudianteId));
            solicitud.setHorario(horario);
            solicitud.setMateria(session.getReference(MateriaCatalogo.class, codigoMateria));
            solicitud.setComentario(comentario);
            solicitud.setEstado(EstadoSolicitud.PENDIENTE);
            session.persist(solicitud);
            session.flush();

            Long solicitudId = solicitud.getId();
            Long horarioId = horario.getId();
            transaction.commit();
            return new SolicitudCreada(solicitudId, horarioId);
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public Long crearSolicitud(Long estudianteId, Long horarioId, String codigoMateria, String comentario) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            if (session.get(Usuario.class, estudianteId) == null) {
                throw new IllegalArgumentException("Estudiante no encontrado.");
            }
            if (session.get(Horario.class, horarioId) == null) {
                throw new IllegalArgumentException("Horario no encontrado.");
            }
            if (session.get(MateriaCatalogo.class, codigoMateria) == null) {
                throw new IllegalArgumentException("Materia no encontrada.");
            }

            SolicitudTutoria solicitud = new SolicitudTutoria();
            solicitud.setEstudiante(session.getReference(Usuario.class, estudianteId));
            solicitud.setHorario(session.getReference(Horario.class, horarioId));
            solicitud.setMateria(session.getReference(MateriaCatalogo.class, codigoMateria));
            solicitud.setComentario(comentario);
            solicitud.setEstado(EstadoSolicitud.PENDIENTE);

            session.persist(solicitud);
            transaction.commit();
            return solicitud.getId();
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
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
