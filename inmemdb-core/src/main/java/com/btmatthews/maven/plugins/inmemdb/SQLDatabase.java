/*
 * Copyright 2011-2016 Brian Matthews
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

import javax.sql.DataSource;

/**
 * Describes the operations that are used by the Mojos to launch in-memory
 * databases, load data into, execute scripts against or shutdown the database.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public interface SQLDatabase extends Database {

    /**
     * Get the data source that describes the connection to the in-memory
     * database.
     *
     * @return The data source.
     */
    DataSource getDataSource();
}
