/*
 * Copyright (c) 2021. AxonIQ
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.axoniq.plugin.data.protection.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.axoniq.plugin.data.protection.config.DataProtectionConfigList;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

/**
 * Starting point for the Axon Data Protection Plugin.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.COMPILE)
public class AxonDataProtectionMojo extends AbstractMojo {

    /**
     * The project the plugin is running on. It can be used to extract info from that or to analyze their classes and
     * dependencies.
     */
    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    /**
     *
     */
    @Parameter(defaultValue = "${plugin}", readonly = true)
    private PluginDescriptor descriptor;

    /**
     * This property specify in which packages the plugin should look for the {@link
     * io.axoniq.plugin.data.protection.annotation.SensitiveDataHolder} Annotation to start generating the metamodel.
     */
    @Parameter(property = "packages", required = true)
    private List<String> packages;

    /**
     * Location of the result config.
     */
    @Parameter(defaultValue = "${project.build.directory}/axon-data-protection-config.json")
    private File outputConfig;

    /**
     * This property specify which class names or packages the plugin should ignore when looking for annotations. This
     * is useful when your Events has an attribute with a non-standard java type. A clear example would be a Joda Time
     * attribute type. Examples would be `my.package.Class` or `my.package.*`.
     */
    @Parameter(property = "ignores")
    private List<String> ignores;

    /**
     * Single instance of the ObjectMapper.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * This is the method called by maven to start the plugin.
     */
    public void execute() throws MojoExecutionException {
        getLog().info(String.format("Starting metamodel generation for %s", packages));
        getLog().info(String.format("Ignoring the following packages and classes: %s", ignores));
        addProjectClassesToPluginClasspath();
        MetamodelGenerator generator = new MetamodelGenerator(getLog(), ignores);
        try {
            DataProtectionConfigList config = generator.generateMetamodel(packages);
            writeOutput(config);
        } catch (Exception e) {
            getLog().error("Metamodel generation failed with: ", e);
            throw e;
        }
    }

    /**
     * Write the output to the configured File. Defaults to {@link AxonDataProtectionMojo#outputConfig}.
     *
     * @param config The generated configuration.
     */
    private void writeOutput(DataProtectionConfigList config) throws MojoExecutionException {
        try {
            if (!outputConfig.getParentFile().exists()) {
                outputConfig.getParentFile().mkdirs();
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(outputConfig, config);
        } catch (IOException e) {
            // TODO: print the config as a info/warn message
            throw new MojoExecutionException("Failed to write result schema.", e);
        }
    }

    /**
     * In order to inspect the classes of the project running the plugin, we need to add them to the plugin classpath.
     */
    private void addProjectClassesToPluginClasspath() {
        try {
            final ClassRealm realm = descriptor.getClassRealm();
            for (String dependency : project.getCompileClasspathElements()) {
                try {
                    final File elementFile = new File(dependency);
                    realm.addURL(elementFile.toURI().toURL());
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (DependencyResolutionRequiredException ex) {
            // throw exception when we are not able to check it
            ex.printStackTrace();
        }
    }
}