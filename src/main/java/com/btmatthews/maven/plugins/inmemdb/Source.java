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

package com.btmatthews.maven.plugins.inmemdb;

import java.io.File;

/**
 * The interface that must be implemented objects that describe DDL/DML scripts
 * or DBUnit data sets.
 * 
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public interface Source {

	/**
	 * Get the source file that contains the DDL/DML script or DBUnit data set.
	 * 
	 * @return The source file.
	 */
	String getSourceFile();

	/**
	 * Determine if the table names in the source file are fully qualified.
	 * 
	 * @return <ul>
	 *         <li>{@link Boolean#TRUE} if the table names are fully qualified.</li>
	 *         <li>{@link Boolean#FALSE} if the table names are not fully qualified.</li>
	 *         </ul>
	 */
	Boolean getQualifiedTableNames();
}
