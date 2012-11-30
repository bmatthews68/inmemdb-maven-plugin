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

package com.btmatthews.maven.plugins.inmemdb.ldr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;

import com.btmatthews.maven.plugins.inmemdb.Loader;
import com.btmatthews.maven.plugins.inmemdb.Source;
import com.btmatthews.utils.monitor.Logger;

/**
 * Abstract base class for loaders that implements the {@link
 * Loader#isSupported(com.btmatthews.utils.monitor.Logger, com.btmatthews.maven.plugins.inmemdb.Source)} method.
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
     * @param logger Used to report errors and raise exceptions.
     * @param source The source data or script.
     * @return <ul>
     *         <li><code>true</code> if the content is valid.</li>
     *         <li><code>false</code> if the content is invalid.</li>
     *         </ul>
     */
    protected boolean hasValidContent(final Logger logger, final Source source) {
        return true;
    }

    /**
     * Determine whether or not the data or script can be loaded or executed.
     *
     * @param logger Used to report errors and raise exceptions.
     * @param source The source file containing the data or script.
     * @return <ul>
     *         <li><code>true</code> if the data or script can be loaded or
     *         executed.</li>
     *         <li><code>false</code>if the data or script cannot be loaded or
     *         executed.</li>
     *         </ul>
     */
    public final boolean isSupported(final Logger logger, final Source source) {
        boolean result = false;
        if (source != null) {
            final Locale locale = Locale.getDefault();
            final String name = source.getSourceFile().toLowerCase(locale);
            if (name.endsWith(getExtension())) {
                if (source.getSourceFile().startsWith("classpath:")) {
                    result = hasValidContent(logger, source);
                } else {
                    final File sourceFile = new File(source.getSourceFile());
                    result = sourceFile.isFile() && hasValidContent(logger, source);
                }
            }
        }
        return result;
    }

    protected boolean isClasspath(final Source source) {
        return source.getSourceFile().startsWith("classpath:");
    }

    protected InputStream getInputStream(final Source source) throws IOException {
        if (isClasspath(source)) {
            return getClass().getResourceAsStream(source.getSourceFile().substring(10));
        } else {
            final File file = new File(source.getSourceFile());
            return new FileInputStream(file);
        }
    }

    protected Reader getReader(final Source source) throws IOException {
        if (isClasspath(source)) {
            final InputStream inputStream = getClass().getResourceAsStream(source.getSourceFile().substring(10));
            return new InputStreamReader(inputStream, System.getProperty("file.encoding"));
        } else {
            final File file = new File(source.getSourceFile());
            return new FileReader(file);
        }
    }
}
