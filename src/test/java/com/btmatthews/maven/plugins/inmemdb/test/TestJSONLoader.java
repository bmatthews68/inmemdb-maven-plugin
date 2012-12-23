package com.btmatthews.maven.plugins.inmemdb.test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import com.btmatthews.maven.plugins.inmemdb.Loader;
import com.btmatthews.maven.plugins.inmemdb.NoSQLDatabase;
import com.btmatthews.maven.plugins.inmemdb.Source;
import com.btmatthews.maven.plugins.inmemdb.ldr.json.JSONLoader;
import com.btmatthews.maven.plugins.inmemdb.mojo.DataSet;
import com.btmatthews.utils.monitor.Logger;
import org.codehaus.jackson.JsonNode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Created with IntelliJ IDEA.
 * User: Brian
 * Date: 23/12/12
 * Time: 07:10
 * To change this template use File | Settings | File Templates.
 */
public class TestJSONLoader {

    @Mock
    private Logger logger;

    @Mock
    private NoSQLDatabase database;

    private Loader loader;

    @Before
    public void setUp() {
        initMocks(this);
        loader = new JSONLoader();
    }

     @Test
     public void testLoadUser() {
         final Source source = new DataSet("classpath:user.json", false);
         loader.load(logger, database, source);
         verify(database).insertObject(eq("user"), any(JsonNode.class), same(logger));
     }



    @Test
    public void testLoadUsers() {
        final Source source = new DataSet("classpath:users.json", false);
        loader.load(logger, database, source);
        verify(database, times(3)).insertObject(eq("users"), any(JsonNode.class), same(logger));
    }
}
