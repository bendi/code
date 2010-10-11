package pl.bedkowski.code.filterdef.rule;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import pl.bedkowski.code.filterdef.MockObject;
import pl.bedkowski.code.filterdef.MockSubObject;

public class InitHibernateRule implements MethodRule {

    private static final SessionFactory sessionFactory;

    static
    {
        try
        {
            AnnotationConfiguration config = new AnnotationConfiguration();
            config.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
            config.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
            config.setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:demodb");
            config.setProperty("hibernate.connection.username", "sa");
            config.setProperty("hibernate.connection.password", "");
            config.setProperty("hibernate.connection.pool_size", "1");
            config.setProperty("hibernate.connection.autocommit", "true");
            config.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");
            config.setProperty("hibernate.hbm2ddl.auto", "create-drop");
            config.setProperty("hibernate.show_sql", "true");
            config.setProperty("hibernate.transaction.factory_class", "org.hibernate.transaction.JDBCTransactionFactory");
            config.setProperty("hibernate.current_session_context_class", "thread");
 
            // Add your mapped classes here:
            config.addAnnotatedClass(MockObject.class);
            config.addAnnotatedClass(MockSubObject.class);
 
            sessionFactory = config.buildSessionFactory();
        }
        catch (Throwable ex)
        {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public Statement apply(final Statement stmt, final FrameworkMethod method, final Object obj) {
		return stmt;
	}

}
