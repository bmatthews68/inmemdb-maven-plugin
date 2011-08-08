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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Abstract base class for unit tests.
 * 
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0
 * 
 */
public abstract class AbstractTest {

	/**
	 * A test fixture containing the database object being tested.
	 */
	private Database database;

	/**
	 * Mocks the logger.
	 */
	@Mock
	private Logger logger;

	/**
	 * Prepare for each unit test by mock objects and test fixtures.
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Mockito.doThrow(new MojoFailureException("")).when(logger)
				.logError(Mockito.anyString(), Mockito.anyVararg());
		Mockito.doThrow(new MojoFailureException(""))
				.when(logger)
				.logError(Mockito.anyString(), Mockito.any(Throwable.class),
						Mockito.anyVararg());
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
		return logger;
	}
}
