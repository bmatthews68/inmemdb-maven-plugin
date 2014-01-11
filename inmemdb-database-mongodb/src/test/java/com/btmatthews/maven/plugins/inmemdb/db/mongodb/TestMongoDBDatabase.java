package com.btmatthews.maven.plugins.inmemdb.db.mongodb;

import com.btmatthews.utils.monitor.Logger;
import com.btmatthews.utils.monitor.Server;
import com.mongodb.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 2.0.0
 */
public class TestMongoDBDatabase {

    /**
     * The URL for the root node in the database.
     */
    private static final String SERVER_ROOT_URI = "http://localhost:7474/db/data";

    /**
     * Mock the logger object.
     */
    @Mock
    private Logger logger;

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
        database = new MongoDBDatabase();
        database.configure("database", "security", logger);
        database.start(logger);
    }

    /**
     * Shutdown the Neo4J database server after executing the test case.
     */
    @After
    public void tearDown() {
        database.stop(logger);
    }

    @Test
    public void insertDocument() throws Exception {
        final MongoClient client = new MongoClient("localhost", 27017);
        final DB db = client.getDB("security");
        final DBCollection users = db.getCollection("users");
        final DBObject user = new BasicDBObjectBuilder()
                .add("username", "bmatthews68")
                .add("email", "brian@btmatthews.com")
                .get();
        users.insert(user);
    }
}
