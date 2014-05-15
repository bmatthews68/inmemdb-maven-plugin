In-Memory Database Maven Plugin
===============================

The [In-Memory Database Maven Plugin](http://inmemdb-maven-plugin.btmatthews.com/) is a [Maven](http://maven.apache.org)
plugin that can launch and shutdown an embedded in-memory SQL database within the Maven build life-cycle. During the
launch the database can be seeded using DDL/DML scripts and/or [DBUnit](http://dbunit.sourceforge.net) data sets.

The following database implementations are supported:

* **derby** - An embedded/in-memory [Apache Derby](http://db.apache.org/derby) database.

* **h2** - An embedded/in-memory [H2](http://h2database.com) database.

* **hsqldb** - An embedded/in-memory [HSQLDB](http://hsqldb.org) database.

The following source file formats are supported:

* **.sql** - Contains DDL/DML SQL commands to create the database structure and/or insert test data

* **.csv** - A file containing a comma separated value (CSV) data set. The first row of the data set contains the column
  names and the file name corresponds to the table name.

* **.xml** - A file containing a
  [flat DBUnit XML data set](http://www.dbunit.org/apidocs/org/dbunit/dataset/xml/FlatXmlDataSet.html).

* **.dbunit.xml** - A file containing a
  [DBUnit XML data set](http://www.dbunit.org/apidocs/org/dbunit/dataset/xml/XmlDataSet.html).

* **.xls** - A Microsoft Excel spread sheet containing one or more work sheets. The name of the work sheet corresponds
  to the table name and the first row of each work sheet contains the column names.

Example
-------
The **In-Memory Database Maven Plugin** can be used to automate integration tests  having a dependency on an
external database server.

### pom.xml

The POM here is from taken from the
[webapp](https://github%20%20.com/bmatthews68/inmemdb-maven-plugin/tree/master/src/it/webapp) integration test for the
**In-Memory Database Maven Plugin**.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="
             http://maven.apache.org/POM/4.0.0
             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>webapp</groupId>
    <artifactId>webapp</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>war</packaging>
    <build>
        <plugins>
            <plugin>
                <groupId>com.btmatthews.maven.plugins.inmemdb</groupId>
                <artifactId>inmemdb-maven-plugin</artifactId>
                <version>1.4.3</version>
                <configuration>
                    <monitorKey>inmemdb</monitorKey>
                    <monitorPort>11527</monitorPort>
                    <skip>${maven.test.skip}</skip>
                    <attributes>
                        <territory>ga_IE</territory>
                    </attributes>
                </configuration>
                <executions>
                    <execution>
                        <id>run</id>
                        <goals>
                            <goal>run</goal>
                        </goals>
                        <phase>pre-integration-test</phase>
                        <configuration>
                            <daemon>true</daemon>
                            <type>derby</type>
                            <database>test</database>
                            <username>sa</username>
                            <password></password>
                            <sources>
                                <script>
                                    <sourceFile>src/test/resources/create_database.sql</sourceFile>
                                </script>
                                <dataSet>
                                    <sourceFile>src/test/resources/users.dbunit.xml</sourceFile>
                                </dataSet>
                            </sources>
                        </configuration>
                    </execution>
```

You can add special configuration on your database connection URL with ***attributes*** tag in the ***configuration*** part.

````xml
				...
				<configuration>
                    <monitorKey>inmemdb</monitorKey>
                    <monitorPort>11527</monitorPort>
					<type>h2</type>
					<database>test</database>
					<attributes>
						<PAGE_SIZE>512</PAGE_SIZE>
					</attributes>
				</configuration>
				...
```

The **In-Memory Database Maven Plugin** is configured here to launch an in-memory Apache Derby database server as a
daemon process during the **pre-integration-test** phase of the build life cycle. The database is initialised with
a database schema and some seed data.

```xml
                    <execution>
                        <id>stop</id>
                        <goals>
                            <goal>stop</goal>
                        </goals>
                        <phase>post-integration-test</phase>
                    </execution>
```

The **In-Memory Database Maven Plugin** is configured here to shutdown the in-memory Apache Derby database server during
the **post-integration-test** phase of the build life cycle.

```xml
                </executions>
            </plugin>
            <plugin>
                <groupId>org.mortbay.jetty</groupId>
                <artifactId>jetty-maven-plugin</artifactId>
                <version>8.1.8.v20121106</version>
                <configuration>
                    <stopKey>jetty</stopKey>
                    <stopPort>19080</stopPort>
                    <daemon>true</daemon>
                    <webApp>
                        <contextPath>/</contextPath>
                    </webApp>
                    <jettyXml>src/test/jetty/jetty.xml</jettyXml>
                    <connectors>
                        <connector implementation="org.eclipse.jetty.server.nio.SelectChannelConnector">
                            <port>9080</port>
                            <maxIdleTime>60000</maxIdleTime>
                        </connector>
                    </connectors>
                </configuration>
                <executions>
                    <execution>
                        <id>start-jetty</id>
                        <phase>pre-integration-test</phase>
                        <goals>
                            <goal>run</goal>
                        </goals>
                    </execution>
```

The [Jetty Maven Plugin](http://wiki.eclipse.org/Jetty/Feature/Jetty_Maven_Plugin) is configured here to launch the
[Jetty](http://wiki.eclipse.org/Jetty/) servlet container as a daemon process during the **pre-integration-test** phase
of the build life cycle.

```xml
                    <execution>
                        <id>stop-jetty</id>
                        <phase>post-integration-test</phase>
                        <goals>
                            <goal>stop</goal>
                        </goals>
                    </execution>
```

The [Jetty Maven Plugin](http://wiki.eclipse.org/Jetty/Feature/Jetty_Maven_Plugin) is configured here to shutdown the
[Jetty](http://wiki.eclipse.org/Jetty/) servlet container during the **post-integration-test** phase of the build life
cycle.

```xml
                </executions>
                <dependencies>
                    <dependency>
                        <groupId>org.apache.derby</groupId>
                        <artifactId>derbyclient</artifactId>
                        <version>10.9.1.0</version>
                    </dependency>
                    <dependency>
                        <groupId>commons-dbcp</groupId>
                        <artifactId>commons-dbcp</artifactId>
                        <version>1.3</version>
                    </dependency>
                </dependencies>
            </plugin>
```

The **org.apache.derby:derbyclient** and **commons-dbcp:commons-dbcp** dependencies are required in order to define the
data source that connects to the in-memory [Apache Derby](http://db.apache.org/derby) database.

```xml
            <plugin>
                <artifactId>maven-failsafe-plugin</artifactId>
                <version>2.12.4</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>integration-test</goal>
                            <goal>verify</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
```

The [Maven Failsafe Plugin](http://maven.apache.org/surefire/maven-failsafe-plugin/) is used to execute the integration
tests during the **integration-test** phase of the build life cycle.

```xml
        </plugins>
    </build>
    <dependencies>
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>3.0.1</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>com.btmatthews.selenium.junit4</groupId>
            <artifactId>selenium-junit4-runner</artifactId>
            <version>1.0.4</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.11</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

The **com.btmatthews.selenium.junit4:selenium-junit-runner** and **junit:junit** dependencies are required by the
integration tests.

### jetty.xml

Add the following fragment of XML to the [jetty.xml](http://wiki.eclipse.org/Jetty/Reference/jetty.xml) to define a
data source for the test database.

```xml
<New class="org.eclipse.jetty.plus.jndi.Resource">
    <Arg>
        <Ref id="Server" />
    </Arg>
    <Arg>jdbc/test</Arg>
    <Arg>
        <New class="org.apache.commons.dbcp.BasicDataSource">
            <Set name="username">sa</Set>
            <Set name="password"></Set>
            <Set name="url">jdbc:derby://localhost/memory:test</Set>
            <Set name="driverClassName">org.apache.derby.jdbc.ClientDriver</Set>
        </New>
    </Arg>
</New>
```

### web.xml

Add the following **<resource-ref/>** to the web.xml to make the data source defined in
[jetty.xml](http://wiki.eclipse.org/Jetty/Reference/jetty.xml) inside the web application:

```xml
<resource-ref>
    <res-ref-name>jdbc/test</res-ref-name>
    <res-type>javax.sql.DataSource</res-type>
    <res-auth>Container</res-auth>
</resource-ref>
```

Maven Central Coordinates
-------------------------
The **In-Memory Database Maven Plugin** has been published in [Maven Central](http://search.maven.org)
at the following coordinates:

```xml
<plugin>
    <groupId>com.btmatthews.maven.plugins.inmemdb</groupId>
    <artifactId>inmemdb-maven-plugin</artifactId>
    <version>1.4.3</version>
</plugin>
```

Credits
-------
This project contains contributions from:

* [Glen Mazza](https://github.com/gmazza)
* [Nicolas Girot](https://github.com/ngirot)

License & Source Code
---------------------
The **In-Memory Database Maven Plugin** is made available under the
[Apache License](http://www.apache.org/licenses/LICENSE-2.0.html) and the source code is hosted on
[GitHub](http://github.com) at https://github.com/bmatthews68/inmemdb-maven-plugin.

