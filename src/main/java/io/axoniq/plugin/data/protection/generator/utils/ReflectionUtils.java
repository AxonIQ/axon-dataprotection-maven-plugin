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

package io.axoniq.plugin.data.protection.generator.utils;

import io.axoniq.plugin.data.protection.annotation.SensitiveData;
import org.axonframework.serialization.Revision;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utils around Java Reflection. It offers several methods for checking types for {@link Class} and {@link Field}.
 */
public class ReflectionUtils {

    /**
     * Empty String constant.
     */
    private static final String EMPTY_STRING = "";

    /**
     * Used to check if the given class is primitive, wrapper or a String. Just delegate the check to the {@link
     * ClassUtils}.
     *
     * @param clazz The class which you want to check.
     * @return True or false, depending on the check.
     */
    public static boolean isPrimitiveOrWrapperOrString(Class<?> clazz) {
        return ClassUtils.isPrimitiveOrWrapper(clazz)
                || ClassUtils.isPrimitiveArray(clazz)
                || ClassUtils.isPrimitiveWrapperArray(clazz)
                || ClassUtils.isAssignable(String.class, clazz);
    }

    /**
     * Used to check if the given field is primitive, wrapper or a String. Just delegate the check to the {@link
     * ClassUtils}.
     *
     * @param field The field which you want to check.
     * @return True or false, depending on the check.
     */
    public static boolean isPrimitiveOrWrapperOrString(Field field) {
        return ClassUtils.isPrimitiveOrWrapper(field.getType())
                || ClassUtils.isPrimitiveArray(field.getType())
                || ClassUtils.isPrimitiveWrapperArray(field.getType())
                || ClassUtils.isAssignable(String.class, field.getType());
    }

    /**
     * Extract the {@link Revision} value when the annotation is present.
     *
     * @param clazz The class which could contain the annotation.
     * @return The extracted value otherwise {@link ReflectionUtils#EMPTY_STRING}.
     */
    public static String extractRevision(Class<?> clazz) {
        if (AnnotationUtils.isAnnotationPresent(clazz, Revision.class)) {
            return clazz.getAnnotation(Revision.class).value();
        } else {
            return EMPTY_STRING;
        }
    }

    /**
     * Extract the {@link SensitiveData} replacement value when the annotation is present.
     *
     * @param field The field which could contain the annotation.
     * @return The extracted value otherwise {@link ReflectionUtils#EMPTY_STRING}.
     */
    public static String extractReplacementValue(Field field) {
        if (AnnotationUtils.isAnnotationPresent(field, SensitiveData.class)) {
            return field.getAnnotation(SensitiveData.class).replacementValue();
        } else {
            return EMPTY_STRING;
        }
    }

    /**
     * Extract the name of the class. Just calls {@link Class#getName()}.
     *
     * @param clazz The class for which the name is needed.
     * @return The name of the class.
     */
    public static String extractName(Class<?> clazz) {
        return clazz.getName();
    }

    /**
     * Extract the name of the field. Just calls {@link Field#getName()}.
     *
     * @param field The field for which the name is needed.
     * @return The name of the field.
     */
    public static String extractName(Field field) {
        return field.getName();
    }

    /**
     * Get all the declared fields on the class and of all super classes or interfaces that the class implements. This will scan the whole
     * inheritance tree. Redeclare fields will produce semi-duplicates.
     * @param clazz The class to inspect the fields of.
     * @return The list of all fields, including fields of superclasses and interfaces
     */
    public static List<Field> getAllDeclaredFields(Class<?> clazz) {
        if (clazz == null) { return new ArrayList<>(); }

        List<Class<?>> parents = getAllParents(clazz);
        List<Field> fields = parents.stream()
                                     .flatMap(c -> Arrays.stream(c.getDeclaredFields()))
                                     .collect(Collectors.toList());
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        return fields;
    }

    /**
     * Get all the super classes or interfaces of the given class. This will scan the whole inheritance tree
     * @param clazz The class to inspect for super classes and interfaces
     * @return The list of all super classes and interfaces
     */
    public static List<Class<?>> getAllParents(Class<?> clazz) {
        if (clazz == null) { return new ArrayList<>(); }
        ArrayList<Class<?>> classes = new ArrayList<>();

        Class<?> superclass = clazz.getSuperclass();

        if(superclass != null) {
            classes.add(superclass);
            classes.addAll(getAllParents(superclass));
        }

        List<Class<?>> interfaces = Arrays.asList(clazz.getInterfaces());
        classes.addAll(interfaces);
        interfaces.forEach(i ->  classes.addAll(getAllParents(i)));

        return classes;
    }
}
