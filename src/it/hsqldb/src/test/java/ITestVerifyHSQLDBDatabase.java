import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.XmlDataSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public final class ITestVerifyHSQLDBDatabase {

    private Connection jdbcConnection;

    private IDatabaseConnection connection;

    @Before
    public void setUp() throws Exception {
        Thread.sleep(1000L);
        Class.forName("org.hsqldb.jdbcDriver");
        jdbcConnection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/test;user=sa");
        connection = new DatabaseConnection(jdbcConnection);
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
        jdbcConnection.close();
    }

    @Test
    public void testLoadedData() throws Exception {
        IDataSet databaseDataSet = connection.createDataSet();
        ITable actualTable = databaseDataSet.getTable("hsqldb_users");

        final InputStream inputStream = getClass().getResourceAsStream("hsqldb_users.dbunit.xml");
        IDataSet expectedDataSet = new XmlDataSet(inputStream);
        ITable expectedTable = expectedDataSet.getTable("hsqldb_users");

        Assertion.assertEquals(expectedTable, actualTable);

    }
}