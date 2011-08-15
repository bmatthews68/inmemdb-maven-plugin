/*
 * Copyright 2008-2011 Brian Thomas Matthews
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

import junit.framework.TestCase;

import org.apache.maven.plugin.Mojo;

/**
 * Unit tests for the plug-in mojo's.
 * 
 * @author <a href="mailto:brian.matthews@btmatthews.com">Brian Matthews</a>
 * @version 1.0
 */
public final class TestMojos extends AbstractMojoTest {
    /**
     * The default constructor.
     */
    public TestMojos() {
    }

    /**
     * Test the configuration for the start and stop goals.
     * 
     * @throws Exception
     *             If something unexpected happens.
     */
    public void testStartStopHSQLDB() throws Exception {
        Mojo mojo = this.getMojo("testHSQLDB", "start");
        TestCase.assertNotNull(mojo);
        assertEquals(mojo, "type", "hsqldb");
        assertEquals(mojo, "database", "test");
        assertEquals(mojo, "username", "sa");
        assertEquals(mojo, "password", null);
        mojo.execute();

        mojo = this.getMojo("testHSQLDB", "stop");
        TestCase.assertNotNull(mojo);
        assertEquals(mojo, "type", "hsqldb");
        assertEquals(mojo, "database", "test");
        assertEquals(mojo, "username", "sa");
        assertEquals(mojo, "password", null);
        mojo.execute();
    }

    /**
     * Test the configuration for the start and stop goals.
     * 
     * @throws Exception
     *             If something unexpected happens.
     */
    public void testStartStopDerby() throws Exception {
        Mojo mojo = this.getMojo("testDerby", "start");
        TestCase.assertNotNull(mojo);
        assertEquals(mojo, "type", "derby");
        assertEquals(mojo, "database", "test");
        assertEquals(mojo, "username", "sa");
        assertEquals(mojo, "password", null);
        mojo.execute();

        mojo = this.getMojo("testDerby", "stop");
        TestCase.assertNotNull(mojo);
        assertEquals(mojo, "type", "derby");
        assertEquals(mojo, "database", "test");
        assertEquals(mojo, "username", "sa");
        assertEquals(mojo, "password", null);
        mojo.execute();
    }

    /**
     * Test the configuration for the start and stop goals.
     * 
     * @throws Exception
     *             If something unexpected happens.
     */
    public void testStartStopH2() throws Exception {
        Mojo mojo = this.getMojo("testH2", "start");
        TestCase.assertNotNull(mojo);
        assertEquals(mojo, "type", "h2");
        assertEquals(mojo, "database", "test");
        assertEquals(mojo, "username", "sa");
        assertEquals(mojo, "password", null);
        mojo.execute();

        mojo = this.getMojo("testH2", "stop");
        TestCase.assertNotNull(mojo);
        assertEquals(mojo, "type", "h2");
        assertEquals(mojo, "database", "test");
        assertEquals(mojo, "username", "sa");
        assertEquals(mojo, "password", null);
        mojo.execute();
    }
}
