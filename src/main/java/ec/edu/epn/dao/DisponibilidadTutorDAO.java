package ec.edu.epn.dao;

import ec.edu.epn.modelo.DiaSemana;
import ec.edu.epn.modelo.DisponibilidadTutor;
import ec.edu.epn.modelo.Usuario;
import ec.edu.epn.util.DisponibilidadUtil;
import ec.edu.epn.util.HibernateUtil;
import ec.edu.epn.util.HorarioUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class DisponibilidadTutorDAO {

    public List<DisponibilidadTutor> listarPorTutor(Long tutorId) {
        if (tutorId == null) {
            return List.of();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<DisponibilidadTutor> franjas = session.createQuery(
                    "from DisponibilidadTutor d where d.tutor.id = :tutorId",
                    DisponibilidadTutor.class
            ).setParameter("tutorId", tutorId).list();
            franjas.sort(DisponibilidadUtil.ordenPorDiaYHora());
            return franjas;
        }
    }

    public Optional<DisponibilidadTutor> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(DisponibilidadTutor.class, id));
        }
    }

    public boolean cubreHorario(Long tutorId, String fecha, String horaInicio, String horaFin) {
        return DisponibilidadUtil.horarioCubierto(fecha, horaInicio, horaFin, listarPorTutor(tutorId));
    }

    public void guardar(Usuario tutor, DiaSemana diaSemana, String horaInicio, String horaFin) {
        if (!HorarioUtil.esRangoValido(horaInicio, horaFin)) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin.");
        }

        List<DisponibilidadTutor> existentes = listarPorTutor(tutor.getId());
        if (DisponibilidadUtil.haySolapamientoConFranjas(diaSemana, horaInicio, horaFin, existentes, null)) {
            throw new IllegalArgumentException("Ya tienes una franja que se solapa en ese día y horario.");
        }

        DisponibilidadTutor franja = new DisponibilidadTutor();
        franja.setTutor(tutor);
        franja.setDiaSemana(diaSemana);
        franja.setHoraInicio(horaInicio);
        franja.setHoraFin(horaFin);
        franja.setActivo(true);

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(franja);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public boolean eliminarSiEsPropietario(Long franjaId, Long tutorId) {
        if (franjaId == null || tutorId == null) {
            return false;
        }
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            DisponibilidadTutor franja = session.get(DisponibilidadTutor.class, franjaId);
            if (franja == null || !franja.getTutor().getId().equals(tutorId)) {
                transaction.rollback();
                return false;
            }
            session.remove(franja);
            transaction.commit();
            return true;
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public List<DiaSemana> diasSemana() {
        return new ArrayList<>(List.of(DiaSemana.values())).stream()
                .sorted(Comparator.comparingInt(DiaSemana::getOrden))
                .toList();
    }
}
