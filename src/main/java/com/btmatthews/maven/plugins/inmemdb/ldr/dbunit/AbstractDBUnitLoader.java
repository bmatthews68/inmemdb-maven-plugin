/*
 * Copyright 2011-2012 Brian Matthews
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

package com.btmatthews.maven.plugins.inmemdb.ldr.dbunit;

import java.io.IOException;
import java.sql.SQLException;

import com.btmatthews.maven.plugins.inmemdb.Database;
import com.btmatthews.maven.plugins.inmemdb.MessageUtil;
import com.btmatthews.maven.plugins.inmemdb.Source;
import com.btmatthews.maven.plugins.inmemdb.ldr.AbstractLoader;
import com.btmatthews.utils.monitor.Logger;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;

/**
 * Abstract base class for loaders that use DBUnit to load data sets.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public abstract class AbstractDBUnitLoader extends AbstractLoader {

    /**
     * Load a DBUnit data set.
     *
     * @param source The source file containing the DBUnit data set.
     * @return The DBUnit data set.
     * @throws DataSetException If there was an error loading the DBUnit data set.
     * @throws IOException      IF there was error reading the file containing the DBUnit
     *                          data set.
     */
    protected abstract IDataSet loadDataSet(Source source)
            throws DataSetException, IOException;

    /**
     * Load a DBUnit data set into the in-memory database.
     *
     * @param logger   Used to report errors and raise exceptions.
     * @param database The in-memory database.
     * @param source   The source file containing the data set.
     */
    public final void load(final Logger logger, final Database database,
                           final Source source) {
        assert database != null;
        assert logger != null;
        assert source != null;

        try {
            final IDataSet dataSet = loadDataSet(source);
            final IDatabaseConnection connection = new DatabaseDataSourceConnection(
                    database.getDataSource());
            final Boolean qualifiedTableNames = source.getQualifiedTableNames();
            if (qualifiedTableNames != null) {
                final DatabaseConfig config = connection.getConfig();
                config.setProperty(
                        DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES,
                        source.getQualifiedTableNames());
            }
            try {
                DatabaseOperation.INSERT.execute(connection, dataSet);
            } finally {
                connection.close();
            }
        } catch (final SQLException exception) {
            final String message = MessageUtil.getMessage(ERROR_PROCESSING_SOURCE_FILE, source.getSourceFile());
            logger.logError(message, exception);
         } catch (final DatabaseUnitException exception) {
            final String message = MessageUtil.getMessage(ERROR_PROCESSING_SOURCE_FILE, source.getSourceFile());
            logger.logError(message, exception);
        } catch (final IOException exception) {
            final String message = MessageUtil.getMessage(CANNOT_READ_SOURCE_FILE, source.getSourceFile());
            logger.logError(message, exception);
        }
    }

}