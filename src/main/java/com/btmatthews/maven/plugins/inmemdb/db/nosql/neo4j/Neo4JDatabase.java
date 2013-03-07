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

package com.btmatthews.maven.plugins.inmemdb.db.nosql.neo4j;

import java.util.Iterator;
import java.util.Map;

import com.btmatthews.maven.plugins.inmemdb.Loader;
import com.btmatthews.maven.plugins.inmemdb.db.AbstractNoSQLDatabase;
import com.btmatthews.maven.plugins.inmemdb.ldr.json.JSONLoader;
import com.btmatthews.utils.monitor.Logger;
import org.codehaus.jackson.JsonNode;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.helpers.Settings;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.neo4j.server.configuration.ServerConfigurator;
import org.neo4j.shell.ShellSettings;

/**
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.0.0
 */
public class Neo4JDatabase extends AbstractNoSQLDatabase {

    private WrappingNeoServerBootstrapper server;

    private GraphDatabaseAPI db;

    @Override
    protected Loader[] getLoaders() {
        return new Loader[]{
                new JSONLoader()
        };
    }

    public void insertObject(final String collection,
                             final JsonNode object,
                             final Logger logger)

    {
        final Node node = db.createNode();
        final Iterator<Map.Entry<String, JsonNode>> entryIterator = object.getFields();
        while (entryIterator.hasNext()) {
            final Map.Entry<String, JsonNode> entry = entryIterator.next();
            if (entry.getValue().isValueNode()) {
                if (entry.getValue().isNumber()) {
                    switch (entry.getValue().getNumberType()) {
                        case INT:
                            node.setProperty(entry.getKey(), entry.getValue().getIntValue());
                            break;
                        case LONG:
                            node.setProperty(entry.getKey(), entry.getValue().getLongValue());
                            break;
                        case BIG_INTEGER:
                            break;
                        case FLOAT:
                            node.setProperty(entry.getKey(), entry.getValue().getDoubleValue());
                            break;
                        case DOUBLE:
                            node.setProperty(entry.getKey(), entry.getValue().getDoubleValue());
                            break;
                        case BIG_DECIMAL:
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void start(final Logger logger) {
        db = (GraphDatabaseAPI)new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder("target/neo4j")
                .setConfig(ShellSettings.remote_shell_enabled, Settings.TRUE)
                .newGraphDatabase();
        final ServerConfigurator config = new ServerConfigurator(db);
        server = new WrappingNeoServerBootstrapper(db, config);
        server.start();
    }

    @Override
    public void stop(final Logger logger) {
        server.stop();
    }
}
