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

package io.axoniq.plugin.data.protection.generator.errors;

/**
 * Exception to indicate that a given Class do not contain field annotated with a {@link
 * io.axoniq.plugin.data.protection.annotation.SubjectId} on it.
 */
public class NoSubjectIdException extends RuntimeException {

    public NoSubjectIdException(String message) {
        super(message);
    }

    public NoSubjectIdException(Class<?> clazz) {
        super("No SubjectId annotated field found in [" + clazz + "] or one of it's parents");
    }
}
