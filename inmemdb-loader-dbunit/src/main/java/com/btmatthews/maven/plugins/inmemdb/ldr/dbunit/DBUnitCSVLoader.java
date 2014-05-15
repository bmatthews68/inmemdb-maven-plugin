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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.btmatthews.maven.plugins.inmemdb.Source;
import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.csv.CsvDataSetWriter;
import org.dbunit.dataset.csv.CsvParser;
import org.dbunit.dataset.csv.CsvParserImpl;
import org.dbunit.dataset.datatype.DataType;

/**
 * Loader that loads data from a DBUnit CSV data set.
 *
 * @author <a href="brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public final class DBUnitCSVLoader extends AbstractDBUnitLoader {

    /**
     * The file extension for DBUnit CSV data set files.
     */
    private static final String EXT = ".csv";

    /**
     * Get the file extension for DBUnit CSV data set files.
     *
     * @return {@link #EXT}
     */
    @Override
    protected String getExtension() {
        return EXT;
    }

    /**
     * Load a DBUnit CSV data set. This implementation was lifted from
     * {@link org.dbunit.dataset.csv.CsvProducer#produceFromFile(java.io.File)}.
     *
     * @param source The source file containing the DBUnit CSV data set.
     * @return The DBUnit CSV data set.
     * @throws DataSetException If there was an error loading the DBUnit CSV data set.
     * @throws IOException      If there was an error reading the DBUnit CSV data set from the file.
     */
    @SuppressWarnings("rawtypes")
    @Override
    protected IDataSet loadDataSet(final Source source) throws DataSetException,
            IOException {
        final int dotPos = source.getSourceFile().lastIndexOf("");
        final int slashPos = source.getSourceFile().lastIndexOf("/");
        final CsvParser parser = new CsvParserImpl();
        final List readData;
        final String tableName;
        if (source.getSourceFile().startsWith(CLASSPATH_PREFIX)) {
            final URL url = getClass().getResource(source.getSourceFile().substring(CLASSPATH_PREFIX_LENGTH));
            readData = parser.parse(url);
            if (slashPos == -1) {
                tableName = source.getSourceFile().substring(CLASSPATH_PREFIX_LENGTH, dotPos);
            } else {
                tableName = source.getSourceFile().substring(slashPos + 1, dotPos);
            }
        } else {
            final File sourceFile = new File(source.getSourceFile());
            readData = parser.parse(sourceFile);
            if (slashPos == -1) {
                tableName = source.getSourceFile().substring(0, dotPos);
            } else {
                tableName = source.getSourceFile().substring(slashPos + 1, dotPos);
            }
        }

        final CachedDataSet dataSet = new CachedDataSet();
        dataSet.startDataSet();
        final List readColumns = ((List)readData.get(0));
        final Column[] columns = new Column[readColumns.size()];
        for (int i = 0; i < readColumns.size(); i++) {
            columns[i] = new Column((String)readColumns.get(i), DataType.UNKNOWN);
        }

        final ITableMetaData metaData = new DefaultTableMetaData(tableName, columns);
        dataSet.startTable(metaData);
        for (int rowIndex = 1; rowIndex < readData.size(); rowIndex++) {
            final List row = (List)readData.get(rowIndex);
            final Object[] values = row.toArray();
            for (int columnIndex = 0; columnIndex < values.length; columnIndex++) {
                if (values[columnIndex].equals(CsvDataSetWriter.NULL)) {
                    values[columnIndex] = null;
                }
            }
            dataSet.row(values);
        }
        dataSet.endTable();
        dataSet.endDataSet();
        return dataSet;
    }
}
