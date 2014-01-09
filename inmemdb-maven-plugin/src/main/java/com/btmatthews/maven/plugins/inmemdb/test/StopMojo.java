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

import com.btmatthews.utils.monitor.mojo.AbstractStopMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Stop an in-memory database server that is running as a daemon.
 *
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.0.0
 */
@Mojo(name = "stop", defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST)
public class StopMojo extends AbstractStopMojo {

    /**
     * Indicates whether or not the Mojo execution should be skipped.
     *
     * @since 1.4.0
     */
    @Parameter(property = "inmemdb.skip", defaultValue = "false")
    private boolean skip;

    /**
     * Checks whether the Mojo execution is being skipped before delegating to the super class to
     * stop the in-memory database.
     *
     * @throws MojoFailureException If there was a problem executing the Mojo.
     * @since 1.4.0
     */
    @Override
    public void execute() throws MojoFailureException {
        if (skip) {
            getLog().info("Skipping inmemdb:stop because inmemdb.skip=='true'");
        } else {
            super.execute();
        }
    }
}
