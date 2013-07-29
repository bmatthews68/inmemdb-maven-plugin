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

import java.io.PrintWriter;
import java.net.InetAddress;
import java.sql.Connection;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
import org.apache.derby.jdbc.ClientDriver;
import org.codehaus.plexus.util.StringUtils;

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
     * The value of the additional connection parameter which will cause the
     * database to be created.
     */
    private static final String CREATE = "create";

    /**
     * The value of the additional connection parameter which will cause the
     * database to be shutdown.
     */
    private static final String DROP = "drop";

    /**
     * The loaders that are supported for loading data or executing scripts.
     */
    private static final Loader[] LOADERS = new Loader[]{
            new DBUnitXMLLoader(), new DBUnitFlatXMLLoader(),
            new DBUnitCSVLoader(), new DBUnitXLSLoader(), new SQLLoader() };

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
     * @param attributes Additional attributes for the data source connection string.
     * @return Returns {@code dataSource} which was initialised by the
     *         constructor.
     */
    @Override
    public DataSource getDataSource(final Map<String, String> attributes) {
        final BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(getUrl(attributes));
        dataSource.setUsername(getUsername());
        if (StringUtils.isNotEmpty(getPassword())) {
            dataSource.setPassword(getPassword());
        }
        dataSource.setDriverClassName("org.apache.derby.jdbc.ClientDriver");
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

        try {
            server = new NetworkServerControl(InetAddress.getByName("localhost"), getPort());
            server.start(null);
        } catch (final Exception exception) {
            final String message = MessageUtil.getMessage(ERROR_STARTING_SERVER, getDatabaseName());
            logger.logError(message, exception);
            return;
        }
        try {
            ClientDriver.class.newInstance();
        } catch (final InstantiationException exception) {
            final String message = MessageUtil.getMessage(ERROR_STARTING_SERVER, getDatabaseName());
            logger.logError(message, exception);
            return;
        } catch (final IllegalAccessException exception) {
            final String message = MessageUtil.getMessage(ERROR_STARTING_SERVER, getDatabaseName());
            logger.logError(message, exception);
            return;
        }

        final Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(CREATE, "true");
        try {
            final DataSource dataSource = getDataSource(attributes);
            final Connection connection = dataSource.getConnection();
            connection.close();
        } catch (final SQLException exception) {
            final String message = MessageUtil.getMessage(ERROR_STARTING_SERVER, getDatabaseName());
            logger.logError(message, exception);
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

        final Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(DROP, "true");
        final String url = getUrl(attributes);
        try {
            new ClientDriver().connect(url, new Properties());
            final String message = MessageUtil.getMessage(ERROR_STOPPING_SERVER, getDatabaseName());
            logger.logError(message);
            return;
        } catch (final SQLException exception) {
            if (!("08006".equals(exception.getSQLState()))) {
                final String message = MessageUtil.getMessage(ERROR_STOPPING_SERVER, getDatabaseName());
                logger.logError(message, exception);
                return;
            }
            try {
                server.shutdown();
            } catch (final Exception e) {
                logger.logError(e.getMessage(), e);
            }
        }

        logger.logInfo("Stopped embedded Derby database");
    }
}
