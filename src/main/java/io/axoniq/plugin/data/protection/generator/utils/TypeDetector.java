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

import com.fasterxml.classmate.ResolvedType;

import java.util.List;
import java.util.Map;

public abstract class TypeDetector {

    /**
     * Check if the given type is a Map and that we should check the inner types of its Value. Key is ignored.
     *
     * @param type Type which will be used to check if it is a Map or not.
     * @return True or false, depending on the check.
     */
    public static boolean isMap(ResolvedType type) {
        return type.isInstanceOf(Map.class)
                && type.getTypeParameters().size() == 2
                && ReflectionUtils.shouldGoDeeper(type.getTypeParameters().get(1).getErasedType());
    }

    /**
     * Check if the given type is an Array and that we should check the inner types of it.
     *
     * @param type Type which will be used to check if it is an Array or not.
     * @return True or false, depending on the check.
     */
    public static boolean isArray(ResolvedType type) {
        return type.isArray()
                && ReflectionUtils.shouldGoDeeper(type.getArrayElementType().getErasedType());
    }

    /**
     * Check if a given type should be ignored or not. For that it uses the full qualified class name to compare with a
     * given class name or package name with `.*`.
     *
     * @param clazz Class which will be used to check if it should be ignored or not.
     * @return True or false, depending on the check.
     */
    public static boolean ignore(List<String> ignores, Class<?> clazz) {
        return ignoreClass(ignores, clazz) || ignorePackage(ignores, clazz);
    }

    private static boolean ignoreClass(List<String> ignores, Class<?> clazz) {
        return ignores.contains(clazz.getName());
    }

    private static boolean ignorePackage(List<String> ignores, Class<?> clazz) {
        return ignores.stream()
                      .filter(ignore -> ignore.endsWith(".*"))
                      .anyMatch(ignore -> clazz.getName().matches(ignore));
    }
}
