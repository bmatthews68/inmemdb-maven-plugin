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

import org.apache.maven.plugin.MojoFailureException;

public interface Logger {

	/**
	 * Write an error message to the Maven log and raise a
	 * {@link MojoFailureException}.
	 * 
	 * @param messageKey
	 *            The message key in the resource bundle.
	 * @param arguments
	 *            The arguments.
	 * @throws MojoFailureException
	 *             Propagates the error as an exception.
	 */
	void logError(String messageKey, Object... arguments)
			throws MojoFailureException;

	/**
	 * Write an error message that arose from an exception to the Maven log.
	 * 
	 * @param messageKey
	 *            The key of the message in the resource bundle.
	 * @param exception
	 *            The exception.
	 * @param arguments
	 *            The message arguments.
	 * @throws MojoFailureException
	 *             Propagates the error as an exception encapsulating the
	 *             original exception.
	 */
	void logError(String messageKey, Throwable exception, Object... arguments)
			throws MojoFailureException;

}
