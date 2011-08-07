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

import javax.sql.DataSource;

import org.apache.maven.plugin.MojoFailureException;
import org.hsqldb.jdbc.JDBCDataSource;

import com.btmatthews.maven.plugins.inmemdb.AbstractDatabase;
import com.btmatthews.maven.plugins.inmemdb.Loader;
import com.btmatthews.maven.plugins.inmemdb.Logger;
import com.btmatthews.maven.plugins.inmemdb.ldr.DBUnitXMLLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.SQLLoader;

/**
 * Implements support for in-memory HSQLDB databases.
 * 
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public class HSQLDBDatabase extends AbstractDatabase {

	/**
	 * The message key for the error reported when a server cannot be stopped.
	 */
	private static final String ERROR_STOPPING_SERVER = "error_stopping_server";

	/**
	 * The data source that describes the connection to the in-memory HSQLDB
	 * database.
	 */
	private JDBCDataSource dataSource;

	/**
	 * The loaders that are supported for loading data or executing scripts.
	 */
	private static final Loader[] LOADERS = new Loader[] {
			new DBUnitXMLLoader(), new SQLLoader() };

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
	public HSQLDBDatabase(String database, String username, String password) {
		final StringBuilder url = new StringBuilder("jdbc:hsqldb:mem:");
		url.append(database);
		dataSource = new JDBCDataSource();
		dataSource.setUrl(url.toString());
		dataSource.setUser(username);
		dataSource.setPassword(password);
	}

	/**
	 * Get the data source that describes the connection to the in-memory HSQLDB
	 * database.
	 * 
	 * @return Returns {@link dataSource} which was initialised by the
	 *         constructor.
	 */
	public DataSource getDataSource() {
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
			logger.logError(ERROR_STOPPING_SERVER, exception);
		}
	}
}
