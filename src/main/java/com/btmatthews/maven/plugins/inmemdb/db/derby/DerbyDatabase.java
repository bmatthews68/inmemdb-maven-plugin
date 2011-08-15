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

package com.btmatthews.maven.plugins.inmemdb.db.derby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.apache.maven.plugin.MojoFailureException;

import com.btmatthews.maven.plugins.inmemdb.Loader;
import com.btmatthews.maven.plugins.inmemdb.Logger;
import com.btmatthews.maven.plugins.inmemdb.db.AbstractDatabase;
import com.btmatthews.maven.plugins.inmemdb.ldr.dbunit.DBUnitCSVLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.dbunit.DBUnitFlatXMLLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.dbunit.DBUnitXLSLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.dbunit.DBUnitXMLLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.sqltool.SQLLoader;

/**
 * Implements support for in-memory Apache Derby databases.
 * 
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public final class DerbyDatabase extends AbstractDatabase {

	/**
	 * The connection protocol for in-memory Apache Derby databases.
	 */
	private static final String PROTOCOL = "derby:memory";

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
	public DerbyDatabase(final String database, final String username,
			final String password) {
		super(database, username, password);
	}

	/**
	 * Get the database connection protocol.
	 * 
	 * @return Always returns {@link DerbyDatabase#PROTOCOL}.
	 */
	protected String getUrlProtocol() {
		return PROTOCOL;
	}

	/**
	 * Get the data source that describes the connection to the in-memory Apache
	 * Derby database.
	 * 
	 * @param attributes
	 *            Additional attributes for the data source connection string.
	 * @return Returns {@link dataSource} which was initialised by the
	 *         constructor.
	 */
	public DataSource getDataSource(final Map<String, String> attributes) {
		final BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl(getUrl(attributes));
		dataSource.setUsername(getUsername());
		dataSource.setPassword(getPassword());
		dataSource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
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
			EmbeddedDriver.class.newInstance();
		} catch (InstantiationException exception) {
			logger.logError(ERROR_STARTING_SERVER, exception, getDatabaseName());
		} catch (IllegalAccessException exception) {
			logger.logError(ERROR_STARTING_SERVER, exception, getDatabaseName());
		}

		final Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(CREATE, "true");
		try {
			final DataSource dataSource = getDataSource(attributes);
			final Connection connection = dataSource.getConnection();
			connection.close();
		} catch (final SQLException exception) {
			logger.logError(ERROR_STARTING_SERVER, exception, getDatabaseName());
		}
	}

	/**
	 * Shutdown the in-memory Apache Derby database by opening a connection with
	 * <code>drop=true</code>. If successful this will cause a SQL exception
	 * with a SQL State of 08006 and a vendor specific error code of 45000.
	 * 
	 * @param logger
	 *            Used to report errors and raise exceptions.
	 * @throws MojoFailureException
	 *             If there was an error trying to shutdown the database.
	 */
	public void shutdown(final Logger logger) throws MojoFailureException {
		assert logger != null;

		final Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(DROP, "true");
		final String url = getUrl(attributes);
		try {
			DriverManager.getConnection(url);
			logger.logError(ERROR_STOPPING_SERVER, getDatabaseName());
		} catch (final SQLException exception) {
			if (!("08006".equals(exception.getSQLState()) && 45000 == exception
					.getErrorCode())) {
				logger.logError(ERROR_STOPPING_SERVER, exception,
						getDatabaseName());
			}
		}
	}
}
