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

import java.util.Map;

import javax.sql.DataSource;

import org.apache.maven.plugin.MojoFailureException;

/**
 * Describes the operations that are used by the Mojos to launch in-memory
 * databases, load data into, execute scripts against or shutdown the database.
 * 
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public interface Database {
	
	/**
	 * Get the data source that describes the connection to the in-memory
	 * database.
	 * 
	 * @return The data source.
	 */
	DataSource getDataSource();

	/**
	 * Get the data source that describes the connection to the in-memory
	 * database.
	 * 
	 * @param attributes
	 *            Additional connection attributes.
	 * @return The data source.
	 */
	DataSource getDataSource(Map<String, String> attributes);

	/**
	 * Start the in-memory database.
	 * 
	 * @param logger
	 *            Used to report errors and raise exceptions.
	 * @throws MojoFailureException
	 *             If the database cannot be started.
	 */
	void start(Logger logger) throws MojoFailureException;

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
	void load(Logger logger, Source source) throws MojoFailureException;

	/**
	 * Shutdown the in-memory database.
	 * 
	 * @param logger
	 *            Used to report errors and raise exceptions.
	 * @throws MojoFailureException
	 *             If there was an error trying to shutdown the database.
	 */
	void shutdown(Logger logger) throws MojoFailureException;
}
