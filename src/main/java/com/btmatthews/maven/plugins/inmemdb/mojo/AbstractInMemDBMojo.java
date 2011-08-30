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

package com.btmatthews.maven.plugins.inmemdb.mojo;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;

import com.btmatthews.maven.plugins.inmemdb.Database;
import com.btmatthews.maven.plugins.inmemdb.DatabaseFactory;
import com.btmatthews.maven.plugins.inmemdb.Logger;

/**
 * Abstract base class for all the In Memory Database plug-in's Mojos.
 * 
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public abstract class AbstractInMemDBMojo extends AbstractMojo implements
		Logger {

	/**
	 * The base name of the message resource bundle.
	 */
	private static final String BUNDLE_NAME = "com.btmatthews.maven.plugins.inmemdb.messages";

	/**
	 * The message resource bundle.
	 */
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	/**
	 * The database type.
	 * 
	 * @parameter expression="${inmemdb.type}" default-value="hsqldb"
	 */
	private String type;

	/**
	 * The database name.
	 * 
	 * @parameter expression="${inmemdb.database}" default-value="."
	 */
	private String database;
	/**
	 * The username for database connections.
	 * 
	 * @parameter expression="${inmemdb.username}" default-value="sa"
	 */
	private String username;
	/**
	 * The password for database connections.
	 * 
	 * @parameter expression="${inmemdb.password}" default-value="sa"
	 */
	private String password;

	/**
	 * The default constructor.
	 */
	protected AbstractInMemDBMojo() {
	}
	
	/**
	 * Use the database, user name and password plug-in configuration parameters
	 * to construct an in-memory HSQLDB database.
	 * 
	 * @return The database.
	 */
	protected final Database getDatabase() {
		final DatabaseFactory factory = new DatabaseFactory();
		return factory.createDatabase(type, database, username, password);
	}

	/**
	 * Write an error message to the Maven log and raise a
	 * {@link MojoFailureException}.
	 * 
	 * @param messageKey
	 *            The message key in the resource bundle.
	 * @param arguments
	 *            The arguments.
	 * @throws MojoFailureException
	 *             Propagates the error as an exception.
	 */
	public final void logErrorAndThrow(final String messageKey, final Object... arguments)
			throws MojoFailureException {
		final String message = getMessage(messageKey, arguments);
		getLog().error(message);
		throw new MojoFailureException(message);
	}

	/**
	 * Write an error message that arose from an exception to the Maven log.
	 * 
	 * @param messageKey
	 *            The key of the message in the resource bundle.
	 * @param exception
	 *            The exception.
	 * @param arguments
	 *            The message arguments.
	 * @throws MojoFailureException
	 *             Propagates the error as an exception encasulating the
	 *             original exception.
	 */
	public final void logErrorAndThrow(final String messageKey, final Throwable exception,
			final Object... arguments) throws MojoFailureException {
		final String message = getMessage(messageKey, arguments);
		getLog().error(message, exception);
		throw new MojoFailureException(message, exception);
	}

	/**
	 * Retrieve the message pattern for a given message key from the resource
	 * bundle and format the message replacing the place holders with the
	 * message arguments.
	 * 
	 * @param messageKey
	 *            The key of the message in the resource bundle.
	 * @param arguments
	 *            The message arguments.
	 * @return The expanded message string.
	 */
	private static String getMessage(final String messageKey,
			final Object... arguments) {
		final String messagePattern = RESOURCE_BUNDLE.getString(messageKey);
		return MessageFormat.format(messagePattern, arguments);
	}
}