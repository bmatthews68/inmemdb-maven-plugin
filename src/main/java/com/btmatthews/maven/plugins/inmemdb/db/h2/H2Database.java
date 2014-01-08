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

package com.btmatthews.maven.plugins.inmemdb.db.h2;

import com.btmatthews.maven.plugins.inmemdb.Loader;
import com.btmatthews.maven.plugins.inmemdb.MessageUtil;
import com.btmatthews.maven.plugins.inmemdb.db.AbstractSQLDatabase;
import com.btmatthews.maven.plugins.inmemdb.ldr.dbunit.DBUnitCSVLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.dbunit.DBUnitFlatXMLLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.dbunit.DBUnitXLSLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.dbunit.DBUnitXMLLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.sqltool.SQLLoader;
import com.btmatthews.utils.monitor.Logger;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.server.TcpServer;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements support for in-memory H2 databases.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public final class H2Database extends AbstractSQLDatabase {

    /**
     * The connection protocol for in-memory H2 databases.
     */
    private static final String PROTOCOL = "h2:tcp://localhost:{0,number,#}/mem:";
    /**
     * Default port H2 listens on, can be altered via setting port property
     */
    private static final int DEFAULT_PORT = 9092;
    /**
     * The loaders that are supported for loading data or executing scripts.
     */
    private static final Loader[] LOADERS = new Loader[]{
            new DBUnitXMLLoader(), new DBUnitFlatXMLLoader(),
            new DBUnitCSVLoader(), new DBUnitXLSLoader(), new SQLLoader()};
    /**
     * The H2 TCP server.
     */
    private TcpServer service;

    /**
     * The default constructor initializes the default database port.
     */
    public H2Database() {
        super(DEFAULT_PORT);
    }

    /**
     * Get the database connection protocol.
     *
     * @return Always returns {@link H2Database#PROTOCOL}.
     */
    protected String getUrlProtocol() {
        return MessageFormat.format(PROTOCOL, getPort());
    }

    /**
     * Get the data source that describes the connection to the in-memory H2
     * database.
     *
     * @return Returns {@code dataSource} which was initialised by the
     *         constructor.
     */
    @Override
    public DataSource getDataSource() {
        final Map<String, String> actualAttributes = new HashMap<String, String>(getAttributes());
        actualAttributes.put("DB_CLOSE_DELAY", "-1");
        final JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(getUrl(actualAttributes));
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
     * Start the in-memory H2 database.
     *
     * @param logger Used to report errors and raise exceptions.
     */
    @Override
    public void start(final Logger logger) {

        logger.logInfo("Starting embedded H2 database");

        try {
            service = new TcpServer();
            service.init("-tcpDaemon", "-tcpPort", Integer.toString(getPort()));
            service.start();
        } catch (final SQLException exception) {
            final String message = MessageUtil.getMessage(ERROR_STARTING_SERVER, getDatabaseName());
            logger.logError(message, exception);
            return;
        }

        final Thread serviceListenerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                service.listen();
            }
        });
        serviceListenerThread.setDaemon(true);
        serviceListenerThread.start();

        logger.logInfo("Embedded H2 database has started");
    }

    /**
     * Shutdown the in-memory H2 database by opening a connection and issuing
     * the SHUTDOWN command.
     *
     * @param logger Used to report errors and raise exceptions.
     */
    public void stop(final Logger logger) {

        logger.logInfo("Stopping embedded H2 database");

        if (service != null) {
            service.stop();
        }

        logger.logInfo("Stopped embedded H2 database");
    }

    @Override
    public boolean isStarted(final Logger logger) {
        return service != null && service.isRunning(true);
    }

    @Override
    public boolean isStopped(final Logger logger) {
        return !service.isRunning(false);
    }
}

