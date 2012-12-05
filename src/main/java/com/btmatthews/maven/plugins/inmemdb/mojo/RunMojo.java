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

import java.util.HashMap;
import java.util.Map;

import com.btmatthews.maven.plugins.inmemdb.Database;
import com.btmatthews.maven.plugins.inmemdb.Source;
import com.btmatthews.utils.monitor.Server;
import com.btmatthews.utils.monitor.mojo.AbstractRunMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * This plug-in Mojo starts an In Memory Database.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.2.0
 */
@Mojo(name = "run", defaultPhase = LifecyclePhase.TEST_COMPILE)
public final class RunMojo extends AbstractRunMojo {

    /**
     * The source files used to populate the database.
     */
    @Parameter
    private Source[] sources;

    /**
     * The database type.
     */
    @Parameter(property = "inmemdb.type", defaultValue = "hsqldb")
    private String type;

    /**
     * The database name.
     */
    @Parameter(property = "inmemdb.database", defaultValue = ".")
    private String database;

    /**
     * The username for database connections.
     */
    @Parameter(property = "inmemdb.username", defaultValue = "sa")
    private String username;

    /**
     * The password for database connections.
     */
    @Parameter(property = "inmemdb.password", defaultValue = "sa")
    private String password;

    /**
     * Get the server type.
     *
     * @return The value of {@link #type}.
     */
    @Override
    public String getServerType() {
        return type;
    }

    /**
     * Get the server configuration parameters. These are {@link #database}, {@link #username} and {@link #password}.
     *
     * @return A {@link Map} containing the configuration parameters.
     */
    @Override
    public Map<String, Object> getServerConfig() {
        final Map<String, Object> config = new HashMap<String, Object>();
        config.put("database", database);
        config.put("username", username);
        config.put("password", password);
        return config;
    }

    /**
     * This callback is invoked after the server has started and is used load the scripts
     * and datasets that will initialise the database.
     *
     * @param server The database server.
     */
    protected void started(final Server server) {
        if (sources != null) {
            for (final Source source : sources) {
                ((Database)server).load(this, source);
            }
        }
    }
}