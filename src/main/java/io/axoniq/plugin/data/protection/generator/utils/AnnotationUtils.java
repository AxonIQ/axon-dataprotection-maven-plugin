package io.axoniq.plugin.data.protection.generator.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * Utils around {@link Annotation}. Provides utils for checking if a given Annotation is present.
 */
public class AnnotationUtils {

    /**
     * Check if a given {@link Annotation} is present or not on a given {@link Class}.
     *
     * @param element    The element which is an instance of an {@link AnnotatedElement}.
     * @param annotation The annotation you want to check the presence.
     * @return True or false, depending on the check.
     */
    public static boolean isAnnotationPresent(AnnotatedElement element, Class<? extends Annotation> annotation) {
        return isAnnotationPresent(element, annotation.getName());
    }

    /**
     * Check if a given Annotation name is present or not on a given {@link Class}.
     *
     * @param element        The element which is an instance of an {@link AnnotatedElement}.
     * @param annotationName The annotation name you want to check the presence.
     * @return True or false, depending on the check.
     */
    private static boolean isAnnotationPresent(AnnotatedElement element, String annotationName) {
        return getAnnotation(element, annotationName) != null;
    }

    /**
     * Get an Annotation by it's name on a given {@link Class}.
     *
     * @param element        The element which is an instance of an {@link AnnotatedElement}.
     * @param annotationName The annotation name you want to check the presence.
     * @return The annotation if it was found. Otherwise null.
     */
    private static Annotation getAnnotation(AnnotatedElement element, String annotationName) {
        for (Annotation annotation : element.getAnnotations()) {
            if (annotationName.equals(annotation.annotationType().getName())) {
                return annotation;
            }
        }
        return null;
    }
}
