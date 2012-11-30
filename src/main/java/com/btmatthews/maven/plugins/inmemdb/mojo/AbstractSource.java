/*
 * Copyright 2011-2012 Brian Matthews
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

import java.io.File;

import com.btmatthews.maven.plugins.inmemdb.Source;

/**
 * Abstract base class for objects that describe the source files that will be
 * loaded into the in-memory database.
 * 
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public abstract class AbstractSource implements Source {

	/**
	 * The source file that contains the DDL/DML script or DBUnit data set.
	 */
	private String sourceFile;

	/**
	 * The default constructor.
	 */
	protected AbstractSource() {
	}

	/**
	 * Construct a source object that describes a DDL/DML script or DBUnit data
	 * set.
	 * 
	 * @param file
	 *            The source file that contains the DDL/DML script or DBUnit
	 *            data set.
	 */
	protected AbstractSource(final String file) {
		this.sourceFile = file;
	}

	/**
	 * Get the source file that contains the DDL/DML script or DBUnit data set.
	 * 
	 * @return The source file.
	 */
	public final String getSourceFile() {
		return this.sourceFile;
	}

	/**
	 * 
	 * Set the source file that contains the DDL/DML script or DBUnit data set.
	 * 
	 * @param file
	 *            The source file.
	 */
	public final void setSourceFile(final String file) {
		this.sourceFile = file;
	}

}