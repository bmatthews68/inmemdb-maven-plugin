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

package com.btmatthews.maven.plugins.inmemdb.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.btmatthews.maven.plugins.inmemdb.Database;
import com.btmatthews.maven.plugins.inmemdb.DatabaseFactory;
import com.btmatthews.maven.plugins.inmemdb.db.derby.DerbyDatabase;
import com.btmatthews.maven.plugins.inmemdb.db.h2.H2Database;
import com.btmatthews.maven.plugins.inmemdb.db.hsqldb.HSQLDBDatabase;

/**
 * Unit test the {@link DatabaseFactory} class.
 * 
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public final class TestDatabaseFactory {

	/**
	 * The test database name.
	 */
	private static final String DATABASE = "test";

	/**
	 * The user name for the test database.
	 */
	private static final String USERNAME = "sa";

	/**
	 * The password for the test database.
	 */
	private static final String PASSWORD = "";

	/**
	 * The database factory that is tested.
	 */
	private DatabaseFactory databaseFactory;

	/**
	 * Create the database factory test fixture.
	 */
	@Before
	public void setUp() {
		databaseFactory = new DatabaseFactory();
	}

	/**
	 * Verify that the database factory returns null when null is passed as the
	 * database type.
	 */
	@Test
	public void testNullType() {
		final Database database = databaseFactory.createDatabase(null,
				DATABASE, USERNAME, PASSWORD);
		assertNull(database);
	}

	/**
	 * Verify that the database factory returns a {@link DerbyDatabase} when
	 * &quot;derby&quot; is passed as the database type.
	 */
	@Test
	public void testDerbyType() {
		final Database database = databaseFactory.createDatabase("derby",
				DATABASE, USERNAME, PASSWORD);
		assertNotNull(database);
		assertTrue(database instanceof DerbyDatabase);
	}

	/**
	 * Verify that the database factory returns a {@link H2Database} when
	 * &quot;h2&quot; is passed as the database type.
	 */
	@Test
	public void testH2Type() {
		final Database database = databaseFactory.createDatabase("h2",
				DATABASE, USERNAME, PASSWORD);
		assertNotNull(database);
		assertTrue(database instanceof H2Database);
	}

	/**
	 * Verify that the database factory returns a {@link HSQLDBDatabase} when
	 * &quot;hsql&quot; is passed as the database type.
	 */
	@Test
	public void testHSQLDBType() {
		final Database database = databaseFactory.createDatabase("hsqldb",
				DATABASE, USERNAME, PASSWORD);
		assertNotNull(database);
		assertTrue(database instanceof HSQLDBDatabase);
	}

	/**
	 * Verify that the database factory returns null when an supported value is
	 * passed as the database type.
	 */
	@Test
	public void testUnknownType() {
		final Database database = databaseFactory.createDatabase("mysql",
				DATABASE, USERNAME, PASSWORD);
		assertNull(database);
	}
}
