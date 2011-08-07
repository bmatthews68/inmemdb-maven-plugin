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

/**
 * This plug-in Mojo starts an In Memory Database.
 * 
 * @goal start
 * 
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public class StartInMemDBMojo extends AbstractInMemDBMojo {

	/**
	 * The source files used to populate the database.
	 * 
	 * @parameter
	 */
	private File[] sources;

	/**
	 * The default constructor.
	 */
	public StartInMemDBMojo() {
	}

	/**
	 * Execute the Maven goal by creating a data source and then iterating over
	 * the list of sources and loading each one.
	 * 
	 * @throws MojoFailureException
	 *             If there was an error executing the goal.
	 */
	public void execute() throws MojoFailureException {
		final Database database = getDatabase();
		for (final File source : sources) {
			database.load(this, source);
		}
	}
}