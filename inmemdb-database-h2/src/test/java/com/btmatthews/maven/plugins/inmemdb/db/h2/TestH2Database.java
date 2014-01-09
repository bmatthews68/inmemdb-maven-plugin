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

package com.btmatthews.maven.plugins.inmemdb.db.h2;

import com.btmatthews.maven.plugins.inmemdb.mojo.AbstractTestDatabase;
import com.btmatthews.utils.monitor.Server;

/**
 * Unit test the H2 database server.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public class TestH2Database extends AbstractTestDatabase {

    /**
     * Create the {@link H2Database} server test fixture.
     *
     * @return The {@link H2Database} server test fixture.
     */
    @Override
    protected Server createDatabaseServer() {
        return new H2Database();
    }
}
