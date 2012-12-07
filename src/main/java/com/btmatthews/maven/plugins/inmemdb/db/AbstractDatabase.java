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

import com.btmatthews.maven.plugins.inmemdb.Database;
import com.btmatthews.maven.plugins.inmemdb.Loader;
import com.btmatthews.maven.plugins.inmemdb.MessageUtil;
import com.btmatthews.maven.plugins.inmemdb.Source;
import com.btmatthews.utils.monitor.AbstractServer;
import com.btmatthews.utils.monitor.Logger;

/**
 * Abstract base classes for database objects providing an implementation of the
 * {@link Database#load(com.btmatthews.utils.monitor.Logger, com.btmatthews.maven.plugins.inmemdb.Source)}
 * operation that is used to load data into or execute scripts
 * against the database.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public abstract class AbstractDatabase extends AbstractServer implements Database {

    /**
     * The message key for the error reported when a file type is not supported.
     */
    private static final String UNSUPPORTED_FILE_TYPE = "unsupported_file_type";

    /**
     * The message key for the error reported when a server cannot be started.
     */
    protected static final String ERROR_STARTING_SERVER = "error_starting_server";

    /**
     * The message key for the error reported when a server cannot be stopped.
     */
    protected static final String ERROR_STOPPING_SERVER = "error_stopping_server";

    /**
     * The database name.
     */
    private String databaseName;

    /**
     * The user name used to connect to the database.
     */
    private String databaseUsername;

    /**
     * The password used to connect to the database.
     */
    private String databasePassword;

    /**
     * Configure the server.
     *
     * @param name   Name of the configuration property.
     * @param value  Value of the configuration property.
     * @param logger Used for logging error and information messages.
     */
    @Override
    public void configure(final String name, final Object value, final Logger logger) {
        if ("database".equals(name)) {
            logger.logInfo("Configured database name: " + value);
            databaseName = (String)value;
        } else if ("username".equals(name)) {
            logger.logInfo("Configured database username: " + value);
            databaseUsername = (String)value;
        } else if ("password".equals(name)) {
            logger.logInfo("Configured database password: " + value);
            databasePassword = (String)value;
        }
    }

    /**
     * Get the database name.
     *
     * @return The database name.
     */
    protected final String getDatabaseName() {
        return this.databaseName;
    }

    /**
     * Get the user name used to connect to the database.
     *
     * @return The user name.
     */
    protected final String getUsername() {
        return databaseUsername;
    }

    /**
     * Get the password used to connect to the database.
     *
     * @return The password.
     */
    protected final String getPassword() {
        if (databasePassword == null) {
            return "";
        } else {
            return databasePassword;
        }
    }

    /**
     * Get the loaders that are supported for loading data or executing scripts.
     *
     * @return Returns an array of loaders.
     */
    protected abstract Loader[] getLoaders();

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
     * Find the loader that supports the source file and use it to load the data
     * into or execute the script against the database.
     *
     * @param logger Used to report errors and raise exceptions.
     * @param source The source file containing data or script.
     */
    @Override
    public final void load(final Logger logger, final Source source) {
        if (source == null) {
            final String message = MessageUtil.getMessage(UNSUPPORTED_FILE_TYPE, "null");
            logger.logError(message);
        } else {
            final Loader[] loaders = getLoaders();
            int i = 0;
            while (i < loaders.length) {
                if (loaders[i].isSupported(logger, source)) {
                    loaders[i].load(logger, this, source);
                    break;
                }
                ++i;
            }
            if (i >= loaders.length) {
                final String message = MessageUtil.getMessage(UNSUPPORTED_FILE_TYPE, source.getSourceFile());
                logger.logError(message);
            }
        }
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
