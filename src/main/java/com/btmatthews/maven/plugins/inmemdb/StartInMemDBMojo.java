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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

import javax.sql.DataSource;

import org.apache.maven.plugin.MojoFailureException;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.hsqldb.cmdline.SqlFile;
import org.hsqldb.cmdline.SqlToolError;

/**
 * This plug-in Mojo starts an In Memory Database.
 * 
 * @goal start
 * 
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public class StartInMemDBMojo extends AbstractInMemDBMojo {

	/**
	 * The message key for the error reported when a file type is not supported.
	 */
	private static final String UNSUPPORTED_FILE_TYPE = "unsupported_file_type";

	/**
	 * The message key for the error reported when a source file cannot be read.
	 */
	private static final String CANNOT_READ_SOURCE_FILE = "cannot_read_source_file";

	/**
	 * The message key for the error reported when a source file cannot be
	 * processed.
	 */
	private static final String ERROR_PROCESSING_SOURCE_FILE = "error_processing_source_file";

	/**
	 * The source files used to populate the database.
	 * 
	 * @parameter
	 */
	private File[] sources;

	/**
	 * The default constructor.
	 */
	public StartInMemDBMojo() {
	}

	/**
	 * Execute the Maven goal by creating a data source and then iterating over
	 * the list of sources and loading each one.
	 * 
	 * If the source file name ends with .sql it is treated as DDL/DML script.
	 * 
	 * If the source file name ends with .dbunit.xml it is treated as DBUnit XML
	 * data set.
	 * 
	 * @throws MojoFailureException
	 *             If there was an error executing the goal.
	 */
	public void execute() throws MojoFailureException {
		final Locale locale = Locale.getDefault();
		final DataSource dataSource = getDataSource();
		for (final File source : sources) {
			final String sourceName = source.getAbsolutePath().toLowerCase(
					locale);
			if (sourceName.endsWith(".sql")) {
				loadSQL(dataSource, source);
			} else if (sourceName.endsWith(".dbunit.xml")) {
				loadDBUnitXML(dataSource, source);
			} else {
				logError(UNSUPPORTED_FILE_TYPE, source.getPath());
			}
		}
	}

	/**
	 * Load a DDL/DML script into the in-memory database.
	 * 
	 * @param dataSource
	 *            The data source used to connect to the in-memory database.
	 * @param source
	 *            The source file containing the DDL/DML script.
	 * @throws MojoFailureException
	 *             If there was an error loading the DDL/DML script.
	 */
	private void loadSQL(final DataSource dataSource, final File source)
			throws MojoFailureException {
		try {
			final SqlFile sqlFile = new SqlFile(source);
			final Connection connection = dataSource.getConnection();
			try {
				sqlFile.setConnection(connection);
				sqlFile.execute();
				connection.commit();
			} finally {
				connection.close();
			}
		} catch (final IOException exception) {
			logError(CANNOT_READ_SOURCE_FILE, exception, source.getPath());
		} catch (final SQLException exception) {
			logError(ERROR_PROCESSING_SOURCE_FILE, exception, source.getPath());
		} catch (final SqlToolError exception) {
			logError(ERROR_PROCESSING_SOURCE_FILE, exception, source.getPath());
		}
	}

	/**
	 * Load a DBUnit XML data set into the in-memory database.
	 * 
	 * @param dataSource
	 *            The data source used to connect to the in-memory database.
	 * @param source
	 *            The source file containing the DBUnit XML data set.
	 * @throws MojoFailureException
	 *             If there was an error loading the DBUnit XML data set.
	 */
	private void loadDBUnitXML(final DataSource dataSource, final File source)
			throws MojoFailureException {
		try {
			final FileInputStream inputStream = new FileInputStream(source);
			final IDataSet dataSet = new XmlDataSet(inputStream);
			final IDatabaseConnection connection = new DatabaseDataSourceConnection(
					dataSource);
			try {
				DatabaseOperation.INSERT.execute(connection, dataSet);
			} finally {
				connection.close();
			}
		} catch (final SQLException exception) {
			logError(ERROR_PROCESSING_SOURCE_FILE, exception, source.getPath());
		} catch (final DatabaseUnitException exception) {
			logError(ERROR_PROCESSING_SOURCE_FILE, exception, source.getPath());
		} catch (final FileNotFoundException exception) {
			logError(CANNOT_READ_SOURCE_FILE, exception, source.getPath());
		}
	}
}