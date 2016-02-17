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

import com.btmatthews.utils.monitor.Logger;

/**
 * Describes the operations that are used by the Mojos to launch in-memory
 * databases, load data into, execute scripts against or shutdown the database.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0.0
 */
public interface Database {

    /**
     * Find the loader that supports the source file and use it to load the data
     * into or execute the script against the database.
     *
     * @param logger Used to report errors and raise exceptions.
     * @param source The source file containing data or script.
     */
    void load(Logger logger, Source source);
}
