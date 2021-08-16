# Axon Data Protection Maven Plugin

Axon Data Protection Maven Plugin is a Maven Plugin responsible to generate the config file to be used on the Axon Server Plugin Data Protection module. The config is generated based on Annotated Classes (using the Axon Data Protection Config API Annotations) and the output is a Json file, which is the input for the Axon Server Data Protection Plugin.

For having the properly Annotations scanned, the `axon-dataprotection-config-api` is needed, and you can configure it like so:
```xml
<dependency>
    <groupId>io.axoniq</groupId>
    <artifactId>axon-dataprotection-config-api</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

This Plugin is hooked into the `Compile` phase of the Maven Lifecycle and has a goal named `generate`.
The mandatory configuration is a `packages` list where you can specify all `package`s where the plugin should scan for Annotated Classes.

Basically, this is an example of what you need on your project:

```xml
<plugins>
    ...
    <plugin>
        <groupId>io.axoniq</groupId>
        <artifactId>axon-dataprotection-maven-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <configuration>
            <packages>
                <package>io.axoniq.package1</package>
                <package>io.axoniq.package2</package>
                <package>io.axoniq.package3</package>
            </packages>
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