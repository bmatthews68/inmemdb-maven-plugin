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

package com.btmatthews.maven.plugins.inmemdb.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

import com.btmatthews.maven.plugins.inmemdb.db.derby.DerbyDatabaseFactory;
import com.btmatthews.maven.plugins.inmemdb.db.h2.H2DatabaseFactory;
import com.btmatthews.maven.plugins.inmemdb.db.hsqldb.HSQLDBDatabaseFactory;
import com.btmatthews.utils.monitor.Logger;
import com.btmatthews.utils.monitor.ServerFactory;
import com.btmatthews.utils.monitor.ServerFactoryLocator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Unit test the server factory configuration.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public final class TestDatabaseFactory {

    /**
     * Mock for the logger.
     */
    @Mock
    private Logger logger;

    /**
     * Used to locate the {@link ServerFactory} for the database server.
     */
    private ServerFactoryLocator locator;

    /**
     * Create the server locator factory test fixture.
     */
    @Before
    public void setUp() {
        initMocks(this);
        locator = ServerFactoryLocator.getInstance(logger);
    }

    /**
     * Verify that the server factory locator returns {@code null} when {@code null} is passed as the
     * database type.
     */
    @Test
    public void testNullType() {
        final ServerFactory factory = locator.getFactory(null);
        assertNull(factory);
    }

    /**
     * Verify that the server factory locator returns a {@link DerbyDatabaseFactory} when
     * &quot;derby&quot; is passed as the database type.
     */
    @Test
    public void testDerbyType() {
        final ServerFactory factory = locator.getFactory("derby");
        assertNotNull(factory);
        assertTrue(factory instanceof DerbyDatabaseFactory);
    }

    /**
     * Verify that the server factory locator returns a {@link H2DatabaseFactory} when
     * &quot;h2&quot; is passed as the database type.
     */
    @Test
    public void testH2Type() {
        final ServerFactory factory = locator.getFactory("h2");
        assertNotNull(factory);
        assertTrue(factory instanceof H2DatabaseFactory);
    }

    /**
     * Verify that the server factory locator returns a {@link HSQLDBDatabaseFactory} when
     * &quot;hsqldb&quot; is passed as the database type.
     */
    @Test
    public void testHSQLDBType() {
        final ServerFactory factory = locator.getFactory("hsqldb");
        assertNotNull(factory);
        assertTrue(factory instanceof HSQLDBDatabaseFactory);
    }

    /**
     * Verify that the server factory locator returns a {@code null} when
     * &quot;mysql&quot; is passed as the database type.
     */
    @Test
    public void testUnknownType() {
        final ServerFactory factory = locator.getFactory("mysql");
        assertNull(factory);
    }
}
