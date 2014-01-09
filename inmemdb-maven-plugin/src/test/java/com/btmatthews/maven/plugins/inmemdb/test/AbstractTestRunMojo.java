/*
 * Copyright 2012 Brian Thomas Matthews
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

import static org.junit.Assert.assertNotNull;
import static org.mockito.MockitoAnnotations.initMocks;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.btmatthews.maven.plugins.inmemdb.Source;
import com.btmatthews.utils.monitor.Logger;
import com.btmatthews.utils.monitor.Monitor;
import org.apache.maven.plugin.Mojo;
import org.codehaus.plexus.util.ReflectionUtils;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;

/**
 * Unit tests for the Mojo that implements the run goal.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 */
public abstract class AbstractTestRunMojo {

    /**
     * Mock the logger.
     */
    @Mock
    private Logger logger;

    /**
     * The test being tested.
     */
    private Mojo mojo;

    /**
     * Used to create temporary directories used by the unit tests.
     */
    @Rule
    public TemporaryFolder outputDirectory = new TemporaryFolder();

    /**
     * Concrete implementations should override this method to get the port number that the monitor controlling the
     * server listens for commands on.
     *
     * @return The port number.
     */
    protected abstract int getMonitorPort();

    /**
     * Concrete implementations should override this method to get the database server type.
     *
     * @return The database server type.
     */
    protected abstract String getType();

    /**
     * Concrete implementations should override this method to get the driver class name.
     *
     * @return The driver class name.
     */
    protected abstract String getDriverClassName();

    /**
     * Concrete implementations should override this method to get the database connection string.
     *
     * @return The database connection string.
     */
    protected abstract String getConnectionString();

    /**
     * Get the name of the database table used for testing.
     *
     * @return The name of the database table.
     */
    private String getTableName() {
        return getType() + "_users";
    }

    /**
     * Get the relative path of the database script used to create the database schema.
     *
     * @return The relative path of the script.
     */
    private String getCreateScript() {
        return "classpath:create_" + getType() + "_database.sql";
    }

    /**
     * Prepare for test execution by initialising the mock objects and test fixture.
     *
     * @throws Exception If there was an error configuring the test fixture.
     */
    @Before
    public void setUp() throws Exception {
        initMocks(this);
        Class.forName(getDriverClassName());
        mojo = new RunMojo();
        ReflectionUtils.setVariableValueInObject(mojo, "monitorPort", getMonitorPort());
        ReflectionUtils.setVariableValueInObject(mojo, "monitorKey", "inmemdb");
        ReflectionUtils.setVariableValueInObject(mojo, "type", getType());
        ReflectionUtils.setVariableValueInObject(mojo, "database", "test");
        final Script source = new Script();
        source.setSourceFile(getCreateScript());
        final List<Source> sources = new ArrayList<Source>();
        sources.add(source);
        ReflectionUtils.setVariableValueInObject(mojo, "sources", sources);
    }

    /**
     * Verify that we can start the server.
     *
     * @throws Exception If there was an error.
     */
    @Test
    public void testRun() throws Exception {
        ReflectionUtils.setVariableValueInObject(mojo, "daemon", Boolean.FALSE);

        final Thread mojoThread = new Thread(new Runnable() {
            public void run() {
                try {
                    mojo.execute();
                } catch (final Exception e) {
                }
            }
        });
        mojoThread.start();
        try {
            Thread.sleep(5000);

            final Connection jdbcConnection = DriverManager.getConnection(getConnectionString());
            final IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
            IDataSet databaseDataSet = connection.createDataSet();
            assertNotNull(databaseDataSet.getTable(getTableName()));

            connection.close();
            jdbcConnection.close();
        } finally {
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    signalStop();
                }
            }, 5000L);
            mojoThread.join(15000L);
        }
    }

    /**
     * Verify that we can start the server as a daemon.
     *
     * @throws Exception If there was an error.
     */
    @Test
    public void testRunDaemon() throws Exception {
        ReflectionUtils.setVariableValueInObject(mojo, "daemon", Boolean.TRUE);
        try {
            mojo.execute();
            Thread.sleep(5000L);
            final Connection jdbcConnection = DriverManager.getConnection(getConnectionString());
            final IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
            IDataSet databaseDataSet = connection.createDataSet();
            assertNotNull(databaseDataSet.getTable(getTableName()));

            connection.close();
            jdbcConnection.close();
        } finally {
            signalStop();
        }
    }

    /**
     * Send a stop signal to monitor controlling the server.
     */
    private void signalStop() {
        new Monitor("inmemdb", getMonitorPort()).sendCommand("stop", logger);
    }
}
