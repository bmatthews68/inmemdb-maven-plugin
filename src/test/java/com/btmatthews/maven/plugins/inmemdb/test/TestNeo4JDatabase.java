package com.btmatthews.maven.plugins.inmemdb.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

import com.btmatthews.maven.plugins.inmemdb.db.nosql.neo4j.Neo4JDatabase;
import com.btmatthews.utils.monitor.Logger;
import com.btmatthews.utils.monitor.Server;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 2.0.0
 */
public class TestNeo4JDatabase {

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
        database = new Neo4JDatabase();
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
        final WebResource resource = Client.create().resource(SERVER_ROOT_URI);
        final ClientResponse response = resource.get(ClientResponse.class);
        assertEquals(ClientResponse.Status.OK, response.getStatus());
        response.close();
    }
}
