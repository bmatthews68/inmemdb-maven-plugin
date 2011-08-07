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

package com.btmatthews.maven.plugins.inmemdb.ldr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.apache.maven.plugin.MojoFailureException;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.operation.DatabaseOperation;

import com.btmatthews.maven.plugins.inmemdb.Database;
import com.btmatthews.maven.plugins.inmemdb.Logger;

/**
 * Loader that loads data from a DBUnit XML data set.
 * 
 * @author <a href="brian@btmatthews.com">Brian Matthews</a>
 * @versin 1.0.0
 */
public final class DBUnitXMLLoader extends AbstractLoader {

	/**
	 * The file extension for DBUnit XL data sets.
	 */
	private static final String EXT = ".dbunit.xml";

	/**
	 * Return the file extension that denotes DBUnit XML data sets.
	 * 
	 * @return Returns {@link EXT}.
	 */
	protected String getExtension() {
		return EXT;
	}

	/**
	 * Load a DBUnit XML data set into the in-memory database.
	 * 
	 * @param logger
	 *            Used to report errors and raise exceptions.
	 * @param dataSource
	 *            The data source used to connect to the in-memory database.
	 * @param source
	 *            The source file containing the DBUnit XML data set.
	 * @throws MojoFailureException
	 *             If there was an error loading the DBUnit XML data set.
	 */
	public void load(final Logger logger, final Database database,
			final File source) throws MojoFailureException {
		assert database != null;
		assert logger != null;
		assert source != null;

		try {
			final FileInputStream inputStream = new FileInputStream(source);
			final IDataSet dataSet = new XmlDataSet(inputStream);
			final IDatabaseConnection connection = new DatabaseDataSourceConnection(
					database.getDataSource());
			try {
				DatabaseOperation.INSERT.execute(connection, dataSet);
			} finally {
				connection.close();
			}
		} catch (final SQLException exception) {
			logger.logError(ERROR_PROCESSING_SOURCE_FILE, exception,
					source.getPath());
		} catch (final DatabaseUnitException exception) {
			logger.logError(ERROR_PROCESSING_SOURCE_FILE, exception,
					source.getPath());
		} catch (final FileNotFoundException exception) {
			logger.logError(CANNOT_READ_SOURCE_FILE, exception,
					source.getPath());
		}
	}

}
