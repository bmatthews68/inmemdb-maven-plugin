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

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import com.btmatthews.maven.plugins.inmemdb.Loader;
import com.btmatthews.maven.plugins.inmemdb.MessageUtil;
import com.btmatthews.maven.plugins.inmemdb.db.AbstractDatabase;
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

/**
 * Implements support for in-memory HSQLDB databases.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public final class HSQLDBDatabase extends AbstractDatabase {

    /**
     * The connection protocol for in-memory HSQLDB databases.
     */
    private static final String PROTOCOL = "hsqldb:hsql//localhost/";

    /**
     * The loaders that are supported for loading data or executing scripts.
     */
    private static final Loader[] LOADERS = new Loader[]{
            new DBUnitXMLLoader(), new DBUnitFlatXMLLoader(),
            new DBUnitCSVLoader(), new DBUnitXLSLoader(), new SQLLoader() };

    /**
     * The HSQLDB server.
     */
    private Server server;

    /**
     * Get the database connection protocol.
     *
     * @return Always returns {@link HSQLDBDatabase#PROTOCOL}.
     */
    @Override
    protected String getUrlProtocol() {
        return PROTOCOL;
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

        server = new Server();
        server.setDatabasePath(0, DatabaseURL.S_MEM + getDatabaseName());
        server.setDatabaseName(0, getDatabaseName());
        server.setDaemon(true);
        server.setAddress(ServerConstants.SC_DEFAULT_ADDRESS);
        server.setPort(ServerConstants.SC_DEFAULT_HSQL_SERVER_PORT);
        server.setErrWriter(null);
        server.setLogWriter(null);
        server.setTls(false);
        server.setTrace(false);
        server.setSilent(true);
        server.setNoSystemExit(true);
        server.setRestartOnShutdown(false);
        server.start();
    }

    /**
     * Shutdown the in-memory HSQLDB database by sending it a SHUTDOWN command.
     *
     * @param logger Used to report errors and raise exceptions.
     */
    @Override
    public void stop(final Logger logger) {
        assert logger != null;

        if (server == null) {
            try {
                final DataSource dataSource = getDataSource();
                final Connection connection = dataSource.getConnection();
                try {
                    final Statement statement = connection.createStatement();
                    try {
                        statement.execute("SHUTDOWN");
                    } finally {
                        statement.close();
                    }
                } finally {
                    connection.close();
                }
            } catch (final SQLException exception) {
                final String message = MessageUtil.getMessage(ERROR_STOPPING_SERVER, getDatabaseName());
                logger.logError(message, exception);
                //throw new MojoFailureException(message, exception);
                return;
            }
        } else {
            server.shutdown();
            server = null;
        }
    }
}
