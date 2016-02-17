/*
 * Copyright 2008-2016 Brian Thomas Matthews
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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.InvalidRepositoryException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.DefaultRepositoryRequest;
import org.apache.maven.artifact.repository.RepositoryRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.configurator.AbstractComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.composite.ObjectWithFieldsConverter;
import org.codehaus.plexus.component.configurator.converters.special.ClassRealmConverter;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:brian@btmatthews.com">Brian Matthews</a>
 * @since 1.2.0
 */
@Component(role = ComponentConfigurator.class, hint = "include-database-dependencies")
public class IncludeDatabaseDependenciesComponentConfigurator extends AbstractComponentConfigurator {

    /**
     * The plugin configuration element that specifies the LDAP server type.
     */
    private static final String SERVER_TYPE_ATTRIBUTE = "type";
    /**
     * The default LDAP server type.
     */
    private static final String DEFAULT_SERVER_TYPE_VALUE = "hsqldb";
    /**
     * The group id of the main artifact for the LDAP server type.
     */
    private static final String DEFAULT_GROUP_ID = "com.btmatthews.maven.plugins.inmemdb";
    /**
     * Used to construct the artifact id of the main artifact for the LDAP server type.
     */
    private static final String DEFAULT_ARTIFACT_ID_FORMAT = "inmemdb-database-{0}";

    @Requirement
    private RepositorySystem repositorySystem;

    /**
     * Configure the Mojo by adding the dependencies for the LDAP server type to the class loader.
     *
     * @param component           The Mojo being configured.
     * @param configuration       The plugin configuration.
     * @param expressionEvaluator Used to evaluate expressions.
     * @param containerRealm      Used to build the class loader.
     * @param listener            Receives notification when configuration is changed.
     * @throws ComponentConfigurationException If there was a problem resolving or locating any of the dependencies.
     */
    @Override
    public void configureComponent(final Object component,
                                   final PlexusConfiguration configuration,
                                   final ExpressionEvaluator expressionEvaluator,
                                   final ClassRealm containerRealm,
                                   final ConfigurationListener listener)
            throws ComponentConfigurationException {

        final String serverType = getServerType(configuration);

        if (!serverType.startsWith("dependency-")) {
            addServerDependenciesToClassRealm(serverType, expressionEvaluator, containerRealm);
        }

        converterLookup.registerConverter(new ClassRealmConverter(containerRealm));

        final ObjectWithFieldsConverter converter = new ObjectWithFieldsConverter();

        converter.processConfiguration(converterLookup, component, containerRealm, configuration,
                expressionEvaluator, listener);
    }

    /**
     * Resolve the dependencies for an LDAP server type and add them to the class loader.
     *
     * @param serverType          The LDAP server type.
     * @param expressionEvaluator Used to evaluate expressions.
     * @param containerRealm      The class loader.
     * @throws ComponentConfigurationException If there was a problem resolving or locating any of the dependencies.
     */
    private void addServerDependenciesToClassRealm(final String serverType,
                                                   final ExpressionEvaluator expressionEvaluator,
                                                   final ClassRealm containerRealm)
            throws ComponentConfigurationException {
        final Collection<Artifact> classpathElements = getServerDependencies(serverType, expressionEvaluator);
        if (classpathElements != null) {
            for (final URL url : buildURLs(classpathElements)) {
                containerRealm.addURL(url);
            }
        }
    }

    /**
     * Retrive the URLs used to locate the JAR files for a list of artifacts.
     *
     * @param classpathElements The list of artifacts.
     * @return The list of URLs.
     * @throws ComponentConfigurationException If any JAR files could not be located.
     */
    private List<URL> buildURLs(final Collection<Artifact> classpathElements)
            throws ComponentConfigurationException {
        final List<URL> urls = new ArrayList<URL>(classpathElements.size());
        for (final Artifact classpathElement : classpathElements) {
            try {
                final URL url = classpathElement.getFile().toURI().toURL();
                urls.add(url);
            } catch (final MalformedURLException e) {
                throw new ComponentConfigurationException("Unable to access project dependency: " + classpathElement, e);
            }
        }
        return urls;
    }

    /**
     * Resolve the LDAP server type artifact and its dependencies.
     *
     * @param serverType          The LDAP server type.
     * @param expressionEvaluator Used to get the Maven project model and repository session objects.
     * @return A list of dependencies required for the LDAP server type.
     * @throws ComponentConfigurationException If the was a problem resolving the dependencies for
     *                                         the LDAP server type.
     */
    private Collection<Artifact> getServerDependencies(final String serverType,
                                                       final ExpressionEvaluator expressionEvaluator)
            throws ComponentConfigurationException {
        try {
            final MavenProject project = (MavenProject) expressionEvaluator.evaluate("${project}");
            final String localRepo = (String) expressionEvaluator.evaluate("${settings.localRepository}");
            final ArtifactRepository localRepository = repositorySystem.createLocalRepository(new File(localRepo));
            final RepositoryRequest repositoryRequest = new DefaultRepositoryRequest();
            repositoryRequest.setRemoteRepositories(project.getRemoteArtifactRepositories());
            repositoryRequest.setLocalRepository(localRepository);
            final ArtifactResolutionRequest request = new ArtifactResolutionRequest(repositoryRequest);
            request.setArtifact(getServerArtifact(serverType));
            request.setResolveTransitively(true);
            final ArtifactResolutionResult result = repositorySystem.resolve(request);
            if (result.isSuccess()) {
                return result.getArtifacts();
            }
            boolean first = true;
            final StringBuilder builder = new StringBuilder("Cannot resolve dependencies: [");
            for (final Artifact artifact : result.getMissingArtifacts()) {
                if (!first) {
                    builder.append(',');
                    first = false;
                }
                builder.append(artifact.getGroupId());
                builder.append(':');
                builder.append(artifact.getArtifactId());
                builder.append(':');
                builder.append(artifact.getVersion());
            }
            builder.append("]");
            throw new ComponentConfigurationException(builder.toString());
        } catch (final ExpressionEvaluationException e) {
            throw new ComponentConfigurationException("Error evaluating expression", e);
        } catch (final InvalidRepositoryException e) {
            throw new ComponentConfigurationException("Error resolving local repository", e);
        }
    }

    /**
     * Get the artifact descriptor for the JAR file that implements support for the LDAP server type.
     *
     * @param serverType The LDAP server type.
     * @return The JAR file artifact descriptor.
     */
    private Artifact getServerArtifact(final String serverType)
            throws ComponentConfigurationException {
        if (serverType.startsWith("gav-")) {
            int index = serverType.indexOf("-", 4);
            if (index > 0) {
                String[] gav = serverType.substring(index + 1).split(":");
                if (gav.length == 3) {
                    return repositorySystem.createArtifact(
                            gav[0],
                            gav[1],
                            gav[2],
                            "runtime",
                            "jar"
                    );
                }
            }
            throw new ComponentConfigurationException("Invalid server type: " + serverType);
        } else {
            return repositorySystem.createArtifact(
                    DEFAULT_GROUP_ID,
                    MessageFormat.format(DEFAULT_ARTIFACT_ID_FORMAT, serverType),
                    getClass().getPackage().getImplementationVersion(),
                    "runtime",
                    "jar");
        }
    }

    /**
     * Determine the configured LDAP server type. If one has not been configured then
     * return the default.
     *
     * @param configuration The plugin configuration.
     * @return The LDAP server type.
     */
    private String getServerType(final PlexusConfiguration configuration) {
        final Pattern pattern = Pattern.compile("\\$\\{[A-Za-z0-9\\._]+\\}");
        for (final PlexusConfiguration cfg : configuration.getChildren()) {
            if (cfg.getName().equals(SERVER_TYPE_ATTRIBUTE)) {
                if (pattern.matcher(cfg.getValue()).matches()) {
                    return DEFAULT_SERVER_TYPE_VALUE;
                } else {
                    return cfg.getValue();
                }
            }
        }
        return DEFAULT_SERVER_TYPE_VALUE;
    }
}
