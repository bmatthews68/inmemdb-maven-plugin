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
import org.junit.Before;

/**
 * Abstract base class for unit tests.
 * 
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0
 * 
 */
public abstract class AbstractTest implements Logger {

    /**
     * A test fixture containing the database object being tested.
     */
    private Database database;

    /**
     * Prepare for each unit test creating the database descriptor.
     * 
     * @throws MojoFailureException
     *             If there was a problem creating the database descriptor.
     */
    @Before
    public void setUp() throws MojoFailureException {
        final DatabaseFactory factory = new DatabaseFactory();
        database = factory.createDatabase(getDatabaseType(), "test", "sa", "");
    }

    /**
     * Returns a string that identifies the database type.
     * 
     * @return The database type.
     */
    protected abstract String getDatabaseType();

    /**
     * Get the database descriptor test fixture.
     * 
     * @return The database descriptor.
     */
    protected final Database getDatabase() {
        return database;
    }

    /**
     * Get the mock logger.
     * 
     * @return The mock logger.
     */
    protected final Logger getLogger() {
        return this;
    }

    /**
     * Raise a {@link MojoFailureException} but do not write to the log when an error occurs.
     * 
     * 
     * @param messageKey
     *            The message key in the resource bundle.
     * @param arguments
     *            The arguments.
     * @throws MojoFailureException
     *             Propagates the error as an exception.
     */
    public final void logError(final String messageKey, final Object... arguments)
            throws MojoFailureException {
        throw new MojoFailureException(messageKey);
    }

    /**
     * Raise a {@link MojoFailureException} but do not write to the log when an error occurs.
     * 
     * @param messageKey
     *            The key of the message in the resource bundle.
     * @param exception
     *            The exception.
     * @param arguments
     *            The message arguments.
     * @throws MojoFailureException
     *             Propagates the error as an exception encapsulating the original exception.
     */
    public final void logError(final String messageKey, final Throwable exception,
            final Object... arguments) throws MojoFailureException {
        throw new MojoFailureException(messageKey, exception);
    }
}
