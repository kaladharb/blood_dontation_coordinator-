package com.bloodbank.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Provides the application's single Hibernate SessionFactory.
 */
public final class HibernateUtil {

    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtil() {
    }

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
            configuration.setProperty("hibernate.connection.url", environmentValue(
                    "DB_URL",
                    "jdbc:mysql://localhost:3306/bloodbank_db?useSSL=false&serverTimezone=UTC"));
            configuration.setProperty("hibernate.connection.username", environmentValue("DB_USER", "root"));
            configuration.setProperty("hibernate.connection.password", environmentValue("DB_PASSWORD", ""));
            return configuration.buildSessionFactory();
        } catch (Throwable exception) {
            System.err.println("Unable to initialize Hibernate SessionFactory.");
            exception.printStackTrace(System.err);
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static String environmentValue(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void shutdown() {
        if (SESSION_FACTORY != null && !SESSION_FACTORY.isClosed()) {
            SESSION_FACTORY.close();
        }
    }
}
