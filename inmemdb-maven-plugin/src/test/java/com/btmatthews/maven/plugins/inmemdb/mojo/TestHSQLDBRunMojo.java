/*
 * Copyright 2012 Brian Thomas Matthews
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
 * Unit tests for the Mojo that implements the run goal for the HSQLDB database server.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 */
public class TestHSQLDBRunMojo extends AbstractTestRunMojo {

    /**
     * Get the monitor port for the HSQLDB test cases.
     *
     * @return Always returns {@code 19001}.
     */
    @Override
    protected int getMonitorPort() {
        return 19001;
    }

    /**
     * Get the type code for the HSQLDB test cases.
     *
     * @return Always returns {@code hsqldb}.
     */
    @Override
    protected String getType() {
        return "hsqldb";
    }

    /**
     * Get the driver class name for the HSQLDB test cases.
     *
     * @return Always returns {@code org.hsqldb.jdbcDriver}.
     */
    @Override
    protected String getDriverClassName() {
        return "org.hsqldb.jdbcDriver";
    }

    /**
     * Get the connection string for the HSQLDB test cases.
     *
     * @return Always returns {@code jdbc:hsqldb:hsql://localhost/test;user=sa;password=}.
     */
    @Override
    protected String getConnectionString() {
        return "jdbc:hsqldb:hsql://localhost/test;user=sa;password=";
    }
}
