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

package com.btmatthews.maven.plugins.inmemdb.db.h2;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.maven.plugin.MojoFailureException;
import org.h2.jdbcx.JdbcDataSource;

import com.btmatthews.maven.plugins.inmemdb.Loader;
import com.btmatthews.maven.plugins.inmemdb.Logger;
import com.btmatthews.maven.plugins.inmemdb.db.AbstractDatabase;
import com.btmatthews.maven.plugins.inmemdb.ldr.dbunit.DBUnitCSVLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.dbunit.DBUnitFlatXMLLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.dbunit.DBUnitXLSLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.dbunit.DBUnitXMLLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.sqltool.SQLLoader;

/**
 * Implements support for in-memory H2 databases.
 * 
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public final class H2Database extends AbstractDatabase {

	/**
	 * The connection protocol for in-memory H2 databases.
	 */
	private static final String PROTOCOL = "h2:mem";

	/**
	 * The loaders that are supported for loading data or executing scripts.
	 */
	private static final Loader[] LOADERS = new Loader[] {
			new DBUnitXMLLoader(), new DBUnitFlatXMLLoader(),
			new DBUnitCSVLoader(), new DBUnitXLSLoader(), new SQLLoader() };

	/**
	 * The constructor for this object stores the database name, user name and
	 * password that will be used to create connections.
	 * 
	 * @param database
	 *            The database name.
	 * @param username
	 *            The user name for the database connection.
	 * @param password
	 *            The password for the database connection.
	 */
	public H2Database(final String database, final String username,
			final String password) {
		super(database, username, password);
	}

	/**
	 * Get the database connection protocol.
	 * 
	 * @return Always returns {@link H2Database#PROTOCOL}.
	 */
	protected String getUrlProtocol() {
		return PROTOCOL;
	}

	/**
	 * Get the data source that describes the connection to the in-memory H2
	 * database.
	 * 
	 * @param attributes
	 *            Additional attributes for the data source connection string.
	 * @return Returns {@link dataSource} which was initialised by the
	 *         constructor.
	 */
	public DataSource getDataSource(final Map<String, String> attributes) {
		final JdbcDataSource dataSource = new JdbcDataSource();
		dataSource.setURL(getUrl(attributes));
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
	 * Start the in-memory H2 database.
	 * 
	 * @param logger
	 *            Used to report errors and raise exceptions.
	 * @throws MojoFailureException
	 *             If the database cannot be started.
	 */
	public void start(final Logger logger) throws MojoFailureException {
		assert logger != null;

		final Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("DB_CLOSE_DELAY", "-1");
		try {
			final DataSource dataSource = getDataSource(attributes);
			final Connection connection = dataSource.getConnection();
			connection.close();
		} catch (final SQLException exception) {
			logger.logError(ERROR_STARTING_SERVER, exception, getDatabaseName());
		}
	}

	/**
	 * Shutdown the in-memory H2 database by opening a connection and issuing
	 * the SHUTDOWN command.
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
