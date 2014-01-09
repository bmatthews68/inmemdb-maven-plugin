package com.btmatthews.maven.plugins.inmemdb.mojo;

import com.jcabi.aether.Aether;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.configurator.AbstractComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.composite.ObjectWithFieldsConverter;
import org.codehaus.plexus.component.configurator.converters.special.ClassRealmConverter;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.JavaScopes;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@Component(role = ComponentConfigurator.class, hint = "include-database-dependencies")
public class IncludeDatabaseDependenciesComponentConfigurator extends AbstractComponentConfigurator {

    /**
     * The plugin configuration element that specifies the LDAP server type.
     */
    private static final String SERVER_TYPE_ATTRIBUTE = "serverType";
    /**
     * The default LDAP server type.
     */
    private static final String DEFAULT_SERVER_TYPE_VALUE = "derby";
    /**
     * The group id of the main artifact for the LDAP server type.
     */
    private static final String DEFAULT_GROUP_ID = "com.btmatthews.maven.plugins.inmemdb";
    /**
     * Used to construct the artifact id of the main artifact for the LDAP server type.
     */
    private static final String DEFAULT_ARTIFACT_ID_FORMAT = "inmemdb-database-{0}";

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

        addServerDependenciesToClassRealm(serverType, expressionEvaluator, containerRealm);

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
    private List<Artifact> getServerDependencies(final String serverType,
                                                 final ExpressionEvaluator expressionEvaluator)
            throws ComponentConfigurationException {
        try {
            final MavenProject project = (MavenProject) expressionEvaluator.evaluate("${project}");
            final RepositorySystemSession session = (RepositorySystemSession) expressionEvaluator.evaluate("${repositorySystemSession}");
            if (session != null) {
                try {
                    final File repo = session.getLocalRepository().getBasedir();
                    return new Aether(project, repo).resolve(
                            getServerArtifact(serverType),
                            JavaScopes.RUNTIME);
                } catch (final DependencyResolutionException e) {
                    final String message = new StringBuilder("Could not resolve dependencies for server type: ")
                            .append(serverType)
                            .toString();
                    throw new ComponentConfigurationException(message, e);
                }
            }
            return Collections.emptyList();
        } catch (final ExpressionEvaluationException e) {
            throw new ComponentConfigurationException("Error evaluating expression", e);
        }
    }

    /**
     * Get the artifact descriptor for the JAR file that implements support for the LDAP server type.
     *
     * @param serverType The LDAP server type.
     * @return The JAR file artifact descriptor.
     */
    private Artifact getServerArtifact(final String serverType) {
        return new DefaultArtifact(
                DEFAULT_GROUP_ID,
                MessageFormat.format(DEFAULT_ARTIFACT_ID_FORMAT, serverType),
                "",
                "jar",
                getClass().getPackage().getImplementationVersion(),
                null);
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

