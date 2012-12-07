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

/**
 * Describes source files that contain DDL/DML scripts that can be used to
 * create and populate the in-memory database.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public final class Script extends AbstractSource {

    /**
     * The default constructor.
     */
    public Script() {
    }

    /**
     * Construct a source object that describes a DDL/DML script or DBUnit data
     * set.
     *
     * @param file The source file that contains the DDL/DML script or DBUnit
     *             data set.
     */
    public Script(final String file) {
        super(file);
    }

    /**
     * Indicates that the script source descriptor does not distinguish between
     * qualified and unqualified table names by returning <code>null</code>.
     *
     * @return Always returns <code>null</code>.
     */
    public Boolean getQualifiedTableNames() {
        return null;
    }

    @Override
    public String toString() {
        return "Script[" + getSourceFile() + "]";
    }
}
