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

package com.btmatthews.maven.plugins.inmemdb.test;

/**
 * Unit tests for the Mojo that implements the run goal for the H2 database server.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 */
public class TestH2RunMojo extends AbstractTestRunMojo {

    /**
     * Get the monitor port for the H2 test cases.
     *
     * @return Always returns {@code 19092}.
     */
    @Override
    protected int getMonitorPort() {
        return 19092;
    }

    /**
     * Get the type code for the H2 test cases.
     *
     * @return Always returns {@code h2}.
     */
    @Override
    protected String getType() {
        return "h2";
    }

    /**
     * Get the driver class name for the H2 test cases.
     *
     * @return Always returns {@code org.h2.Driver}.
     */
    @Override
    protected String getDriverClassName() {
        return "org.h2.Driver";
    }

    /**
     * Get the connection string for the H2 test cases.
     *
     * @return Always returns {@code jdbc:h2:tcp://localhost/mem:test;user=sa;password=}.
     */
    @Override
    protected String getConnectionString() {
        return "jdbc:h2:tcp://localhost/mem:test;user=sa;password=";
    }
}
