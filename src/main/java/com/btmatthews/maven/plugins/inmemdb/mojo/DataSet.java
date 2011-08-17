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

import java.io.File;

/**
 * Describes source files that contain DBUnit data sets that can be used to
 * populate the in-memory database.
 * 
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 * @see Source
 * @see AbstractSource
 */
public final class DataSet extends AbstractSource {

	/**
	 * Indicates whether or not the data set contains qualified table names.
	 */
	private Boolean qualifiedTableNames;

	/**
	 * The default constructor.
	 * 
	 * @see AbstractSource#AbstractSource()
	 */
	public DataSet() {
	}

	/**
	 * Construct a source object that describes DBUnit data set.
	 * 
	 * @param file
	 *            The source file that contains the DDL/DML script or DBUnit
	 *            data set.
	 * @param flag
	 *            <ul>
	 *            <li>{@link Boolean#TRUE} if the are qualified with the schema
	 *            name.</li>
	 *            <li>{@link Boolean#FALSE} if the are not qualified with the
	 *            schema name.</li>
	 *            </ul>
	 * @see AbstractSource#AbstractSource(File)
	 */
	public DataSet(final File file, final Boolean flag) {
		super(file);
		this.qualifiedTableNames = flag;
	}

	/**
	 * Determine if the DBUnit data set contains qualified or unqualified table
	 * names.
	 * 
	 * @return <ul>
	 *         <li>{@link Boolean#TRUE} if the are qualified with the schema
	 *         name.</li>
	 *         <li>{@link Boolean#FALSE} if the are not qualified with the
	 *         schema name.</li>
	 *         </ul>
	 * @see Source#getQualifiedTableNames()
	 */
	public Boolean getQualifiedTableNames() {
		return this.qualifiedTableNames;
	}

	/**
	 * Indicate whether or not the DBUnit data set contains qualified or
	 * unqualified table names.
	 * 
	 * @param flag
	 *            <ul>
	 *            <li>{@link Boolean#TRUE} if the are qualified with the schema
	 *            name.</li>
	 *            <li>{@link Boolean#FALSE} if the are not qualified with the
	 *            schema name.</li>
	 *            </ul>
	 */
	public void setQualifiedTableNames(final Boolean flag) {
		this.qualifiedTableNames = flag;
	}
}
