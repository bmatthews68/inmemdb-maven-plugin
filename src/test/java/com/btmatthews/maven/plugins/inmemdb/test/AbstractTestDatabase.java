/*
 * Copyright 2011-2012 Brian Matthews
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.btmatthews.maven.plugins.inmemdb.test;

import static org.mockito.MockitoAnnotations.initMocks;

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

    /**
     * Mock for the logger.
     */
    @Mock
    private Logger logger;

    /**
     * The main test fixture.
     */
    private Server database;

    /**
     * Concrete class should override this method to create the main test fixture.
     *
     * @return The main test fixture.
     */
    protected abstract Server createDatabaseServer();

    /**
     * Prepare for test case execute by creating, configuring and starting the main test fixture.
     */
    @Before
    public void setUp() {
        initMocks(this);
        database = createDatabaseServer();
        database.configure("database", "test", logger);
        database.configure("username", "sa", logger);
        database.configure("password", "", logger);
        database.start(logger);
    }

    /**
     * Clean up after test case execution by stopping the main test fixture.
     */
    @After
    public void tearDown() {
        database.stop(logger);
    }

    /**
     * Verify that the database server starts and stops cleanly.
     */
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
