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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for loaders using an in-memory HSQL database.
 * 
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public class TestHSQLDBLoad {

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
		database = factory.createDatabase(DatabaseFactory.TYPE_HSQLDB, ".",
				"sa", "");
	}

	/**
	 * Clean-up after each unit test by shutting down the database.
	 * 
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		database.shutdown(logger);
	}

	/**
	 * Verify that a valid DDL/DML script can be loaded.
	 * 
	 * @throws MojoFailureException
	 *             If there was an error.
	 */
	@Test
	public void testLoadScript() throws MojoFailureException {
		final File source = new File("src/test/resources/create_database.sql");
		database.load(logger, source);
	}

	/**
	 * Verify than an exception is thrown when the script does not exist.
	 * 
	 * @throws MojoFailureException
	 *             If there was an error.
	 */
	@Test(expected = MojoFailureException.class)
	public void testLoadNonExistantScript() throws MojoFailureException {
		final File source = new File("src/test/resources/create_database1.sql");
		database.load(logger, source);
	}

	/**
	 * Verify than an exception is thrown when the script is invalid.
	 * 
	 * @throws MojoFailureException
	 *             If there was an error.
	 */
	@Test(expected = MojoFailureException.class)
	public void testLoadInvalidScript() throws MojoFailureException {
		final File source = new File("src/test/resources/create_database2.sql");
		database.load(logger, source);
	}

	/**
	 * Verify than an exception is thrown when <code>null</code> is passed as
	 * the source file.
	 * 
	 * @throws MojoFailureException
	 *             If there was an error.
	 */
	@Test(expected = MojoFailureException.class)
	public void testLoadNull() throws MojoFailureException {
		database.load(logger, null);
	}

	/**
	 * Verify than an exception is thrown when the source file is actually a
	 * directory.
	 * 
	 * @throws MojoFailureException
	 *             If there was an error.
	 */
	@Test(expected = MojoFailureException.class)
	public void testLoadDirectory() throws MojoFailureException {
		final File source = new File("src/test/resources");
		database.load(logger, source);
	}
}
