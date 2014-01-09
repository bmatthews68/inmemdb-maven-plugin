package com.btmatthews.maven.plugins.inmemdb.ldr.json;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.btmatthews.maven.plugins.inmemdb.Loader;
import com.btmatthews.maven.plugins.inmemdb.NoSQLDatabase;
import com.btmatthews.maven.plugins.inmemdb.Source;
import com.btmatthews.utils.monitor.Logger;
import com.fasterxml.jackson.databind.JsonNode;
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
    private Source source;

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
         when(source.getSourceFile()).thenReturn("classpath:user.json");
         when(source.getQualifiedTableNames()).thenReturn(false);
         loader.load(logger, database, source);
         verify(database).insertObject(eq("user"), any(JsonNode.class), same(logger));
     }



    @Test
    public void testLoadUsers() {
        when(source.getSourceFile()).thenReturn("classpath:users.json");
        when(source.getQualifiedTableNames()).thenReturn(false);
        loader.load(logger, database, source);
        verify(database, times(3)).insertObject(eq("users"), any(JsonNode.class), same(logger));
    }
}
