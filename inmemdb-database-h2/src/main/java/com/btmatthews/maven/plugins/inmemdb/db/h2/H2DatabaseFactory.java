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

import com.btmatthews.utils.monitor.Server;
import com.btmatthews.utils.monitor.ServerFactory;

/**
 * A factory that creates embedded H2 database servers.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
public final class H2DatabaseFactory implements ServerFactory {

    /**
     * Get the name of the server created by this server factory.
     *
     * @return Always returns {@code h2}.
     */
    @Override
    public String getServerName() {
        return "h2";
    }

    /**
     * Create the server instance.
     *
     * @return A {@link H2Database} instance.
     */
    @Override
    public Server createServer() {
        return new H2Database();
    }
}
