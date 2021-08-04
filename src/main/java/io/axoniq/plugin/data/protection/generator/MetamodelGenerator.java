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
import io.axoniq.plugin.data.protection.annotation.PII;
import io.axoniq.plugin.data.protection.annotation.SensitiveData;
import io.axoniq.plugin.data.protection.annotation.SubjectId;
import io.axoniq.plugin.data.protection.config.DataProtectionConfig;
import io.axoniq.plugin.data.protection.config.DataProtectionConfigList;
import io.axoniq.plugin.data.protection.config.SensitiveDataConfig;
import io.axoniq.plugin.data.protection.config.SubjectIdConfig;
import io.axoniq.plugin.data.protection.generator.errors.NoSubjectIdException;
import io.axoniq.plugin.data.protection.generator.utils.AnnotationUtils;
import io.axoniq.plugin.data.protection.generator.utils.ReflectionUtils;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
     * Start the generation of the metamodel based on the given {@param basePackage}.
     *
     * @param basePackage The base package for look up for annotated classes.
     */
    public DataProtectionConfigList generateMetamodel(String basePackage) {
        List<DataProtectionConfig> dataProtectionConfigs = new ArrayList<>();
        // reflections lib code
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(PII.class);
        // all PII annotated class
        annotatedClasses.stream()
                        .map(this::createDataProtectionConfig)
                        .forEach(dataProtectionConfigs::add);

        // TODO: getFieldsAnnotatedWith can be used to validate if we got the right number of config entries
        return new DataProtectionConfigList(dataProtectionConfigs);
    }

    /**
     * Create a {@link DataProtectionConfig} instance based on the class and its fields.
     *
     * @param piiAnnotatedClass A class which is annotated with {@link PII}.
     * @return A new instance of a {@link DataProtectionConfig}.
     */
    private DataProtectionConfig createDataProtectionConfig(Class<?> piiAnnotatedClass) {
        List<SensitiveDataConfig> sensitiveDataList = new ArrayList<>();
        String type = ReflectionUtils.extractName(piiAnnotatedClass);
        String revision = ReflectionUtils.extractRevision(piiAnnotatedClass);

        List<Field> piiClassFields = ReflectionUtils.getAllDeclaredFields(piiAnnotatedClass);
        SubjectIdConfig subjectId = extractSubjectId(piiClassFields)
                .orElseThrow(() -> new NoSubjectIdException(piiAnnotatedClass));

        extractSensitiveData(piiClassFields, sensitiveDataList, PATH_PREFIX);
        return new DataProtectionConfig(type, revision, subjectId, sensitiveDataList);
    }

    /**
     * Create a {@link SubjectIdConfig} instance based on a list of {@link Field}s. The first field annotated with {@link SubjectId} is the
     * one which the value will be taken.
     *
     * @param piiClassFields List of fields from a {@link Class}.
     * @return A new instance of a {@link SubjectIdConfig}.
     */
    private Optional<SubjectIdConfig> extractSubjectId(List<Field> piiClassFields) {
        return piiClassFields.stream()
                             .filter(field -> AnnotationUtils.isAnnotationPresent(field, SubjectId.class))
                             .findFirst()
                             .map(subjectIdField -> new SubjectIdConfig(buildPath(PATH_PREFIX,
                                                                                  ReflectionUtils.extractName(subjectIdField))));
    }

    /**
     * Create one or more instances of a {@link SensitiveDataConfig} which are added to the {@param sensitiveDataList}. This method is
     * called recursively.
     *
     * @param piiClassFields    A list of {@link Field}s that may contains {@link SensitiveData} annotated fields.
     * @param sensitiveDataList A list which will hold all the {@link SensitiveDataConfig} created during the calls. Needed because this is
     *                          meant to be a recursive method.
     * @param path              The path from the previous field. Needed because this is meant to be a recursive method.
     */
    private void extractSensitiveData(List<Field> piiClassFields,
                                      List<SensitiveDataConfig> sensitiveDataList,
                                      String path) {
        // direct annotated fields
        piiClassFields.stream()
                      .filter(f -> AnnotationUtils.isAnnotationPresent(f, SensitiveData.class))
                      .forEach(f -> sensitiveDataList.add(
                              new SensitiveDataConfig(buildPath(path, ReflectionUtils.extractName(f)),
                                                      ReflectionUtils.extractReplacementValue(f))
                      ));
        // if it's not a primitive type, go deeper recursively
        piiClassFields
                .forEach(field -> {
                    if (ReflectionUtils.isPrimitiveOrWrapperOrString(field)) {
                        return;
                    }
                    checkType(field, sensitiveDataList, buildPath(path, ReflectionUtils.extractName(field)));
                });
    }

    /**
     * Check the type of the given {@link Field} to decide if it's a form of Container or not. In case it's a container, the method calls
     * {@link MetamodelGenerator#extractSensitiveData(List, List, String)} on its type. If not, it calls the method on its {@link Field}s.
     *
     * @param field             The {@link Field} we are going to perform the type check.
     * @param sensitiveDataList The container for all the {@link SensitiveDataConfig}. Needed because this is meant to be a recursive
     *                          method.
     * @param path              The path from the previous field. Needed because this is meant to be a recursive method.
     */
    private void checkType(Field field, List<SensitiveDataConfig> sensitiveDataList, String path) {
        // TODO: can we replace the following 2 lines to avoid another dependency?
        TypeResolver resolver = new TypeResolver();
        ResolvedType type = resolver.resolve(field.getGenericType());

        // if it has parameters, it should be a form of Collection
        // TODO: really check if it's a collection/optional for example
        // TODO: we need to append an [*] for list (map later)
        // TODO: check Map
        // TODO: check Set
        // TODO: check Optional
        // TODO: check Array
        // TODO: check List
        if (!type.getTypeParameters().isEmpty()) {
            type.getTypeParameters().stream()
                .filter(tp -> !ReflectionUtils.isPrimitiveOrWrapperOrString(tp.getErasedType()))
                .forEach(tp -> extractSensitiveData(ReflectionUtils.getAllDeclaredFields(tp.getErasedType()), sensitiveDataList, path));
        } else {
            extractSensitiveData(ReflectionUtils.getAllDeclaredFields(type.getErasedType()), sensitiveDataList, path);
        }
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
}
