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

import com.btmatthews.maven.plugins.inmemdb.db.derby.DerbyDatabase;
import com.btmatthews.maven.plugins.inmemdb.db.h2.H2Database;
import com.btmatthews.maven.plugins.inmemdb.db.hsqldb.HSQLDBDatabase;

/**
 * The factory that creates database objects.
 * 
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public final class DatabaseFactory {

	/**
	 * The database type code for HSQLDB databases.
	 */
	public static final String TYPE_HSQLDB = "hsqldb";

	/**
	 * The database type code for Apache Derby databases.
	 */
	public static final String TYPE_DERBY = "derby";

	/**
	 * The database type code for H2 databases.
	 */
	public static final String TYPE_H2 = "h2";

	/**
	 * Create and return a database object corresponding to type and initialise
	 * it with the database name, user name and password. If the type is not
	 * supported then <code>null</code> will be returned.
	 * 
	 * @param type
	 *            The database type.
	 * @param databaseName
	 *            The database name.
	 * @param username
	 *            The user name used to connect to the database.
	 * @param password
	 *            The password used to connect to the database.
	 * @return The database object or <code>null</code> if the database type is
	 *         not supported.
	 */
	public Database createDatabase(final String type,
			final String databaseName, final String username,
			final String password) {
		Database database;
		if (TYPE_HSQLDB.equals(type)) {
			database = new HSQLDBDatabase(databaseName, username, password);
		} else if (TYPE_DERBY.equals(type)) {
			database = new DerbyDatabase(databaseName, username, password);
		} else if (TYPE_H2.equals(type)) {
			database = new H2Database(databaseName, username, password);
		} else {
			database = null;
		}
		return database;
	}
}
