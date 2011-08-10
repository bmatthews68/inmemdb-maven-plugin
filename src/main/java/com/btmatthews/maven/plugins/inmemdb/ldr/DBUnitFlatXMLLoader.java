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
import java.net.MalformedURLException;

import org.apache.maven.plugin.MojoFailureException;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;

import com.btmatthews.maven.plugins.inmemdb.Logger;

/**
 * Loader that loads data from a DBUnit XML data set.
 * 
 * @author <a href="brian@btmatthews.com">Brian Matthews</a>
 * @versin 1.0.0
 */
public final class DBUnitFlatXMLLoader extends AbstractDBUnitLoader {

	/**
	 * Check the contents of the file to see if it is valid for this loader.
	 * 
	 * @param source
	 *            The source data or script.
	 * @return <ul>
	 *         <li><code>true</code> if the content is valid.</li>
	 *         <li><code>false</code> if the content is invalid.</li>
	 *         </ul>
	 */
	protected boolean hasValidContent(final File source) {
		return true;
	}

	/**
	 * Load a DBUnit Flat XML data set.
	 * 
	 * @param logger
	 *            Used to report errors and raise exceptions.
	 * @param source
	 *            The source file containing the DBUnit Flat XML data set.
	 * @throws MojoFailureException
	 *             If there was an error loading the DBUnit Flat XML data set.
	 */
	protected IDataSet loadDataSet(final Logger logger, final File source)
			throws MojoFailureException {
		assert logger != null;
		assert source != null;

		IDataSet dataSet;
		try {
			final FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
			dataSet = builder.build(source);
		} catch (final MalformedURLException exception) {
			logger.logError(CANNOT_READ_SOURCE_FILE, exception,
					source.getPath());
			dataSet = null;
		} catch (final DataSetException exception) {
			logger.logError(ERROR_PROCESSING_SOURCE_FILE, exception,
					source.getPath());
			dataSet = null;
		}
		return dataSet;
	}

}
