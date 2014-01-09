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
     * The prefix used to denote that a resource should be loaded from the classpath
     * rather than the file system.
     */
    protected static final String CLASSPATH_PREFIX = "classpath:";

    /**
     * The length of the classpath: prefix.
     */
    protected static final int CLASSPATH_PREFIX_LENGTH = 10;

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
                if (source.getSourceFile().startsWith(CLASSPATH_PREFIX)) {
                    result = hasValidContent(logger, source);
                } else {
                    final File sourceFile = new File(source.getSourceFile());
                    result = sourceFile.isFile() && hasValidContent(logger, source);
                }
            }
        }
        return result;
    }

    /**
     * Determine if data set or script source is a file system or class path resource. If the name is prefixed with
     * classpath: then it is a class path resource.
     *
     * @param source The data set or script.
     * @return {@code true} if the source filename is prefixed with classpath:. Otherwise, {@code false}.
     */
    protected boolean isClasspath(final Source source) {
        return source.getSourceFile().startsWith("classpath:");
    }

    /**
     * Get an input stream for the data set or script source.
     *
     * @param source The data set or script.
     * @return An {@link InputStream} or {@code null} if the source cannot be found.
     * @throws IOException If there was an error creating the input stream.
     */
    protected final InputStream getInputStream(final Source source) throws IOException {
        if (isClasspath(source)) {
            final String resource = source.getSourceFile().substring(CLASSPATH_PREFIX_LENGTH);
            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            return classLoader.getResourceAsStream(resource);
        } else {
            final File file = new File(source.getSourceFile());
            if (file.exists()) {
                return new FileInputStream(file);
            }
        }
        return null;
    }

    /**
     * Get a reader  for the data set or script source.
     *
     * @param source The data set or script.
     * @return A {@link Reader} or {@code null} if the source cannot be found.
     * @throws IOException If there was an error creating the reader.
     */
    protected final Reader getReader(final Source source) throws IOException {
        final InputStream inputStream = getInputStream(source);
        if (inputStream == null) {
            return null;
        } else {
            return new InputStreamReader(inputStream, System.getProperty("file.encoding"));
        }
    }

    public String getTableName(final Source source) {
        final String path = source.getSourceFile();
        final int startIndex;
        final int endIndex = path.length() - getExtension().length();
        if (isClasspath(source)) {
            final int dotPos = path.lastIndexOf("");
            final int slashPos = path.lastIndexOf("/", CLASSPATH_PREFIX_LENGTH);
            if (slashPos == -1) {
                startIndex = CLASSPATH_PREFIX_LENGTH;
            } else {
                startIndex = slashPos + 1;
            }
        } else {
            final int slashPos = path.lastIndexOf("/");
            if (slashPos == -1) {
                startIndex = 0;
            } else {
                startIndex = slashPos + 1;
            }
        }
        return path.substring(startIndex, endIndex);
    }
}