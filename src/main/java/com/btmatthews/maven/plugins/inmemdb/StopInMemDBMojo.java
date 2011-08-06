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

package com.btmatthews.maven.plugins.inmemdb;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.maven.plugin.MojoFailureException;

/**
 * This plug-in Mojo stops an In Memory Database.
 * 
 * @goal stop
 * 
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public final class StopInMemDBMojo extends AbstractInMemDBMojo {

	/**
	 * The message key for the error reported when a server cannot be stopped.
	 */
	private static final String ERROR_STOPPING_SERVER = "error_stopping_server";

	/**
	 * The default constructor.
	 */
	public StopInMemDBMojo() {
	}

	/**
	 * Execute the Maven goal to stop the in-memory database by sending it a
	 * shutdown command.
	 * 
	 * @throws MojoFailureException
	 *             If there was an error executing the goal.
	 */
	public void execute() throws MojoFailureException {
		final DataSource dataSource = getDataSource();
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
			logError(ERROR_STOPPING_SERVER, exception);
		}
	}
}