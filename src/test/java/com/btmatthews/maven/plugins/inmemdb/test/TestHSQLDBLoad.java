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
import org.junit.Test;

import com.btmatthews.maven.plugins.inmemdb.DatabaseFactory;
import com.btmatthews.maven.plugins.inmemdb.mojo.Script;

/**
 * Unit tests for loaders using an in-memory HSQLDB database.
 * 
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public final class TestHSQLDBLoad extends AbstractTestLoad {

	/**
	 * Get the database type.
	 * 
	 * @return {@link DatabaseFactory#TYPE_HSQLDB}
	 */
	protected String getDatabaseType() {
		return DatabaseFactory.TYPE_HSQLDB;
	}

	/**
	 * Verify that a valid DDL/DML script can be loaded.
	 * 
	 * @throws MojoFailureException
	 *             If there was an error.
	 */
	@Test
	public void testLoadScript() throws MojoFailureException {
		final Script source = new Script();
		source.setSourceFile(new File("src/test/resources/create_database.sql"));
		getDatabase().load(getLogger(), source);
	}

	/**
	 * Verify than an exception is thrown when the script does not exist.
	 * 
	 * @throws MojoFailureException
	 *             If there was an error.
	 */
	@Test(expected = MojoFailureException.class)
	public void testLoadNonExistantScript() throws MojoFailureException {
		final Script source = new Script();
		source.setSourceFile(new File("src/test/resources/create_database1.sql"));
		getDatabase().load(getLogger(), source);
	}

	/**
	 * Verify than an exception is thrown when the script is invalid.
	 * 
	 * @throws MojoFailureException
	 *             If there was an error.
	 */
	@Test(expected = MojoFailureException.class)
	public void testLoadInvalidScript() throws MojoFailureException {
		final Script source = new Script();
		source.setSourceFile(new File("src/test/resources/create_database2.sql"));
		getDatabase().load(getLogger(), source);
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
		getDatabase().load(getLogger(), null);
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
		final Script source = new Script();
		source.setSourceFile(new File("src/test/resources"));
		getDatabase().load(getLogger(), source);
	}
}
