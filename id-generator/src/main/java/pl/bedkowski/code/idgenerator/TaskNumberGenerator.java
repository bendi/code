package pl.bedkowski.code.idgenerator;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.MappingException;
import org.hibernate.cfg.ObjectNameNormalizer;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGeneratorHelper;
import org.hibernate.id.IntegralDataTypeHolder;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.jdbc.AbstractReturningWork;
import org.hibernate.jdbc.WorkExecutorVisitable;
import org.hibernate.mapping.Table;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

import pl.bedkowski.code.idgenerator.TaskNumber.Id;

public class TaskNumberGenerator implements PersistentIdentifierGenerator, Configurable {

    private static final CoreMessageLogger LOG                           = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                                 TaskNumberGenerator.class.getName());

    public static final String             DEFAULT_TABLE_NAME            = "task_number_seq";
    public static final String             DEFAULT_UNIQUE_FK_COLUMN_NAME = "project_id";
    public static final String             DEFAULT_SEQUENCE_COLUMN_NAME  = "task_number";

    public static final String             UNIQUE_FK_COLUMN              = "unique_fk_column";
    public static final String             SEQUENCE_COLUMN               = "target_column";

    private String                         query;
    private String                         insert;
    private String                         update;
    private String                         tableName;

    private String                         uniqueFkColumnName;
    private String                         sequenceColumnName;

    private static final int               JDBC_POSITION_1               = 1;
    private static final int               JDBC_POSITION_2               = 2;
    private static final long              DEFAULT_NUMBER_VALUE          = 1;

    @Override
    public synchronized Serializable generate(final SessionImplementor session, final Object obj) {
        final TaskNumber tn = (TaskNumber) obj;

        final WorkExecutorVisitable<IntegralDataTypeHolder> work = new AbstractReturningWork<IntegralDataTypeHolder>() {
            @Override
            public IntegralDataTypeHolder execute(Connection connection) throws SQLException {
                IntegralDataTypeHolder value = IdentifierGeneratorHelper.getIntegralDataTypeHolder(Long.class);
                SqlStatementLogger statementLogger = session.getFactory().getServiceRegistry().getService(JdbcServices.class)
                        .getSqlStatementLogger();
                int rows;
                long projectId = tn.getId().getProject().getId();
                do {
                    statementLogger.logStatement(query, FormatStyle.BASIC.getFormatter());
                    PreparedStatement qps = connection.prepareStatement(query);
                    qps.setLong(JDBC_POSITION_1, projectId);
                    PreparedStatement ips = null;
                    ResultSet rs = null;
                    try {
                        rs = qps.executeQuery();
                        boolean isInitialized = rs.next();
                        if (!isInitialized) {
                            value.initialize(DEFAULT_NUMBER_VALUE);
                            statementLogger.logStatement(insert, FormatStyle.BASIC.getFormatter());
                            ips = connection.prepareStatement(insert);
                            value.bind(ips, JDBC_POSITION_1);
                            ips.setLong(JDBC_POSITION_2, projectId);

                            ips.execute();
                        } else {
                            value.initialize(rs, DEFAULT_NUMBER_VALUE);
                        }
                    } catch (SQLException sqle) {
                        LOG.unableToReadOrInitHiValue(sqle);
                        throw sqle;
                    } finally {
                        if (rs != null) {
                            rs.close();
                        }
                        if (ips != null) {
                            ips.close();
                        }
                        qps.close();
                    }

                    statementLogger.logStatement(update, FormatStyle.BASIC.getFormatter());
                    PreparedStatement ups = connection.prepareStatement(update);
                    try {
                        value.copy().increment().bind(ups, JDBC_POSITION_1);
                        ups.setLong(JDBC_POSITION_2, projectId);

                        rows = ups.executeUpdate();
                    } catch (SQLException sqle) {
                        LOG.error(LOG.unableToUpdateHiValue(tableName), sqle);
                        throw sqle;
                    } finally {
                        ups.close();
                    }
                } while (rows == 0);

                return value;
            }

        };

        Id id = tn.getId();
        IntegralDataTypeHolder value = null;
        value = session.getTransactionCoordinator().getTransaction().createIsolationDelegate().delegateWork(work, true);
        id.setTaskNum((Long) value.makeValue());
        return id;
    }

    @Override
    public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
        ObjectNameNormalizer normalizer = (ObjectNameNormalizer) params.get(IDENTIFIER_NORMALIZER);

        tableName = ConfigurationHelper.getString(TABLE, params, DEFAULT_TABLE_NAME) + "_seq";

        if (tableName.indexOf('.') < 0) {
            final String schemaName = normalizer.normalizeIdentifierQuoting(params.getProperty(SCHEMA));
            final String catalogName = normalizer.normalizeIdentifierQuoting(params.getProperty(CATALOG));
            tableName = Table.qualify(dialect.quote(catalogName), dialect.quote(schemaName), dialect.quote(tableName));
        } else {
            // if already qualified there is not much we can do in a portable manner so we pass it
            // through and assume the user has set up the name correctly.
        }

        uniqueFkColumnName = dialect.quote(normalizer.normalizeIdentifierQuoting(ConfigurationHelper.getString(UNIQUE_FK_COLUMN, params,
                DEFAULT_UNIQUE_FK_COLUMN_NAME)));

        sequenceColumnName = dialect.quote(normalizer.normalizeIdentifierQuoting(ConfigurationHelper.getString(SEQUENCE_COLUMN, params,
                DEFAULT_SEQUENCE_COLUMN_NAME)));

        query = "select " + sequenceColumnName + " from " + dialect.appendLockHint(new LockOptions(LockMode.PESSIMISTIC_WRITE), tableName)
                + " where " + uniqueFkColumnName + " = ? " + dialect.getForUpdateString();

        update = "update " + tableName + " set " + sequenceColumnName + " = ? where " + uniqueFkColumnName + " = ? ";

        insert = "insert into " + tableName + "(" + sequenceColumnName + ", " + uniqueFkColumnName + ") " + "values(?, ?)";
    }

    @Override
    public String[] sqlCreateStrings(Dialect dialect) throws HibernateException {
        return new String[] { new StringBuilder(dialect.getCreateTableString()).append(' ').append(tableName).append(" ( ")
                .append(uniqueFkColumnName).append(' ').append(dialect.getTypeName(Types.BIGINT)).append(" , ").append(sequenceColumnName)
                .append(" ").append(dialect.getTypeName(Types.BIGINT)).append(")").toString() };
    }

    @Override
    public String[] sqlDropStrings(Dialect dialect) throws HibernateException {
        return new String[] { dialect.getDropTableString(tableName) };
    }

    @Override
    public Object generatorKey() {
        return tableName;
    }
}
