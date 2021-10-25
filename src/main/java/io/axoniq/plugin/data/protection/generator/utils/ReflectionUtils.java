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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utils around Java Reflection. It offers several methods for checking types for {@link Class} and {@link Field}.
 */
public abstract class ReflectionUtils {

    /**
     * Empty String constant.
     */
    private static final String EMPTY_STRING = "";

    /**
     * Check if it should go deeper checking for extra fields inside the given class. This is not true for primitives,
     * wrappers, enums and common java types.
     *
     * @param clazz The class which you want to check.
     * @return True or false, depending on the check.
     */
    public static boolean shouldGoDeeper(Class<?> clazz) {
        return !isPrimitiveOrWrapper(clazz)
                && !isCommonJavaType(clazz)
                && !isDateTimeJavaType(clazz)
                && !isEnumType(clazz);
    }

    /**
     * Check if it should go deeper checking for extra fields inside the type of the given field. This is not true for
     * primitives, wrappers, enums and common java types.
     *
     * @param field The field which you want to check.
     * @return True or false, depending on the check.
     */
    public static boolean shouldGoDeeper(Field field) {
        return shouldGoDeeper(field.getType());
    }

    /**
     * Used to check if the given class is primitive, wrapper or a String. Just delegate the check to the {@link
     * ClassUtils}.
     *
     * @param clazz The class which you want to check.
     * @return True or false, depending on the check.
     */
    private static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return ClassUtils.isPrimitiveOrWrapper(clazz)
                || ClassUtils.isPrimitiveArray(clazz)
                || ClassUtils.isPrimitiveWrapperArray(clazz);
    }

    /**
     * Check for common Java Types, for example String, BigDecimal and BigInteger.
     *
     * @param clazz The class which you want to check.
     * @return True or false, depending on the check.
     */
    private static boolean isCommonJavaType(Class<?> clazz) {
        return ClassUtils.isAssignable(String.class, clazz)
                || ClassUtils.isAssignable(BigDecimal.class, clazz)
                || ClassUtils.isAssignable(BigInteger.class, clazz);
    }

    /**
     * Check for common Date Time Java Types, for example Date, LocalDate, LocalTime, etc.
     *
     * @param clazz The class which you want to check.
     * @return True or false, depending on the check.
     */
    private static boolean isDateTimeJavaType(Class<?> clazz) {
        return ClassUtils.isAssignable(Date.class, clazz)
                || ClassUtils.isAssignable(LocalDate.class, clazz)
                || ClassUtils.isAssignable(LocalTime.class, clazz)
                || ClassUtils.isAssignable(LocalDateTime.class, clazz)
                || ClassUtils.isAssignable(OffsetDateTime.class, clazz)
                || ClassUtils.isAssignable(ZonedDateTime.class, clazz)
                || ClassUtils.isAssignable(Instant.class, clazz)
                || ClassUtils.isAssignable(Period.class, clazz)
                || ClassUtils.isAssignable(Duration.class, clazz);
    }

    /**
     * Check for Java Enum types.
     *
     * @param clazz The class which you want to check.
     * @return True or false, depending on the check.
     */
    private static boolean isEnumType(Class<?> clazz) {
        return clazz.isEnum();
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
     * Get all the declared fields on the class and of all super classes or interfaces that the class implements. This
     * will scan the whole inheritance tree. Redeclared fields will produce semi-duplicates.
     *
     * @param clazz The class to inspect the fields of.
     * @return The list of all fields, including fields of superclasses and interfaces
     */
    public static List<Field> getAllDeclaredFields(Class<?> clazz) {
        if (clazz == null) {
            return new ArrayList<>();
        }

        List<Class<?>> parents = getAllParents(clazz);
        List<Field> fields = parents.stream()
                                    .flatMap(c -> Arrays.stream(c.getDeclaredFields()))
                                    .collect(Collectors.toList());
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        return fields;
    }

    /**
     * Get all the super classes or interfaces of the given class. This will scan the whole inheritance tree
     *
     * @param clazz The class to inspect for super classes and interfaces
     * @return The list of all super classes and interfaces
     */
    public static List<Class<?>> getAllParents(Class<?> clazz) {
        if (clazz == null) {
            return new ArrayList<>();
        }
        ArrayList<Class<?>> classes = new ArrayList<>();

        Class<?> superclass = clazz.getSuperclass();

        if (superclass != null) {
            classes.add(superclass);
            classes.addAll(getAllParents(superclass));
        }

        List<Class<?>> interfaces = Arrays.asList(clazz.getInterfaces());
        classes.addAll(interfaces);
        interfaces.forEach(i -> classes.addAll(getAllParents(i)));

        return classes;
    }
}
