import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.XmlDataSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public final class ITestVerifyH2Database {

    private Connection jdbcConnection;

    private IDatabaseConnection connection;

    @Before
    public void setUp() throws Exception {
        Thread.sleep(1000L);
        Class.forName("org.h2.Driver");
        jdbcConnection = DriverManager.getConnection("jdbc:h2:tcp://localhost/mem:test;user=sa;password=");
        connection = new DatabaseConnection(jdbcConnection);
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
        jdbcConnection.close();
    }

    @Test
    public void testLoadedData() throws Exception {
        final IDataSet databaseDataSet = connection.createDataSet();
        final ITable actualTable = databaseDataSet.getTable("h2_users");

        final InputStream inputStream = getClass().getResourceAsStream("h2_users.dbunit.xml");
        final IDataSet expectedDataSet = new XmlDataSet(inputStream);
        final ITable expectedTable = expectedDataSet.getTable("h2_users");

        Assertion.assertEquals(expectedTable, actualTable);
    }
}