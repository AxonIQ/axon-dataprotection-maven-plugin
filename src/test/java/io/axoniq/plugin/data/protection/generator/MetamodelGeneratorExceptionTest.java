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

import io.axoniq.plugin.data.protection.annotation.SensitiveData;
import io.axoniq.plugin.data.protection.annotation.SensitiveDataHolder;
import io.axoniq.plugin.data.protection.annotation.SubjectId;
import io.axoniq.plugin.data.protection.generator.errors.NoSensitiveDataHolderAnnotationException;
import io.axoniq.plugin.data.protection.generator.errors.NoSubjectIdException;
import org.junit.jupiter.api.*;

public class MetamodelGeneratorExceptionTest {

    MetamodelGenerator metamodelGenerator = new MetamodelGenerator();

    @Test
    void noSensitiveDataHolderClass() {
        Assertions.assertThrows(NoSensitiveDataHolderAnnotationException.class,
                                () -> metamodelGenerator.generateMetamodel(NoSensitiveDataHolderClass.class));
    }

    @Test
    void noSubjectIdClass() {
        Assertions.assertThrows(NoSubjectIdException.class,
                                () -> metamodelGenerator.generateMetamodel(NoSubjectIdClass.class));
    }

    static class NoSensitiveDataHolderClass {

        @SubjectId
        String subjectId;
        @SensitiveData(replacementValue = "replacement-value")
        String sensitiveData;
    }

    @SensitiveDataHolder
    static class NoSubjectIdClass {

        String subjectId;
        @SensitiveData(replacementValue = "replacement-value")
        String sensitiveData;
    }
}
