# Axon Data Protection Maven Plugin

[![Release](https://img.shields.io/github/release/AxonIQ/axon-dataprotection-maven-plugin.svg?style=flat-square)](https://github.com/AxonIQ/axon-dataprotection-maven-plugin/releases/latest)
![Build Status](https://github.com/AxonIQ/axon-dataprotection-maven-plugin/workflows/Axon%20Server%20Plugin%20-%20Data%20Protection/badge.svg?branch=master)

Axon Data Protection Maven Plugin is a Maven Plugin responsible to generate the config file to be used on the Axon Server Plugin Data Protection module. The config is generated based on Annotated Classes (using the Axon Data Protection Config API Annotations) and the output is a Json file, which is one of the inputs for the Axon Server Data Protection Plugin.

For having the properly Annotations scanned, the `axon-dataprotection-config-api` is needed, and you can configure it like so:
```xml
<dependency>
    <groupId>io.axoniq</groupId>
    <artifactId>axon-dataprotection-config-api</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>compile</scope>
</dependency>
```

This Plugin is hooked into the `compile` phase of the Maven Lifecycle and has a goal named `generate`.
The mandatory configuration is a `packages` list where you can specify all `package`s where the plugin should scan for Annotated Classes. The optional configuration is the `outputConfig` where you can specify the directory you want the output json to be created. By default, it creates a file named `axon-data-protection-config.json` on your `target` folder.

> It has proven to be a good practice to make this json part of your git repository, so you can follow the evolving of your configuration as well as be notified (by git) when it changed to not forget to change it on the server.

Basically, this is an example of what you need on your project:

```xml
<plugins>
    ...
    <plugin>
        <groupId>io.axoniq</groupId>
        <artifactId>axon-dataprotection-maven-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <configuration>
            <!-- required -->
            <packages>
                <package>io.axoniq.package1</package>
                <package>io.axoniq.package2</package>
                <package>io.axoniq.package3</package>
            </packages>
            <!-- optional -->
            <outputConfig>output.json</outputConfig>
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

### Versioning

As we know, Events evolve and the `@Revision` annotation from Axon Framework is taken into account when generating the configuration. For that reason, every time any event changes a `@Revision`, a new configuration should be generated and the end configuration should contain both versions of the Event - this is a similar to a merge process, and it is **not automatic**.

---
Created with :heart: by [AxonIQ](https://axoniq.io/)