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

package com.btmatthews.maven.plugins.inmemdb.db.hsqldb;

import com.btmatthews.maven.plugins.inmemdb.Loader;
import com.btmatthews.maven.plugins.inmemdb.MessageUtil;
import com.btmatthews.maven.plugins.inmemdb.db.AbstractSQLDatabase;
import com.btmatthews.maven.plugins.inmemdb.ldr.dbunit.DBUnitCSVLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.dbunit.DBUnitFlatXMLLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.dbunit.DBUnitXLSLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.dbunit.DBUnitXMLLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.sqltool.SQLLoader;
import com.btmatthews.utils.monitor.Logger;
import org.hsqldb.DatabaseURL;
import org.hsqldb.jdbc.JDBCDataSource;
import org.hsqldb.server.Server;
import org.hsqldb.server.ServerConstants;

import javax.sql.DataSource;
import java.text.MessageFormat;
import java.util.Map;

/**
 * Implements support for in-memory HSQLDB databases.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public final class HSQLDBDatabase extends AbstractSQLDatabase {

    /**
     * The connection protocol for in-memory HSQLDB databases.
     */
    private static final String PROTOCOL = "hsqldb:hsql://localhost:{0,number,#}/";
    /**
     * Default port HSQLDB listens on, can be altered via setting port property
     */
    private static final int DEFAULT_PORT = ServerConstants.SC_DEFAULT_HSQL_SERVER_PORT;
    private static final int PING_RETRIES = 10;
    private static final int PING_DELAY = 100;
    /**
     * The loaders that are supported for loading data or executing scripts.
     */
    private static final Loader[] LOADERS = new Loader[]{
            new DBUnitXMLLoader(), new DBUnitFlatXMLLoader(),
            new DBUnitCSVLoader(), new DBUnitXLSLoader(), new SQLLoader()};
    /**
     * The HSQLDB server.
     */
    private Server server;

    /**
     * The default constructor initializes the default database port.
     */
    public HSQLDBDatabase() {
        super(DEFAULT_PORT);
    }

    /**
     * Get the database connection protocol.
     *
     * @return Always returns {@link HSQLDBDatabase#PROTOCOL}.
     */
    @Override
    protected String getUrlProtocol() {
        return MessageFormat.format(PROTOCOL, getPort());
    }

    /**
     * Get the data source that describes the connection to the in-memory HSQLDB
     * database.
     *
     * @param attributes Additional attributes for the data source connection string.
     * @return Returns {@code dataSource} which was initialised by the
     *         constructor.
     */
    @Override
    public DataSource getDataSource(final Map<String, String> attributes) {
        final JDBCDataSource dataSource = new JDBCDataSource();
        dataSource.setUrl(getUrl(attributes));
        dataSource.setUser(getUsername());
        dataSource.setPassword(getPassword());
        return dataSource;
    }

    /**
     * Get the loaders that are supported for loading data or executing scripts.
     *
     * @return Returns {@link #LOADERS}.
     */
    @Override
    public Loader[] getLoaders() {
        return LOADERS;
    }

    /**
     * Start the in-memory HSQLDB server.
     *
     * @param logger Used to report errors and raise exceptions.
     */
    @Override
    public void start(final Logger logger) {

        logger.logInfo("Starting embedded HSQLDB database");

        server = new Server();
        server.setDatabasePath(0, DatabaseURL.S_MEM + getDatabaseName());
        server.setDatabaseName(0, getDatabaseName());
        server.setDaemon(true);
        server.setAddress(ServerConstants.SC_DEFAULT_ADDRESS);
        server.setPort(getPort());
        server.setErrWriter(null);
        server.setLogWriter(null);
        server.setTls(false);
        server.setTrace(false);
        server.setSilent(true);
        server.setNoSystemExit(true);
        server.setRestartOnShutdown(false);
        server.start();
        if (!waitForStart()) {
            final String message = MessageUtil.getMessage(ERROR_STARTING_SERVER, getDatabaseName());
            logger.logError(message);
            return;
        }

        logger.logInfo("Started embedded HSQLDB database");
    }

    /**
     * Shutdown the in-memory HSQLDB database by sending it a SHUTDOWN command.
     *
     * @param logger Used to report errors and raise exceptions.
     */
    @Override
    public void stop(final Logger logger) {

        logger.logInfo("Stopping embedded HSQLDB database");

        if (server != null) {
            server.stop();
            if (!waitForStop()) {
                final String message = MessageUtil.getMessage(ERROR_STOPPING_SERVER, getDatabaseName());
                logger.logError(message);
                return;
            }
            server.shutdown();
            server = null;
        }

        logger.logInfo("Stopping embedded HSQLDB database");
    }

    protected boolean hasStarted() {
        try {
            server.checkRunning(true);
            return true;
        } catch (final RuntimeException e) {
            return false;
        }
    }

    protected boolean hasStopped() {
        try {
            server.checkRunning(false);
            return true;
        } catch (final RuntimeException e) {
            return false;
        }
    }
}
