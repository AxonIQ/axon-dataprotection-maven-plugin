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
