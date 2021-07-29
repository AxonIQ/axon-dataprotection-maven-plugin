package io.axoniq.plugin.data.protection.generator.utils;

import io.axoniq.plugin.data.protection.annotation.SensitiveData;
import org.axonframework.serialization.Revision;

import java.lang.reflect.Field;

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
}
