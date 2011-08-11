/*
 * Copyright 2011 Brian Matthews
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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.maven.plugin.MojoFailureException;
import org.hsqldb.jdbc.JDBCDataSource;

import com.btmatthews.maven.plugins.inmemdb.AbstractDatabase;
import com.btmatthews.maven.plugins.inmemdb.Loader;
import com.btmatthews.maven.plugins.inmemdb.Logger;
import com.btmatthews.maven.plugins.inmemdb.ldr.DBUnitCSVLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.DBUnitFlatXMLLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.DBUnitXLSLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.DBUnitXMLLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.SQLLoader;

/**
 * Implements support for in-memory HSQLDB databases.
 * 
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public final class HSQLDBDatabase extends AbstractDatabase {

    /**
     * The loaders that are supported for loading data or executing scripts.
     */
    private static final Loader[] LOADERS = new Loader[] {
            new DBUnitXMLLoader(), new DBUnitFlatXMLLoader(),
            new DBUnitCSVLoader(), new DBUnitXLSLoader(), new SQLLoader() };

    /**
     * The constructor for this object creates and initialises the data source.
     * 
     * @param database
     *            The database name.
     * @param username
     *            The user name for the database connection.
     * @param password
     *            The password for the database connection.
     */
    public HSQLDBDatabase(final String database, final String username,
            final String password) {
        super(database, username, password);
    }

    /**
     * Get the data source that describes the connection to the in-memory HSQLDB database.
     * 
     * @param attributes
     *            Additional attributes for the data source connection string.
     * @return Returns {@link dataSource} which was initialised by the constructor.
     */
    public DataSource getDataSource(final Map<String, String> attributes) {
        final StringBuilder url = new StringBuilder("jdbc:hsqldb:mem:");
        url.append(getDatabaseName());
        final JDBCDataSource dataSource = new JDBCDataSource();
        dataSource.setUrl(url.toString());
        dataSource.setUser(getUsername());
        dataSource.setPassword(getPassword());
        return dataSource;
    }

    /**
     * Get the loaders that are supported for loading data or executing scripts.
     * 
     * @return Returns {@link LOADERS}.
     */
    public Loader[] getLoaders() {
        return LOADERS;
    }

    /**
     * Start the in-memory Apache Derby database.
     * 
     * @param logger
     *            Used to report errors and raise exceptions.
     * @throws MojoFailureException
     *             If the database cannot be started.
     */
    public void start(final Logger logger) throws MojoFailureException {
        assert logger != null;

        try {
            final DataSource dataSource = getDataSource();
            final Connection connection = dataSource.getConnection();
            connection.close();
        } catch (final SQLException exception) {
            logger.logError(ERROR_STARTING_SERVER, exception, getDatabaseName());
        }
    }

    /**
     * Shutdown the in-memory HSQLDB database by sending it a SHUTDOWN command.
     * 
     * @param logger
     *            Used to report errors and raise exceptions.
     * @throws MojoFailureException
     *             If there was an error trying to shutdown the database.
     */
    public void shutdown(final Logger logger) throws MojoFailureException {
        assert logger != null;

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
            logger.logError(ERROR_STOPPING_SERVER, exception, getDatabaseName());
        }
    }
}
