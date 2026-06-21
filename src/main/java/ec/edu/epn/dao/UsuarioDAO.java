package ec.edu.epn.dao;

import ec.edu.epn.modelo.Usuario;
import ec.edu.epn.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class UsuarioDAO {

    public boolean existePorEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                    "select count(u.id) from Usuario u where lower(u.email) = lower(:email)",
                    Long.class
            );
            query.setParameter("email", email.trim());
            return query.uniqueResult() > 0;
        }
    }

    public void guardar(Usuario usuario) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(usuario);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
