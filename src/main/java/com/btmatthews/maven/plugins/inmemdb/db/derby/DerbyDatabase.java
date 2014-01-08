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

package com.btmatthews.maven.plugins.inmemdb.db.derby;

import com.btmatthews.maven.plugins.inmemdb.Loader;
import com.btmatthews.maven.plugins.inmemdb.MessageUtil;
import com.btmatthews.maven.plugins.inmemdb.db.AbstractSQLDatabase;
import com.btmatthews.maven.plugins.inmemdb.ldr.dbunit.DBUnitCSVLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.dbunit.DBUnitFlatXMLLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.dbunit.DBUnitXLSLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.dbunit.DBUnitXMLLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.sqltool.SQLLoader;
import com.btmatthews.utils.monitor.Logger;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.derby.drda.NetworkServerControl;
import org.apache.derby.iapi.reference.Property;
import org.codehaus.plexus.util.StringUtils;

import javax.sql.DataSource;
import java.io.OutputStream;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements support for in-memory Apache Derby databases.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public final class DerbyDatabase extends AbstractSQLDatabase {

    /**
     * The connection protocol for in-memory H2 databases.
     */
    private static final String PROTOCOL = "derby://localhost:{0,number,#}/memory:";
    /**
     * Default port Derby listens on, can be altered via setting port property
     */
    private static final int DEFAULT_PORT = 1527;
    /**
     * The name of the additional connection parameter which will cause the
     * database to be created.
     */
    private static final String CREATE = "create";
    /**
     * The name of the additional connection parameter which will cause the
     * database to be dropped.
     */
    private static final String DROP = "drop";
    /**
     * The value used with the {@link #CREATE} and {@link #DROP} connection parameters.
     */
    private static final String TRUE = "true";
    /**
     * The JDBC driver class name.
     */
    private static final String DRIVER_CLASS = "org.apache.derby.jdbc.ClientDriver";
    /**
     * The loaders that are supported for loading data or executing scripts.
     */
    private static final Loader[] LOADERS = new Loader[]{
            new DBUnitXMLLoader(), new DBUnitFlatXMLLoader(),
            new DBUnitCSVLoader(), new DBUnitXLSLoader(), new SQLLoader()};
    /**
     *
     */
    private static final OutputStream DEV_NULL = new OutputStream() {
        public void write(int b) {
        }
    };
    /**
     * The server used to accept connections from other JVMs.
     */
    private NetworkServerControl server;

    /**
     * The default constructor initializes the default database port.
     */
    public DerbyDatabase() {
        super(DEFAULT_PORT);
    }

    /**
     * Get the database connection protocol used for JDBC connections
     *
     * @return Returns protocol
     */
    protected String getUrlProtocol() {
        return MessageFormat.format(PROTOCOL, getPort());
    }

    /**
     * Get the data source that describes the connection to the in-memory Apache
     * Derby database.
     *
     * @return Returns {@code dataSource} which was initialised by the
     *         constructor.
     */
    @Override
    public DataSource getDataSource() {
        final BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(getUrl());
        dataSource.setUsername(getUsername());
        if (StringUtils.isNotEmpty(getPassword())) {
            dataSource.setPassword(getPassword());
        }
        dataSource.setDriverClassName(DRIVER_CLASS);
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
     * Start the in-memory Apache Derby database.
     *
     * @param logger Used to report errors and raise exceptions.
     */
    @Override
    public void start(final Logger logger) {

        logger.logInfo("Starting embedded Derby database");

        System.setProperty(Property.ERRORLOG_FIELD_PROPERTY, "com.btmatthews.maven.plugins.inmemdb.db.derby.DerbyDatabase.DEV_NULL");

        try {
            server = new NetworkServerControl(InetAddress.getByName("localhost"), getPort());
            server.start(null);
        } catch (final Exception exception) {
            final String message = MessageUtil.getMessage(ERROR_STARTING_SERVER, getDatabaseName());
            logger.logError(message, exception);
            return;
        }

        try {
            Class.forName(DRIVER_CLASS).newInstance();
        } catch (final InstantiationException exception) {
            final String message = MessageUtil.getMessage(ERROR_STARTING_SERVER, getDatabaseName());
            logger.logError(message, exception);
            return;
        } catch (final IllegalAccessException exception) {
            final String message = MessageUtil.getMessage(ERROR_STARTING_SERVER, getDatabaseName());
            logger.logError(message, exception);
            return;
        } catch (final ClassNotFoundException exception) {
            final String message = MessageUtil.getMessage(ERROR_STARTING_SERVER, getDatabaseName());
            logger.logError(message, exception);
            return;
        }

        final Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(CREATE, TRUE);
        try {
            final Connection connection = DriverManager.getConnection(getUrl(attributes), getUsername(), getPassword().length() == 0 ? null : getPassword());
            connection.close();
        } catch (final SQLException exception) {
            final String message = MessageUtil.getMessage(ERROR_STARTING_SERVER, getDatabaseName());
            logger.logError(message, exception);
            return;
        }

        logger.logInfo("Started embedded Derby database");
    }

    /**
     * Shutdown the in-memory Apache Derby database by opening a connection with
     * <code>drop=true</code>. If successful this will cause a SQL exception
     * with a SQL State of 08006 and a vendor specific error code of 45000.
     *
     * @param logger Used to report errors and raise exceptions.
     */
    @Override
    public void stop(final Logger logger) {

        logger.logInfo("Stopping embedded Derby database");

        if (server != null) {
            final Map<String, String> attributes = new HashMap<String, String>();
            attributes.put(DROP, TRUE);
            try {
                DriverManager.getConnection(getUrl(attributes), getUsername(), getPassword().length() == 0 ? null : getPassword());
            } catch (final SQLException exception) {
                if (exception.getErrorCode() != 45000 || !"08006".equals(exception.getSQLState())) {
                    final String message = MessageUtil.getMessage(ERROR_STARTING_SERVER, getDatabaseName());
                    logger.logError(message, exception);
                    return;
                }
            }
            try {
                server.shutdown();
            } catch (final Exception exception) {
                final String message = MessageUtil.getMessage(ERROR_STOPPING_SERVER, getDatabaseName());
                logger.logError(message, exception);
                return;
            }
        }

        logger.logInfo("Stopped embedded Derby database");
    }

    @Override
    public boolean isStarted(final Logger logger) {
        if (server != null) {
            try {
                server.ping();
                return true;
            } catch (final Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean isStopped(final Logger logger) {
        try {
            server.ping();
            return false;
        } catch (final Exception e) {
            return true;
        }
    }
}
