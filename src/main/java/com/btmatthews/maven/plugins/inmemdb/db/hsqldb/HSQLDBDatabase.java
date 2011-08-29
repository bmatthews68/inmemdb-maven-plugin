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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.maven.plugin.MojoFailureException;
import org.hsqldb.DatabaseURL;
import org.hsqldb.jdbc.JDBCDataSource;
import org.hsqldb.server.Server;
import org.hsqldb.server.ServerConstants;

import com.btmatthews.maven.plugins.inmemdb.Loader;
import com.btmatthews.maven.plugins.inmemdb.Logger;
import com.btmatthews.maven.plugins.inmemdb.db.AbstractDatabase;
import com.btmatthews.maven.plugins.inmemdb.ldr.dbunit.DBUnitCSVLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.dbunit.DBUnitFlatXMLLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.dbunit.DBUnitXLSLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.dbunit.DBUnitXMLLoader;
import com.btmatthews.maven.plugins.inmemdb.ldr.sqltool.SQLLoader;

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
	private static final Loader[] LOADERS = new Loader[] {
			new DBUnitXMLLoader(), new DBUnitFlatXMLLoader(),
			new DBUnitCSVLoader(), new DBUnitXLSLoader(), new SQLLoader() };

	/**
	 * The HSQLDB server.
	 */
	private Server server;

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
	 * Get the database connection protocol.
	 * 
	 * @return Always returns {@link HSQLDBDatabase#PROTOCOL}.
	 */
	protected String getUrlProtocol() {
		return PROTOCOL;
	}

	/**
	 * Get the data source that describes the connection to the in-memory HSQLDB
	 * database.
	 * 
	 * @param attributes
	 *            Additional attributes for the data source connection string.
	 * @return Returns {@link dataSource} which was initialised by the
	 *         constructor.
	 */
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
	 * @return Returns {@link LOADERS}.
	 */
	public Loader[] getLoaders() {
		return LOADERS;
	}

	/**
	 * Start the in-memory HSQLDB server.
	 * 
	 * @param logger
	 *            Used to report errors and raise exceptions.
	 * @throws MojoFailureException
	 *             If the database cannot be started.
	 */
	public void start(final Logger logger) throws MojoFailureException {

		server = new Server();
		server.setDatabasePath(0, DatabaseURL.S_MEM + getDatabaseName());
		server.setDatabaseName(0, getDatabaseName());
		server.setDaemon(true);
		server.setAddress("localhost");
		server.setPort(9001);
		server.setErrWriter(new PrintWriter(System.err));
		server.setLogWriter(new PrintWriter(System.out));
		server.setTls(false);
		server.setTrace(false);
		server.setSilent(true);
		if (server.start() != ServerConstants.SERVER_STATE_ONLINE) {
			logger.logError(ERROR_STARTING_SERVER);
		}
	}

	/**
	 * Run the in-memory HSQLDB server.
	 * 
	 * @param logger
	 *            Used to report errors and raise exceptions.
	 * @throws MojoFailureException
	 *             If there was an error trying to shutdown the database.
	 */
	public void run(final Logger logger) throws MojoFailureException {
		try {
			server.getServerThread().join();
		} catch (final InterruptedException exception) {
			logger.logError("", exception);
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
