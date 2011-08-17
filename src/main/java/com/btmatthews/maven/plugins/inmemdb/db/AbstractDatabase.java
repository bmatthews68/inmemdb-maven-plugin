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

package com.btmatthews.maven.plugins.inmemdb.db;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.maven.plugin.MojoFailureException;

import com.btmatthews.maven.plugins.inmemdb.Database;
import com.btmatthews.maven.plugins.inmemdb.Loader;
import com.btmatthews.maven.plugins.inmemdb.Logger;
import com.btmatthews.maven.plugins.inmemdb.Source;

/**
 * Abstract base classes for database objects providing an implementation of the
 * {@link load()} operation that is used to load data into or execute scripts
 * against the database.
 * 
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public abstract class AbstractDatabase implements Database {

	/**
	 * The message key for the error reported when a file type is not supported.
	 */
	private static final String UNSUPPORTED_FILE_TYPE = "unsupported_file_type";

	/**
	 * The message key for the error reported when a server cannot be started.
	 */
	protected static final String ERROR_STARTING_SERVER = "error_starting_server";

	/**
	 * The message key for the error reported when a server cannot be stopped.
	 */
	protected static final String ERROR_STOPPING_SERVER = "error_stopping_server";

	/**
	 * The database name.
	 */
	private String databaseName;

	/**
	 * The user name used to connect to the database.
	 */
	private String databaseUsername;

	/**
	 * The password used to connect to the database.
	 */
	private String databasePassword;

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
	protected AbstractDatabase(final String database, final String username,
			final String password) {
		this.databaseName = database;
		this.databaseUsername = username;
		if (password == null) {
			this.databasePassword = "";
		} else {
			this.databasePassword = password;
		}
	}

	/**
	 * Get the database name.
	 * 
	 * @return The database name.
	 */
	protected final String getDatabaseName() {
		return this.databaseName;
	}

	/**
	 * Get the user name used to connect to the database.
	 * 
	 * @return The user name.
	 */
	protected final String getUsername() {
		return databaseUsername;
	}

	/**
	 * Get the password used to connect to the database.
	 * 
	 * @return The password.
	 */
	protected final String getPassword() {
		return databasePassword;
	}

	/**
	 * Get the loaders that are supported for loading data or executing scripts.
	 * 
	 * @return Returns an array of loaders.
	 */
	protected abstract Loader[] getLoaders();

	/**
	 * Get a data source object without additional connection attributes.
	 * 
	 * @return The data source object.
	 */
	public final DataSource getDataSource() {
		final Map<String, String> attributes = new HashMap<String, String>();
		return getDataSource(attributes);
	}

	/**
	 * Find the loader that supports the source file and use it to load the data
	 * into or execute the script against the database.
	 * 
	 * @param logger
	 *            Used to report errors and raise exceptions.
	 * @param source
	 *            The source file containing data or script.
	 * @throws MojoFailureException
	 *             If there was an error loading the data or executing the
	 *             script or if the source file type was not supported.
	 */
	public final void load(final Logger logger, final Source source)
			throws MojoFailureException {
		if (source == null) {
			logger.logError(UNSUPPORTED_FILE_TYPE, "null");
		} else {
			final Loader[] loaders = getLoaders();
			int i = 0;
			while (i < loaders.length) {
				if (loaders[i].isSupported(logger, source)) {
					loaders[i].load(logger, this, source);
					break;
				}
				++i;
			}
			if (i >= loaders.length) {
				logger.logError(UNSUPPORTED_FILE_TYPE, source.getSourceFile().getPath());
			}
		}
	}

	/**
	 * Get the database connection protocol.
	 * 
	 * @return The database connection protocol.
	 */
	protected abstract String getUrlProtocol();

	/**
	 * Construct the JDBC URL with connection specific attributes.
	 * 
	 * @param attributes
	 *            The connection specific attributes.
	 * @return The JDBC URL.
	 */
	public final String getUrl(final Map<String, String> attributes) {
		final StringBuilder url = new StringBuilder("jdbc:");
		url.append(getUrlProtocol());
		url.append(':');
		url.append(getDatabaseName());
		if (attributes.size() > 0) {
			for (Map.Entry<String, String> entry : attributes.entrySet()) {
				url.append(';');
				url.append(entry.getKey());
				url.append('=');
				url.append(entry.getValue());
			}
		}
		return url.toString();
	}
}
