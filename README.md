# Axon Data Protection Maven Plugin

Axon Data Protection Maven Plugin is a Maven Plugin responsible to generate the config file to be used on the Axon Server Plugin Data Protection module. The config is generated based on Annotated Classes (using the Axon Data Protection Config API Annotations) and the output is a Json file, which is the input for the Axon Server Plugin.

This Plugin is hooked into the `Compile` phase of the Maven Lifecycle and has a goal names `generate`.
The mandatory configuration is a `basePackage` field where you can specify where the plugin should scan for Annotated Classes.

Basically, this is an example of what you need on your project:

```xml
<plugins>
    ...
    <plugin>
        <groupId>io.axoniq</groupId>
        <artifactId>axon-dp-maven-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <configuration>
            <basePackage>io.axoniq</basePackage>
        </configuration>
        <executions>
            <execution>
                <goals>
                    <goal>generate</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
    ...
</plugins>
```

---
Created with :heart: by [AxonIQ](https://axoniq.io/)