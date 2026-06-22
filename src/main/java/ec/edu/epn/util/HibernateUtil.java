package ec.edu.epn.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class HibernateUtil {

    private static volatile SessionFactory sessionFactory;

    private HibernateUtil() {
    }

    public static SessionFactory getSessionFactory() {
        SessionFactory factory = sessionFactory;
        if (factory == null) {
            synchronized (HibernateUtil.class) {
                factory = sessionFactory;
                if (factory == null) {
                    sessionFactory = factory = buildSessionFactory();
                }
            }
        }
        return factory;
    }

    private static SessionFactory buildSessionFactory() {
        DbConfig config = loadDbConfig();

        Configuration configuration = new Configuration().configure();
        configuration.setProperty("hibernate.connection.url", config.url());
        configuration.setProperty("hibernate.connection.driver_class", config.driverClass());
        configuration.setProperty("hibernate.dialect", config.dialect());
        configuration.setProperty("hibernate.hbm2ddl.auto", "update");
        configuration.setProperty("hibernate.show_sql", "false");

        if (config.username() != null) {
            configuration.setProperty("hibernate.connection.username", config.username());
        }
        if (config.password() != null) {
            configuration.setProperty("hibernate.connection.password", config.password());
        }

        return configuration.buildSessionFactory();
    }

    private static DbConfig loadDbConfig() {
        Properties properties = new Properties();
        loadPropertiesFile(properties, "database.local.properties");
        loadPropertiesFile(properties, Path.of("database.local.properties"));

        String url = firstNonBlank(
                System.getenv("DB_URL"),
                properties.getProperty("db.url"),
                "jdbc:sqlite:owlshare.db"
        );

        if (url.startsWith("jdbc:postgresql:")) {
            return new DbConfig(
                    url,
                    "org.postgresql.Driver",
                    "org.hibernate.dialect.PostgreSQLDialect",
                    firstNonBlank(System.getenv("DB_USER"), properties.getProperty("db.user"), "postgres"),
                    firstNonBlank(System.getenv("DB_PASSWORD"), properties.getProperty("db.password"), "postgres")
            );
        }

        return new DbConfig(
                url,
                "org.sqlite.JDBC",
                "org.hibernate.community.dialect.SQLiteDialect",
                null,
                null
        );
    }

    private static void loadPropertiesFile(Properties target, String classpathResource) {
        try (InputStream input = HibernateUtil.class.getClassLoader().getResourceAsStream(classpathResource)) {
            if (input != null) {
                target.load(input);
            }
        } catch (IOException ignored) {
        }
    }

    private static void loadPropertiesFile(Properties target, Path path) {
        if (!Files.isRegularFile(path)) {
            return;
        }
        try (InputStream input = Files.newInputStream(path)) {
            target.load(input);
        } catch (IOException ignored) {
        }
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }

    private record DbConfig(String url, String driverClass, String dialect, String username, String password) {
    }
}
