/*
 * Copyright 2008-2011 Brian Thomas Matthews
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

package com.btmatthews.maven.plugins.inmemdb.test;

import junit.framework.TestCase;

import org.apache.maven.plugin.Mojo;

import com.btmatthews.maven.plugins.inmemdb.DatabaseFactory;

/**
 * Unit tests for the plug-in mojo's.
 * 
 * @author <a href="mailto:brian.matthews@btmatthews.com">Brian Matthews</a>
 * @version 1.0
 */
public final class TestMojos extends AbstractMojoTest {

	/**
	 * The database name.
	 */
	private static final String DATABASE_NAME = "test";

	/**
	 * The user name used to access the database.
	 */
	private static final String USERNAME = "sa";

	/**
	 * The password used to access the database.
	 */
	private static final String PASSWORD = null;

	/**
	 * The name of the goal that starts the database server.
	 */
	private static final String START_GOAL = "start";

	/**
	 * The name of the goal that stops the database server.
	 */
	private static final String STOP_GOAL = "stop";

	/**
	 * The delay between the start and stop goals.
	 */
	private static final int START_DELAY = 500;

	/**
	 * The Mojo parameter that specifies the database type.
	 */
	private static final String TYPE_PARAM = "type";

	/**
	 * The Mojo parameter that specifies the database name.
	 */
	private static final String DATABASE_PARAM = "database";

	/**
	 * The Mojo parameter that specifies the user name used to access the
	 * database.
	 */
	private static final String USERNAME_PARAM = "username";

	/**
	 * The Mojo parameter that specifies the password used to access the
	 * database.
	 */
	private static final String PASSWORD_PARAM = "password";

	/**
	 * The default constructor.
	 */
	public TestMojos() {
	}

	/**
	 * Test the configuration for the start and stop goals.
	 * 
	 * @throws Exception
	 *             If something unexpected happens.
	 */
	public void testHSQLDB() throws Exception {
		runTest(getName(), DatabaseFactory.TYPE_HSQLDB);
	}

	/**
	 * Test the configuration for the start and stop goals.
	 * 
	 * @throws Exception
	 *             If something unexpected happens.
	 */
	public void testDerby() throws Exception {
		Mojo mojo = this.getMojo(getName(), START_GOAL);
		TestCase.assertNotNull(mojo);
		assertEquals(mojo, TYPE_PARAM, DatabaseFactory.TYPE_DERBY);
		assertEquals(mojo, DATABASE_PARAM, DATABASE_NAME);
		assertEquals(mojo, USERNAME_PARAM, USERNAME);
		assertEquals(mojo, PASSWORD_PARAM, PASSWORD);
		mojo.execute();

		mojo = this.getMojo(getName(), STOP_GOAL);
		TestCase.assertNotNull(mojo);
		assertEquals(mojo, TYPE_PARAM, DatabaseFactory.TYPE_DERBY);
		assertEquals(mojo, DATABASE_PARAM, DATABASE_NAME);
		assertEquals(mojo, USERNAME_PARAM, USERNAME);
		assertEquals(mojo, PASSWORD_PARAM, PASSWORD);
		mojo.execute();
	}

	/**
	 * Test the configuration for the start and stop goals.
	 * 
	 * @throws Exception
	 *             If something unexpected happens.
	 */
	public void testH2() throws Exception {
		runTest(getName(), DatabaseFactory.TYPE_H2);
	}

	/**
	 * Test helper used to verify starting and stopping a database.
	 * 
	 * @param testName
	 *            The test name.
	 * @param databaseType
	 *            The database type.
	 * @throws Exception
	 *             If there is a problem stopping or starting the database.
	 */
	public void runTest(final String testName, final String databaseType)
			throws Exception {
		final Mojo startMojo = this.getMojo(testName, START_GOAL);
		TestCase.assertNotNull(startMojo);
		assertEquals(startMojo, TYPE_PARAM, databaseType);
		assertEquals(startMojo, DATABASE_PARAM, DATABASE_NAME);
		assertEquals(startMojo, USERNAME_PARAM, USERNAME);
		assertEquals(startMojo, PASSWORD_PARAM, PASSWORD);

		final Mojo stopMojo = this.getMojo(testName, STOP_GOAL);
		TestCase.assertNotNull(stopMojo);
		assertEquals(stopMojo, TYPE_PARAM, databaseType);
		assertEquals(stopMojo, DATABASE_PARAM, DATABASE_NAME);
		assertEquals(stopMojo, USERNAME_PARAM, USERNAME);
		assertEquals(stopMojo, PASSWORD_PARAM, PASSWORD);

		MojoStarter starter = new MojoStarter(startMojo);
		Thread serverThread = new Thread(starter);
		serverThread.start();
		Thread.sleep(START_DELAY);
		if (starter.getError() != null) {
			throw starter.getError();
		}
		stopMojo.execute();
	}

	/**
	 * {@link Runnable} that runs the start Mojo and captures any exceptions.
	 */
	static class MojoStarter implements Runnable {

		/**
		 * The start Mojo.
		 */
		private Mojo startMojo;

		/**
		 * An exception that was captured.
		 */
		private Exception error;

		/**
		 * The constructor.
		 * 
		 * @param mojo
		 *            The start mojo.
		 */
		public MojoStarter(final Mojo mojo) {
			startMojo = mojo;
		}

		/**
		 * Return the exception that was captured.
		 * 
		 * @return An exception or null.
		 */
		public Exception getError() {
			return error;
		}

		/**
		 * The run method.
		 */
		public void run() {
			try {
				startMojo.execute();
			} catch (final Exception exception) {
				error = exception;
			}
		}
	}

}
