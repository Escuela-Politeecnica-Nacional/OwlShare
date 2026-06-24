package ec.edu.epn.dao;

import ec.edu.epn.modelo.Materia;
import ec.edu.epn.modelo.MateriaCatalogo;
import ec.edu.epn.util.CatalogoRegistro;
import ec.edu.epn.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Optional;

public class MateriaCatalogoDAO {

    public Optional<MateriaCatalogo> buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return Optional.empty();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(MateriaCatalogo.class, codigo.trim()));
        }
    }

    public MateriaCatalogo obtenerOCrear(String codigo) {
        String codigoNormalizado = codigo.trim();
        Optional<MateriaCatalogo> existente = buscarPorCodigo(codigoNormalizado);
        if (existente.isPresent()) {
            return existente.get();
        }

        Materia materiaCatalogo = CatalogoRegistro.buscarMateriaPorCodigo(codigoNormalizado);
        if (materiaCatalogo == null) {
            throw new IllegalArgumentException("La materia no existe en el catálogo.");
        }

        MateriaCatalogo materia = new MateriaCatalogo(
                materiaCatalogo.getCodigo(),
                materiaCatalogo.getNombre(),
                materiaCatalogo.getSemestre()
        );

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(materia);
            transaction.commit();
            return materia;
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            Optional<MateriaCatalogo> recreada = buscarPorCodigo(codigoNormalizado);
            if (recreada.isPresent()) {
                return recreada.get();
            }
            throw e;
        }
    }
}
