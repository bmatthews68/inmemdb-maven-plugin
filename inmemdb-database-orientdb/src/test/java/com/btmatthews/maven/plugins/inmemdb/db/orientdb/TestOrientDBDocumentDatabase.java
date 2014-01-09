package com.btmatthews.maven.plugins.inmemdb.db.orientdb;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.btmatthews.maven.plugins.inmemdb.NoSQLDatabase;
import com.btmatthews.maven.plugins.inmemdb.Source;
import com.btmatthews.maven.plugins.inmemdb.db.orientdb.OrientDBDatabase;
import com.btmatthews.utils.monitor.Logger;
import com.btmatthews.utils.monitor.Server;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 2.0.0
 */
public class TestOrientDBDocumentDatabase {

    /**
     * Mock the logger object.
     */
    @Mock
    private Logger logger;

    @Mock
    private Source source;

    /**
     * The Neo4J database server test fixture.
     */
    private Server database;

    /**
     * Create and launch the Neo4J database server before executing the test case.
     */
    @Before
    public void setUp() {
        initMocks(this);
        database = new OrientDBDatabase();
        database.configure("database", "test", logger);
        database.configure("username", "brian", logger);
        database.configure("password", "everclear", logger);
        database.start(logger);
    }

    /**
     * Shutdown the Neo4J database server after executing the test case.
     */
    @After
    public void tearDown() {
        database.stop(logger);
    }

    /**
     * Verify that the Neo4J database is server is running and is accessible via the REST API.
     */
    @Test
    public void testStartStop() {
        final ODatabaseDocumentTx db = new ODatabaseDocumentTx("remote:localhost/test").open("brian", "everclear");
        final ODocument user = new ODocument("User");
        user.field("username", "yaromir");
        user.field("name", "Yaromir Popov Matthews");
        user.field("password", "lego");
        user.save();
        db.close();
    }

    @Test
    public void testLoadUser() {
        when(source.getSourceFile()).thenReturn("classpath:user.json");
        when(source.getQualifiedTableNames()).thenReturn(false);
        ((NoSQLDatabase)database).load(logger, source);
        final ODatabaseDocumentTx db = new ODatabaseDocumentTx("remote:localhost/test").open("brian", "everclear");
        try {
            assertEquals(1, db.countClass("user"));
        } finally {
            db.close();
        }
    }


    @Test
    public void testLoadUsers() {
        when(source.getSourceFile()).thenReturn("classpath:users.json");
        when(source.getQualifiedTableNames()).thenReturn(false);
        ((NoSQLDatabase)database).load(logger, source);
        final ODatabaseDocumentTx db = new ODatabaseDocumentTx("remote:localhost/test").open("brian", "everclear");
        try {
            assertEquals(3, db.countClass("users"));
        } finally {
            db.close();
        }
    }
}
