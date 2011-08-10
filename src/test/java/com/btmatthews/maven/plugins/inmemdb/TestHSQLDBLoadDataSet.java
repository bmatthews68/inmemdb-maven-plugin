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
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for loaders using an in-memory HSQLDB database.
 * 
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public final class TestHSQLDBLoadDataSet extends AbstractTestLoad {

	@Before
	public void setUp() throws MojoFailureException {
		super.setUp();
		final File source = new File("src/test/resources/create_database.sql");
		getDatabase().load(getLogger(), source);
	}

	/**
	 * Get the database type.
	 * 
	 * @return {@link DatabaseFactory.TYPE_HSQLDB}
	 */
	protected String getDatabaseType() {
		return DatabaseFactory.TYPE_HSQLDB;
	}

	/**
	 * Verify that a valid DBUnit XML data set can be loaded.
	 * 
	 * @throws MojoFailureException
	 *             If there was an error.
	 */
	@Test
	public void testLoadDBUnitXML() throws MojoFailureException {
		final File source = new File("src/test/resources/users.dbunit.xml");
		getDatabase().load(getLogger(), source);
	}

	/**
	 * Verify that a valid DBUinit Flat XML data set can be loaded.
	 * 
	 * @throws MojoFailureException
	 *             If there was an error.
	 */
	@Test
	public void testLoadFlatDBUnitXML() throws MojoFailureException {
		final File source = new File("src/test/resources/users_flat.dbunit.xml");
		getDatabase().load(getLogger(), source);
	}
}
