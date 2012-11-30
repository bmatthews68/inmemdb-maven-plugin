package com.btmatthews.maven.plugins.inmemdb.test;

import static org.mockito.MockitoAnnotations.initMocks;

import java.io.File;

import com.btmatthews.maven.plugins.inmemdb.Database;
import com.btmatthews.maven.plugins.inmemdb.mojo.DataSet;
import com.btmatthews.maven.plugins.inmemdb.mojo.Script;
import com.btmatthews.utils.monitor.Logger;
import com.btmatthews.utils.monitor.Server;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Created with IntelliJ IDEA.
 * User: Brian
 * Date: 29/11/12
 * Time: 19:53
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractTestDatabase {

    @Mock
    private Logger logger;

    private Server database;

    protected abstract Server createDatabaseServer();

    @Before
    public void setUp() {
        initMocks(this);
        database = createDatabaseServer();
        database.configure("database", "test", logger);
        database.configure("username", "sa", logger);
        database.configure("password", "", logger);
        database.start(logger);
    }

    @After
    public void tearDown() {
        database.stop(logger);
    }

    @Test
    public void testStartStop() {
    }

    /**
     * Verify that a valid DDL/DML script can be loaded.
     */
    @Test
    public void testLoadScript() {
        final Script source = new Script();
        source.setSourceFile("src/test/resources/create_database.sql");
        ((Database)database).load(logger, source);
    }

    /**
     * Verify than an exception is thrown when the script does not exist.
     */
    @Test
    public void testLoadNonExistantScript() {
        final Script source = new Script();
        source.setSourceFile("src/test/resources/create_database1.sql");
        ((Database)database).load(logger, source);
    }

    /**
     * Verify than an exception is thrown when the script is invalid.
     */
    @Test
    public void testLoadInvalidScript() throws MojoFailureException {
        final Script source = new Script();
        source.setSourceFile("src/test/resources/create_database2.sql");
        ((Database)database).load(logger, source);
    }

    /**
     * Verify than an exception is thrown when {@code null} is passed as
     * the source file.
     */
    @Test
    public void testLoadNull() throws MojoFailureException {
        ((Database)database).load(logger, null);
    }

    /**
     * Verify than an exception is thrown when the source file is actually a
     * directory.
     */
    @Test
    public void testLoadDirectory() throws MojoFailureException {
        final Script source = new Script();
        source.setSourceFile("src/test/resources");
        ((Database)database).load(logger, source);
    }

    /**
     * Verify that a valid DBUnit XML data set can be loaded.
     */
    @Test
    public void testLoadDBUnitXML() {
        final Script script = new Script();
        script.setSourceFile("src/test/resources/create_database.sql");
        ((Database)database).load(logger, script);
        final DataSet source = new DataSet();
        source.setSourceFile("src/test/resources/users.dbunit.xml");
        ((Database)database).load(logger, source);
    }

    /**
     * Verify that a valid DBUinit Flat XML data set can be loaded.
     */
    @Test
    public void testLoadDBUnitFlatXML() {
        final Script script = new Script();
        script.setSourceFile("src/test/resources/create_database.sql");
        ((Database)database).load(logger, script);
        final DataSet source = new DataSet();
        source.setSourceFile("src/test/resources/users.xml");
        ((Database)database).load(logger, source);
    }

    /**
     * Verify that a valid DBUinit CSV data set can be loaded.
     */
    @Test
    public void testLoadDBUnitCSV() {
        final Script script = new Script();
        script.setSourceFile("src/test/resources/create_database.sql");
        ((Database)database).load(logger, script);
        final DataSet source = new DataSet();
        source.setSourceFile("src/test/resources/users.csv");
        ((Database)database).load(logger, source);
    }

    /**
     * Verify that a valid DBUinit XLS data set can be loaded.
     */
    @Test
    public void testLoadDBUnitXLS() {
        final Script script = new Script();
        script.setSourceFile("src/test/resources/create_database.sql");
        ((Database)database).load(logger, script);
        final DataSet source = new DataSet();
        source.setSourceFile("src/test/resources/users.xls");
        ((Database)database).load(logger, source);
    }

}
