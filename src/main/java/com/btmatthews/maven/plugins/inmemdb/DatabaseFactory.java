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

import com.btmatthews.maven.plugins.inmemdb.db.hsqldb.DerbyDatabase;
import com.btmatthews.maven.plugins.inmemdb.db.hsqldb.HSQLDBDatabase;

/**
 * The factory that creates database objects.
 * 
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public class DatabaseFactory {

	/**
	 * The database type code for HSQLDB databases.
	 */
	public static final String TYPE_HSQLDB = "hsqldb";

	/**
	 * The database type code for Apache Derby databases.
	 */
	public static final String TYPE_DERBY = "derby";

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
	public Database createDatabase(String type, String databaseName,
			String username, String password) {
		Database database;
		if (TYPE_HSQLDB.equals(type)) {
			database = new HSQLDBDatabase(databaseName, username, password);
		} else if (TYPE_DERBY.equals(type)) {
				database = new DerbyDatabase(databaseName, username, password);
		} else {
			database = null;
		}
		return database;
	}
}
