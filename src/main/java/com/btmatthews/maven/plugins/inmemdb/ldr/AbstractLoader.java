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

import com.btmatthews.maven.plugins.inmemdb.Loader;

/**
 * Abstract base class for loaders that implements the {@link
 * Loader.isSupported()} method.
 * 
 * @author <a href="mailto:brian@btmathews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public abstract class AbstractLoader implements Loader {

	/**
	 * Get the extension of the files that the loader will support.
	 * 
	 * @return The file extension.
	 */
	protected abstract String getExtension();

	/**
	 * Determine whether or not the data or script can be loaded or executed.
	 * 
	 * @param source
	 *            The source file containing the data or script.
	 * @return <ul>
	 *         <li><code>true</code> if the data or script can be loaded or
	 *         executed.</li>
	 *         <li><code>false</code>if the data or script cannot be loaded or
	 *         executed.</li>
	 *         </ul>
	 */
	public final boolean isSupported(final File source) {
		boolean result;
		if (source != null && source.isFile()) {
			final Locale locale = Locale.getDefault();
			final String name = source.getAbsolutePath().toLowerCase(locale);
			result = name.endsWith(getExtension());
		} else {
			result = false;
		}
		return result;
	}
}
