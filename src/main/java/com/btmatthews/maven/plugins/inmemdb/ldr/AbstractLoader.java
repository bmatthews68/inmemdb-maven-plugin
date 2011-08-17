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
import java.util.Locale;

import org.apache.maven.plugin.MojoFailureException;

import com.btmatthews.maven.plugins.inmemdb.Loader;
import com.btmatthews.maven.plugins.inmemdb.Logger;
import com.btmatthews.maven.plugins.inmemdb.Source;

/**
 * Abstract base class for loaders that implements the {@link
 * Loader.isSupported()} method.
 * 
 * @author <a href="mailto:brian@btmathews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public abstract class AbstractLoader implements Loader {

	/**
	 * The message key for the error reported when an error occurs validating a
	 * file.
	 */
	protected static final String CANNOT_VALIDATE_FILE = "cannot_validate_file";

	/**
	 * Get the extension of the files that the loader will support.
	 * 
	 * @return The file extension.
	 */
	protected abstract String getExtension();

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
	 *             If there was an error checking that the content is a data or
	 *             script that is supported by the loader. If there was an error
	 *             loading the DBUnit XML data set.
	 */
	protected boolean hasValidContent(final Logger logger, final Source source)
			throws MojoFailureException {
		return true;
	}

	/**
	 * Determine whether or not the data or script can be loaded or executed.
	 * 
	 * @param logger
	 *            Used to report errors and raise exceptions.
	 * @param source
	 *            The source file containing the data or script.
	 * @return <ul>
	 *         <li><code>true</code> if the data or script can be loaded or
	 *         executed.</li>
	 *         <li><code>false</code>if the data or script cannot be loaded or
	 *         executed.</li>
	 *         </ul>
	 * @throws MojoFailureException
	 *             If there was an error checking that the data or script is
	 *             supported by the loader.
	 */
	public final boolean isSupported(final Logger logger, final Source source)
			throws MojoFailureException {
		boolean result;
		if (source != null) {
			final File sourceFile = source.getSourceFile();
			if (sourceFile != null && sourceFile.isFile()) {
				final Locale locale = Locale.getDefault();
				final String name = sourceFile.getAbsolutePath().toLowerCase(
						locale);
				result = name.endsWith(getExtension())
						&& hasValidContent(logger, source);
			} else {
				result = false;
			}
		} else {
			result = false;
		}
		return result;
	}
}
