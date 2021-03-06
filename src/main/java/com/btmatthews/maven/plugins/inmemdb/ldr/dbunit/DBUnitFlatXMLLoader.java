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

import com.btmatthews.maven.plugins.inmemdb.Source;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;

/**
 * Loader that loads data from a DBUnit XML data set.
 *
 * @author <a href="brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public final class DBUnitFlatXMLLoader extends AbstractDBUnitLoader {

    /**
     * The file extension for DBUnit Flat XML data set files.
     */
    private static final String EXT = ".xml";

    /**
     * Get the file extension for DBUnit Flat XML data set files.
     *
     * @return {@link #EXT}
     */
    @Override
    protected String getExtension() {
        return EXT;
    }

    /**
     * Load a DBUnit Flat XML data set.
     *
     * @param source The source file containing the DBUnit Flat XML data set.
     * @return The DBUnit Flat XML data set.
     * @throws DataSetException If there was an error loading the DBUnit Flat XML data set.
     * @throws IOException      If there was an error reading the DBUnit Flat XML data set from the file.
     */
    @Override
    protected IDataSet loadDataSet(final Source source) throws DataSetException,
            IOException {
        final FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        if (source.getSourceFile().startsWith("classpath:")) {
            final URL url = getClass().getResource(source.getSourceFile().substring(10));
            return builder.build(url);
        } else {
            final File file = new File(source.getSourceFile());
            return builder.build(file);
        }
    }
}
