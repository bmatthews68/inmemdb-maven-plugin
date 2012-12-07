import java.io.FileInputStream;
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

public final class ITestVerifyH2Database {

    private Connection jdbcConnection;

    private IDatabaseConnection connection;

    @Before
    public void setUp() throws Exception {
        Class.forName("org.h2.Driver");
        jdbcConnection = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test;user=sa;password=");
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
        System.err.println("Table names are:");
        for (String tableName : databaseDataSet.getTableNames()) {
            System.err.println("\t" + tableName);
        }
        ITable actualTable = databaseDataSet.getTable("users");

        final InputStream inputStream = getClass().getResourceAsStream("users.dbunit.xml");
        IDataSet expectedDataSet =  new XmlDataSet(inputStream);
        ITable expectedTable = expectedDataSet.getTable("users");

        Assertion.assertEquals(expectedTable, actualTable);

    }
}