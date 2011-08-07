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

import org.apache.maven.plugin.MojoFailureException;

/**
 * Abstract base classes for database objects providing an implentation of the
 * {@link load()} operation that is used to load data into or execute scripts
 * against the database.
 * 
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public abstract class AbstractDatabase implements Database {

	/**
	 * The message key for the error reported when a file type is not supported.
	 */
	private static final String UNSUPPORTED_FILE_TYPE = "unsupported_file_type";

	/**
	 * The message key for the error reported when a server cannot be stopped.
	 */
	protected static final String ERROR_STOPPING_SERVER = "error_stopping_server";

	/**
	 * Get the loaders that are supported for loading data or executing scripts.
	 * 
	 * @return Returns an array of loaders.
	 */
	protected abstract Loader[] getLoaders();

	/**
	 * Find the loader that supports the source file and use it to load the data
	 * into or execute the script against the database.
	 * 
	 * @param logger
	 *            Used to report errors and raise exceptions.
	 * @param source
	 *            The source file containing data or script.
	 * @throws MojoFailureException
	 *             If there was an error loading the data or executing the
	 *             script or if the source file type was not supported.
	 */
	public final void load(final Logger logger, final File source)
			throws MojoFailureException {
		if (source == null) {
			logger.logError(UNSUPPORTED_FILE_TYPE, "null");
		} else {
			final Loader[] loaders = getLoaders();
			int i = 0;
			while (i < loaders.length) {
				if (loaders[i].isSupported(source)) {
					loaders[i].load(logger, this, source);
					break;
				}
				++i;
			}
			if (i >= loaders.length) {
				logger.logError(UNSUPPORTED_FILE_TYPE, source.getPath());
			}
		}
	}
}
