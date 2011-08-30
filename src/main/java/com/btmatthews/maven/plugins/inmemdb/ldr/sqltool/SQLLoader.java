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

package com.btmatthews.maven.plugins.inmemdb.ldr.sqltool;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.maven.plugin.MojoFailureException;
import org.hsqldb.cmdline.SqlFile;
import org.hsqldb.cmdline.SqlToolError;

import com.btmatthews.maven.plugins.inmemdb.Database;
import com.btmatthews.maven.plugins.inmemdb.Logger;
import com.btmatthews.maven.plugins.inmemdb.Source;
import com.btmatthews.maven.plugins.inmemdb.ldr.AbstractLoader;

/**
 * Loader that uses HSQLDB&apos;s {@link SqlFile} to load a DDL/DML script into
 * the database.
 * 
 * @author <a href="brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public final class SQLLoader extends AbstractLoader {

	/**
	 * The file extension for DDL/DML scripts.
	 */
	private static final String EXT = ".sql";

	/**
	 * Return the file extension that denotes DBUnit XML data sets.
	 * 
	 * @return Returns {@link SQLLoader#EXT}.
	 */
	public String getExtension() {
		return EXT;
	}

	/**
	 * Load a DDL/DML script into the in-memory database.
	 * 
	 * @param logger
	 *            Used to report errors and raise exceptions.
	 * @param database
	 *            The in-memory database.
	 * @param source
	 *            The source file containing the DDL/DML script.
	 * @throws MojoFailureException
	 *             If there was an error loading the DDL/DML script.
	 */
	public void load(final Logger logger, final Database database,
			final Source source) throws MojoFailureException {
		try {
			final SqlFile sqlFile = new SqlFile(source.getSourceFile());
			final DataSource dataSource = database.getDataSource();
			final Connection connection = dataSource.getConnection();
			try {
				sqlFile.setConnection(connection);
				sqlFile.execute();
			} finally {
				connection.close();
			}
		} catch (final IOException exception) {
			logger.logErrorAndThrow(CANNOT_READ_SOURCE_FILE, exception, source
					.getSourceFile().getPath());
		} catch (final SQLException exception) {
			logger.logErrorAndThrow(ERROR_PROCESSING_SOURCE_FILE, exception, source
					.getSourceFile().getPath());
		} catch (final SqlToolError exception) {
			logger.logErrorAndThrow(ERROR_PROCESSING_SOURCE_FILE, exception, source
					.getSourceFile().getPath());
		}
	}
}
