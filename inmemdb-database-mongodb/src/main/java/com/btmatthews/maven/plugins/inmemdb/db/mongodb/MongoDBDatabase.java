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

package com.btmatthews.maven.plugins.inmemdb.db.mongodb;

import com.btmatthews.maven.plugins.inmemdb.Loader;
import com.btmatthews.maven.plugins.inmemdb.db.AbstractNoSQLDatabase;
import com.btmatthews.maven.plugins.inmemdb.ldr.json.JSONLoader;
import com.btmatthews.utils.monitor.Logger;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.0.0
 */
public class MongoDBDatabase extends AbstractNoSQLDatabase {

    private static final int DEFAULT_PORT = 27017;

    public MongoDBDatabase() {
        super(DEFAULT_PORT);
    }

    @Override
    protected Loader[] getLoaders() {
        return new Loader[]{
                new JSONLoader()
        };
    }

    public void insertObject(final String collection,
                             final JsonNode object,
                             final Logger logger) {
    }

    @Override
    public void start(final Logger logger) {
    }

    @Override
    public void stop(final Logger logger) {
    }
}
