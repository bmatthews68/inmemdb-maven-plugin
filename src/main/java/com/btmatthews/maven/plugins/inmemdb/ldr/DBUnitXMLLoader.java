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
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.maven.plugin.MojoFailureException;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.omg.CORBA.BooleanHolder;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.btmatthews.maven.plugins.inmemdb.Logger;

/**
 * Loader that loads data from a DBUnit XML data set.
 * 
 * @author <a href="brian@btmatthews.com">Brian Matthews</a>
 * @versin 1.0.0
 */
public final class DBUnitXMLLoader extends AbstractDBUnitLoader {

	/**
	 * Check the contents of the file to see if it is valid for this loader.
	 * 
	 * @param logger
	 *            Used to report errors and raise exceptions.
	 * @param source
	 *            The source data or script.
	 * @return <ul>
	 *         <li><code>true</code> if the content is valid.</li>
	 *         <li><code>false</code> if the content is invalid.</li>
	 *         </ul>
	 * @throws MojoFailureException
	 *             If there was an error loading the DBUnit XML data set.
	 */
	protected boolean hasValidContent(final Logger logger, final File source)
			throws MojoFailureException {
		boolean result;

		try {
			final DocumentBuilder parser = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			final ParserErrorHandler errorHandler = new ParserErrorHandler();
			
			parser.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(final String publicId,
						final String systemId) throws SAXException, IOException {
					final InputStream schemaStream = Thread
							.currentThread()
							.getContextClassLoader()
							.getResourceAsStream(
									"com/btmatthews/maven/plugins/inmemdb/xmldataset.xsd"
									);
					return new InputSource(schemaStream);
				}
			});
		
			parser.setErrorHandler(errorHandler);

			try {
				parser.parse(source);
				result = errorHandler.getResult();
			} catch (final SAXException exception) {
				result = false;
			}
		} catch (final Exception exception) {
			logger.logError(CANNOT_VALIDATE_FILE, exception, source.getPath());
			result = false;
		}
		return result;
	}

	/**
	 * Load a DBUnit XML data set.
	 * 
	 * @param logger
	 *            Used to report errors and raise exceptions.
	 * @param source
	 *            The source file containing the DBUnit XML data set.
	 * @throws MojoFailureException
	 *             If there was an error loading the DBUnit XML data set.
	 */
	protected IDataSet loadDataSet(final Logger logger, final File source)
			throws MojoFailureException {
		assert logger != null;
		assert source != null;

		IDataSet dataSet;
		try {
			final FileInputStream inputStream = new FileInputStream(source);
			dataSet = new XmlDataSet(inputStream);
		} catch (final DatabaseUnitException exception) {
			logger.logError(ERROR_PROCESSING_SOURCE_FILE, exception,
					source.getPath());
			dataSet = null;
		} catch (final FileNotFoundException exception) {
			logger.logError(CANNOT_READ_SOURCE_FILE, exception,
					source.getPath());
			dataSet = null;
		}
		return dataSet;
	}

	static final class ParserErrorHandler implements ErrorHandler {
		private boolean result;

		public ParserErrorHandler() {
			result = true;
		}

		public boolean getResult() {
			return result;
		}

		public void warning(final SAXParseException exception) throws SAXException {
			result = false;
		}

		public void error(final SAXParseException exception) throws SAXException {
			result = false;
		}

		public void fatalError(final SAXParseException exception) throws SAXException {
			result = false;
		}

	}
}
