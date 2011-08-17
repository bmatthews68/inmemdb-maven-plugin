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

import java.io.File;

import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Test;

import com.btmatthews.maven.plugins.inmemdb.DatabaseFactory;
import com.btmatthews.maven.plugins.inmemdb.mojo.DataSet;
import com.btmatthews.maven.plugins.inmemdb.mojo.Script;

/**
 * Unit tests for loaders using an in-memory HSQLDB database.
 * 
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public final class TestHSQLDBLoadDataSet extends AbstractTestLoad {

	/**
	 * Prepare for test case execution by creating the database and loading a
	 * schema.
	 * 
	 * @throws MojoFailureException
	 *             If there was a problem creating the database or loading the
	 *             schema.
	 */
	@Before
	public void setUp() throws MojoFailureException {
		super.setUp();
		final Script source = new Script();
		source.setSourceFile(new File("src/test/resources/create_database.sql"));
		getDatabase().load(getLogger(), source);
	}

	/**
	 * Get the database type.
	 * 
	 * @return {@link DatabaseFactory#TYPE_HSQLDB}
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
		final DataSet source = new DataSet();
		source.setSourceFile(new File("src/test/resources/users.dbunit.xml"));
		getDatabase().load(getLogger(), source);
	}

	/**
	 * Verify that a valid DBUinit Flat XML data set can be loaded.
	 * 
	 * @throws MojoFailureException
	 *             If there was an error.
	 */
	@Test
	public void testLoadDBUnitFlatXML() throws MojoFailureException {
		final DataSet source = new DataSet();
		source.setSourceFile(new File("src/test/resources/users.xml"));
		getDatabase().load(getLogger(), source);
	}

	/**
	 * Verify that a valid DBUinit CSV data set can be loaded.
	 * 
	 * @throws MojoFailureException
	 *             If there was an error.
	 */
	@Test
	public void testLoadDBUnitCSV() throws MojoFailureException {
		final DataSet source = new DataSet();
		source.setSourceFile(new File("src/test/resources/users.csv"));
		getDatabase().load(getLogger(), source);
	}

	/**
	 * Verify that a valid DBUinit XLS data set can be loaded.
	 * 
	 * @throws MojoFailureException
	 *             If there was an error.
	 */
	@Test
	public void testLoadDBUnitXLS() throws MojoFailureException {
		final DataSet source = new DataSet();
		source.setSourceFile(new File("src/test/resources/users.xls"));
		getDatabase().load(getLogger(), source);
	}
}
