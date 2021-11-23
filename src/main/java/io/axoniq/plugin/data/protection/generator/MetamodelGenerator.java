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

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import io.axoniq.plugin.data.protection.annotation.SensitiveData;
import io.axoniq.plugin.data.protection.annotation.SensitiveDataHolder;
import io.axoniq.plugin.data.protection.annotation.SubjectId;
import io.axoniq.plugin.data.protection.config.DataProtectionConfig;
import io.axoniq.plugin.data.protection.config.DataProtectionConfigList;
import io.axoniq.plugin.data.protection.config.SensitiveDataConfig;
import io.axoniq.plugin.data.protection.config.SubjectIdConfig;
import io.axoniq.plugin.data.protection.generator.errors.NoSensitiveDataHolderAnnotationException;
import io.axoniq.plugin.data.protection.generator.errors.NoSubjectIdException;
import io.axoniq.plugin.data.protection.generator.utils.AnnotationUtils;
import io.axoniq.plugin.data.protection.generator.utils.ReflectionUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static io.axoniq.plugin.data.protection.generator.utils.ReflectionUtils.*;
import static io.axoniq.plugin.data.protection.generator.utils.TypeDetector.*;

/**
 * Class responsible to hold the logic for generating the Metamodel Configuration.
 */
public class MetamodelGenerator {

    /**
     * Represents a path prefix for json path.
     */
    private static final String PATH_PREFIX = "$";

    /**
     * Represents a path divider for json path.
     */
    private static final String PATH_DIVIDER = ".";

    /**
     * Represents every element of a given List.
     */
    private static final String PATH_LIST_ELEMENTS = "[*]";

    /**
     * Represents every element of a given Map entry.
     */
    private static final String PATH_MAP_ELEMENTS = "*";

    private final Log log;

    private final List<String> ignores;

    /**
     * Create a new instance of the {@link MetamodelGenerator}. Specially useful on tests setting up the default {@link
     * Log}.
     */
    public MetamodelGenerator() {
        this.log = new SystemStreamLog();
        this.ignores = new ArrayList<>();
    }

    /**
     * Create a new instance of the {@link MetamodelGenerator}. Specially useful on tests setting up the default {@link
     * Log}.
     *
     * @param ignores List of classes or packages that should be ignored when checking for types.
     */
    public MetamodelGenerator(List<String> ignores) {
        this.log = new SystemStreamLog();
        this.ignores = ignores;
    }

    /**
     * Create a new instance of the {@link MetamodelGenerator}.
     *
     * @param log     Log to be used on class. As a Maven Plugin, the default log from the {@link AbstractMojo#getLog()}
     *                is recommended.
     * @param ignores List of classes or packages that should be ignored when checking for types.
     */
    public MetamodelGenerator(Log log, List<String> ignores) {
        this.log = log;
        this.ignores = ignores;
    }

    /**
     * Start the generation of the metamodel based on the given {@code packages}.
     *
     * @param packages List of packages for look up for annotated classes.
     * @return A new instance of a {@link DataProtectionConfig}.
     */
    public DataProtectionConfigList generateMetamodel(List<String> packages) {
        List<DataProtectionConfig> dataProtectionConfigs = new ArrayList<>();
        packages.forEach(pkg -> dataProtectionConfigs.addAll(generateMetamodel(pkg).getConfig()));
        return new DataProtectionConfigList(dataProtectionConfigs);
    }

    /**
     * Start the generation of the metamodel based on the given {@code pkg}.
     *
     * @param pkg The package for look up for annotated classes.
     * @return A new instance of a {@link DataProtectionConfig}.
     */
    public DataProtectionConfigList generateMetamodel(String pkg) {
        log.info(String.format("Scanning package [%s]", pkg));
        List<DataProtectionConfig> dataProtectionConfigs = new ArrayList<>();
        // reflections lib code
        Reflections reflections = new Reflections(pkg);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(SensitiveDataHolder.class);
        // all SensitiveDataHolder annotated class
        annotatedClasses.stream()
                        .map(this::generateMetamodel)
                        .forEach(dataProtectionConfigs::add);

        // TODO: getFieldsAnnotatedWith can be used to validate if we got the right number of config entries
        return new DataProtectionConfigList(dataProtectionConfigs);
    }

    /**
     * Create a {@link DataProtectionConfig} instance based on the class and its fields.
     *
     * @param annotatedClass A class which is annotated with {@link SensitiveDataHolder}.
     * @return A new instance of a {@link DataProtectionConfig}.
     */
    public DataProtectionConfig generateMetamodel(Class<?> annotatedClass) {
        if (!annotatedClass.isAnnotationPresent(SensitiveDataHolder.class)) {
            throw new NoSensitiveDataHolderAnnotationException(annotatedClass);
        }
        log.debug(String.format("Scanning class [%s]", extractName(annotatedClass)));
        List<SensitiveDataConfig> sensitiveDataList = new ArrayList<>();
        String type = extractName(annotatedClass);
        String revision = extractRevision(annotatedClass);

        List<Field> classFields = getAllDeclaredFields(annotatedClass);
        SubjectIdConfig subjectId = extractSubjectId(classFields)
                .orElseThrow(() -> new NoSubjectIdException(annotatedClass));

        extractSensitiveData(classFields, sensitiveDataList, PATH_PREFIX);
        return new DataProtectionConfig(type, revision, subjectId, sensitiveDataList);
    }

    /**
     * Create a {@link SubjectIdConfig} instance based on a list of {@link Field}s. The first field annotated with
     * {@link SubjectId} is the one which the value will be taken.
     *
     * @param classFields List of fields from a {@link Class}.
     * @return A new instance of a {@link SubjectIdConfig}.
     */
    private Optional<SubjectIdConfig> extractSubjectId(List<Field> classFields) {
        return classFields.stream()
                          .filter(field -> AnnotationUtils.isAnnotationPresent(field, SubjectId.class))
                          .findFirst()
                          .map(subjectIdField -> new SubjectIdConfig(
                                  buildPath(PATH_PREFIX, extractName(subjectIdField))));
    }

    /**
     * Create one or more instances of a {@link SensitiveDataConfig} which are added to the {@param sensitiveDataList}.
     * This method is called recursively.
     *
     * @param classFields       A list of {@link Field}s that may contains {@link SensitiveData} annotated fields.
     * @param sensitiveDataList A list which will hold all the {@link SensitiveDataConfig} created during the calls.
     *                          Needed because this is meant to be a recursive method.
     * @param path              The path from the previous field. Needed because this is meant to be a recursive
     *                          method.
     */
    private void extractSensitiveData(List<Field> classFields,
                                      List<SensitiveDataConfig> sensitiveDataList,
                                      String path) {
        // direct annotated fields (ignoring the SubjectId annotated field)
        classFields.stream()
                   .filter(f -> AnnotationUtils.isAnnotationPresent(f, SensitiveData.class))
                   .filter(f -> !AnnotationUtils.isAnnotationPresent(f, SubjectId.class))
                   .forEach(f -> sensitiveDataList.add(
                           new SensitiveDataConfig(buildPath(path, extractName(f)),
                                                   extractReplacementValue(f))
                   ));
        // if it's not a primitive type, go deeper recursively (ignoring the SubjectId annotated field)
        classFields.stream()
                   .filter(f -> !AnnotationUtils.isAnnotationPresent(f, SubjectId.class))
                   .filter(f -> !ignore(ignores, f.getType()))
                   .filter(ReflectionUtils::shouldGoDeeper)
                   .forEach(field -> checkType(field,
                                               sensitiveDataList,
                                               buildPath(path, extractName(field))));
    }

    /**
     * Check the type of the given {@link Field} to decide if it's a form of Container, Array or not. In case it's a
     * Container or an Array, the method calls {@link MetamodelGenerator#extractSensitiveData(List, List, String)} on
     * its type. If not, it calls the method on its {@link Field}s.
     *
     * @param field             The {@link Field} we are going to perform the type check.
     * @param sensitiveDataList The container for all the {@link SensitiveDataConfig}. Needed because this is meant to
     *                          be a recursive method.
     * @param path              The path from the previous field. Needed because this is meant to be a recursive
     *                          method.
     */
    private void checkType(Field field, List<SensitiveDataConfig> sensitiveDataList, String path) {
        // TODO: can we replace the following 2 lines to avoid another dependency?
        TypeResolver resolver = new TypeResolver();
        ResolvedType type = resolver.resolve(field.getGenericType());

        if (isMap(type)) {
            // only Value of the Map, ignore Key
            extractSensitiveData(getAllDeclaredFields(type.getTypeParameters().get(1).getErasedType()),
                                 sensitiveDataList,
                                 buildMapPath(path));
        } else if (isArray(type)) {
            extractSensitiveData(getAllDeclaredFields(type.getArrayElementType().getErasedType()),
                                 sensitiveDataList,
                                 buildCollectionPath(path));
        } else if (hasTypeParameters(type)) {
            type.getTypeParameters()
                .stream()
                .filter(tp -> shouldGoDeeper(tp.getErasedType()))
                .forEach(tp -> extractSensitiveData(getAllDeclaredFields(tp.getErasedType()),
                                                    sensitiveDataList,
                                                    buildCollectionPath(path)));
        } else {
            extractSensitiveData(getAllDeclaredFields(type.getErasedType()), sensitiveDataList, path);
        }
    }

    /**
     * Check if the given type has Type Parameters.
     *
     * @param type Type which will be used to check if it has Type Parameters.
     * @return True or false, depending on the check.
     */
    private boolean hasTypeParameters(ResolvedType type) {
        return !type.getTypeParameters().isEmpty();
    }

    /**
     * Build a path based on the previous path and the current one.
     *
     * @param previousPath Previous path on the json
     * @param path         Current path on the json
     * @return A new path built based on the parameters divided by the {@link MetamodelGenerator#PATH_DIVIDER}
     */
    private String buildPath(String previousPath, String path) {
        return previousPath + PATH_DIVIDER + path;
    }

    /**
     * Build a path based on the previous path and the current one.
     *
     * @param path Current path on the json
     * @return A new path built based on the parameters divided by the {@link MetamodelGenerator#PATH_DIVIDER}
     */
    private String buildCollectionPath(String path) {
        return path + PATH_LIST_ELEMENTS;
    }

    /**
     * Build a path based on the previous path and the current one.
     *
     * @param path Current path on the json
     * @return A new path built based on the parameters divided by the {@link MetamodelGenerator#PATH_DIVIDER}
     */
    private String buildMapPath(String path) {
        return path + PATH_DIVIDER + PATH_MAP_ELEMENTS;
    }
}
