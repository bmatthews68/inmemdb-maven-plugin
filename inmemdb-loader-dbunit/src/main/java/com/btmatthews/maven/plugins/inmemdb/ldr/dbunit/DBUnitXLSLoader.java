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
import java.io.InputStream;

import com.btmatthews.maven.plugins.inmemdb.Source;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.excel.XlsDataSet;

/**
 * Loader that loads data from a DBUnit Excel data set.
 *
 * @author <a href="brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public final class DBUnitXLSLoader extends AbstractDBUnitLoader {

    /**
     * The file extension for DBUnit Excel data set files.
     */
    private static final String EXT = ".xls";

    /**
     * Get the file extension for DBUnit Excel data set files.
     *
     * @return {@link #EXT}
     */
    @Override
    protected String getExtension() {
        return EXT;
    }

    /**
     * Load a DBUnit Excel data set.
     *
     * @param source The source file containing the DBUnit Excel data set.
     * @return The DBUnit Excel data set.
     * @throws DataSetException If there was an error loading the DBUnit Excel data set.
     * @throws IOException      If there was an error reading the DBUnit Excel data set from the file.
     */
    @Override
    protected IDataSet loadDataSet(final Source source) throws DataSetException,
            IOException {
        if (source.getSourceFile().startsWith("classpath:")) {
            final InputStream in = getClass().getResourceAsStream(source.getSourceFile().substring(10));
            return new XlsDataSet(in);
        } else {
            final File file = new File(source.getSourceFile());
            return new XlsDataSet(file);
        }

    }
}
