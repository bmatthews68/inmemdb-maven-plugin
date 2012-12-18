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

package com.btmatthews.maven.plugins.inmemdb.db;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import com.btmatthews.maven.plugins.inmemdb.SQLDatabase;

/**
 * Abstract base classes for database objects providing an implementation of the
 * {@link com.btmatthews.maven.plugins.inmemdb.Database#load(com.btmatthews.utils.monitor.Logger, com.btmatthews.maven.plugins.inmemdb.Source)}
 * operation that is used to load data into or execute scripts
 * against the database.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public abstract class AbstractNoSQLDatabase extends AbstractDatabase implements SQLDatabase {

    /**
     * Get a data source object without additional connection attributes.
     *
     * @return The data source object.
     */
    @Override
    public final DataSource getDataSource() {
        final Map<String, String> attributes = new HashMap<String, String>();
        return getDataSource(attributes);
    }

    /**
     * Get the database connection protocol.
     *
     * @return The database connection protocol.
     */
    protected abstract String getUrlProtocol();

    /**
     * Construct the JDBC URL with connection specific attributes.
     *
     * @param attributes The connection specific attributes.
     * @return The JDBC URL.
     */
    public final String getUrl(final Map<String, String> attributes) {
        final StringBuilder url = new StringBuilder("jdbc:");
        url.append(getUrlProtocol());
        url.append(getDatabaseName());
        if (attributes.size() > 0) {
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                url.append(';');
                url.append(entry.getKey());
                url.append('=');
                url.append(entry.getValue());
            }
        }
        return url.toString();
    }
}
