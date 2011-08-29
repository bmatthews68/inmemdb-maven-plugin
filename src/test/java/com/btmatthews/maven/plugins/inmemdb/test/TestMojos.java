/*
 * Copyright 2008-2011 Brian Thomas Matthews
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

import junit.framework.TestCase;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Unit tests for the plug-in mojo's.
 * 
 * @author <a href="mailto:brian.matthews@btmatthews.com">Brian Matthews</a>
 * @version 1.0
 */
public final class TestMojos extends AbstractMojoTest {

	/**
	 * The default constructor.
	 */
	public TestMojos() {
	}

	/**
	 * Test the configuration for the start and stop goals.
	 * 
	 * @throws Exception
	 *             If something unexpected happens.
	 */
	public void testHSQLDB() throws Exception {

		final Mojo startMojo = this.getMojo(getName(), "start");
		TestCase.assertNotNull(startMojo);
		assertEquals(startMojo, "type", "hsqldb");
		assertEquals(startMojo, "database", "test");
		assertEquals(startMojo, "username", "sa");
		assertEquals(startMojo, "password", null);

		final Mojo stopMojo = this.getMojo(getName(), "stop");
		TestCase.assertNotNull(stopMojo);
		assertEquals(stopMojo, "type", "hsqldb");
		assertEquals(stopMojo, "database", "test");
		assertEquals(stopMojo, "username", "sa");
		assertEquals(stopMojo, "password", null);
		Thread serverThread = new Thread() {
			public void run() {
				try {
					startMojo.execute();
				} catch (MojoExecutionException e) {
				} catch (MojoFailureException e) {
				}
			}
		};
		serverThread.start();
		Thread.sleep(500);
		stopMojo.execute();
	}

	/**
	 * Test the configuration for the start and stop goals.
	 * 
	 * @throws Exception
	 *             If something unexpected happens.
	 */
	public void testDerby() throws Exception {
		Mojo mojo = this.getMojo(getName(), "start");
		TestCase.assertNotNull(mojo);
		assertEquals(mojo, "type", "derby");
		assertEquals(mojo, "database", "test");
		assertEquals(mojo, "username", "sa");
		assertEquals(mojo, "password", null);
		mojo.execute();

		mojo = this.getMojo(getName(), "stop");
		TestCase.assertNotNull(mojo);
		assertEquals(mojo, "type", "derby");
		assertEquals(mojo, "database", "test");
		assertEquals(mojo, "username", "sa");
		assertEquals(mojo, "password", null);
		mojo.execute();
	}

	/**
	 * Test the configuration for the start and stop goals.
	 * 
	 * @throws Exception
	 *             If something unexpected happens.
	 */
	public void testH2() throws Exception {
		Mojo mojo = this.getMojo(getName(), "start");
		TestCase.assertNotNull(mojo);
		assertEquals(mojo, "type", "h2");
		assertEquals(mojo, "database", "test");
		assertEquals(mojo, "username", "sa");
		assertEquals(mojo, "password", null);
		mojo.execute();

		mojo = this.getMojo(getName(), "stop");
		TestCase.assertNotNull(mojo);
		assertEquals(mojo, "type", "h2");
		assertEquals(mojo, "database", "test");
		assertEquals(mojo, "username", "sa");
		assertEquals(mojo, "password", null);
		mojo.execute();
	}
}
