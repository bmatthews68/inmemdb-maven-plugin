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

import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

import com.btmatthews.maven.plugins.inmemdb.DatabaseFactory;

/**
 * Unit tests for loaders using an in-memory HSQL database.
 * 
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public final class TestHSQLDBShutdown extends AbstractTest {

	/**
	 * Get the database type.
	 * 
	 * @return {@link DatabaseFactory#TYPE_HSQLDB}
	 */
	protected String getDatabaseType() {
		return DatabaseFactory.TYPE_HSQLDB;
	}

	/**
	 * Verify that the database shutdown operation works.
	 * 
	 * @throws MojoFailureException
	 *             If there was an error.
	 */
	@Test
	public void testShutdown() throws MojoFailureException {
		getDatabase().start(getLogger());
		getDatabase().shutdown(getLogger());
	}
}
