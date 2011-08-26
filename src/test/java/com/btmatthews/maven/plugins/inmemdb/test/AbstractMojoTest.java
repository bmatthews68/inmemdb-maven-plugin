/*
 * Copyright 2011 Brian Matthews
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

import java.io.File;

import junit.framework.TestCase;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.PlexusTestCase;

/**
 * Abstract base class for Mojo test cases belonging to the In-Memory Database
 * Maven Plugin.
 * 
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @version 1.0
 */
public abstract class AbstractMojoTest extends AbstractMojoTestCase {

	/**
	 * The default constructor.
	 */
	protected AbstractMojoTest() {
	}

	/**
	 * Get the Mojo that implements the specified goal.
	 * 
	 * @param test
	 *            The test name.
	 * @param goal
	 *            The goal name.
	 * @return The Mojo that implements the specified goal.
	 * @throws Exception
	 *             If something unexpected happens.
	 */
	protected final Mojo getMojo(final String test, final String goal)
			throws Exception {
		final StringBuilder buffer = new StringBuilder("/target/test-classes/");
		buffer.append(test);
		buffer.append("-plugin-config.xml");
		final File testPom = new File(PlexusTestCase.getBasedir(),
				buffer.toString());
		assert testPom.exists();
		return this.lookupMojo(goal, testPom);
	}

	/**
	 * Assert that the value of a Mojo variable has the specified expected
	 * value.
	 * 
	 * @param mojo
	 *            The Mojo object.
	 * @param name
	 *            The name of the variable.
	 * @param expectedValue
	 *            The expected value of the variable.
	 * @throws Exception
	 *             If something unexpected happens.
	 */
	protected final void assertEquals(final Mojo mojo, final String name,
			final String expectedValue) throws Exception {
		TestCase.assertEquals(expectedValue,
				(String) this.getVariableValueFromObject(mojo, name));
	}

	/**
	 * Assert that the value of a Mojo variable has the specified expected
	 * value.
	 * 
	 * @param mojo
	 *            The Mojo object.
	 * @param name
	 *            The name of the variable.
	 * @param expectedValue
	 *            The expected value of the variable.
	 * @throws Exception
	 *             If something unexpected happens.
	 */
	protected final void assertEquals(final Mojo mojo, final String name,
			final int expectedValue) throws Exception {
		TestCase.assertEquals(expectedValue, ((Integer) this
				.getVariableValueFromObject(mojo, name)).intValue());
	}

	/**
	 * Assert that the value of a Mojo variable has the specified expected
	 * value.
	 * 
	 * @param mojo
	 *            The Mojo object.
	 * @param name
	 *            The name of the variable.
	 * @param expectedValue
	 *            The expected value of the variable.
	 * @throws Exception
	 *             If something unexpected happens.
	 */
	protected final void assertEquals(final Mojo mojo, final String name,
			final boolean expectedValue) throws Exception {
		TestCase.assertEquals(expectedValue, ((Boolean) this
				.getVariableValueFromObject(mojo, name)).booleanValue());
	}
}
