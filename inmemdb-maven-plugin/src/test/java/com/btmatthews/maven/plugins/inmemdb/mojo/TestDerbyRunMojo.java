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
 * Unit tests for the Mojo that implements the run goal for the Derby database server.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 */
public class TestDerbyRunMojo extends AbstractTestRunMojo {

    /**
     * Get the monitor port for the Derby test cases.
     *
     * @return Always returns {@code 11527}.
     */
    @Override
    protected int getMonitorPort() {
        return 11527;
    }

    /**
     * Get the type code for the Derby test cases.
     *
     * @return Always returns {@code derby}.
     */
    @Override
    protected String getType() {
        return "derby";
    }

    /**
     * Get the driver class name for the Derby test cases.
     *
     * @return Always returns {@code org.apache.derby.jdbc.ClientDriver}.
     */
    @Override
    protected String getDriverClassName() {
        return "org.apache.derby.jdbc.ClientDriver";
    }

    /**
     * Get the connection string for the Derby test cases.
     *
     * @return Always returns {@code jdbc:derby//localhost/memory:test;user=sa}.
     */
    @Override
    protected String getConnectionString() {
        return "jdbc:derby://localhost/memory:test;user=sa";
    }
}
