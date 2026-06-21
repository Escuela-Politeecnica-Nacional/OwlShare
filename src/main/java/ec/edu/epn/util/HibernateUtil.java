package ec.edu.epn.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class HibernateUtil {

    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtil() {
    }

    private static SessionFactory buildSessionFactory() {
        Configuration configuration = new Configuration().configure();

        String dbUrl = env("DB_URL", "jdbc:postgresql://localhost:5432/owlshare");
        String dbUser = env("DB_USER", "postgres");
        String dbPassword = env("DB_PASSWORD", "postgres");

        configuration.setProperty("hibernate.connection.url", dbUrl);
        configuration.setProperty("hibernate.connection.username", dbUser);
        configuration.setProperty("hibernate.connection.password", dbPassword);

        return configuration.buildSessionFactory();
    }

    private static String env(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }
}
