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

package com.btmatthews.maven.plugins.inmemdb.mojo;

import org.apache.maven.plugin.MojoFailureException;

import com.btmatthews.maven.plugins.inmemdb.Database;
import com.btmatthews.maven.plugins.inmemdb.Source;

/**
 * This plug-in Mojo loads data and scripts into an In Memory Database.
 * 
 * @goal load
 * 
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public final class LoadnMemDBMojo extends AbstractInMemDBMojo {

	/**
	 * The source files used to populate the database.
	 * 
	 * @parameter
	 * @required
	 */
	private Source[] sources;

	/**
	 * The default constructor.
	 */
	public LoadnMemDBMojo() {
	}

	/**
	 * Execute the Maven goal by iterating over the list of sources and loading
	 * each one.
	 * 
	 * @throws MojoFailureException
	 *             If there was an error executing the goal.
	 */
	public void execute() throws MojoFailureException {
		final Database database = getDatabase();
		if (database != null) {
			if (sources != null) {
				for (final Source source : sources) {
					database.load(this, source);
				}
			}
		}
	}
}