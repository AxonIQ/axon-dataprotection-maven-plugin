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

import io.axoniq.plugin.data.protection.annotation.PII;
import io.axoniq.plugin.data.protection.annotation.SensitiveData;
import io.axoniq.plugin.data.protection.annotation.SubjectId;
import io.axoniq.plugin.data.protection.generator.errors.NoPIIAnnotationException;
import io.axoniq.plugin.data.protection.generator.errors.NoSubjectIdException;
import org.junit.jupiter.api.*;

public class MetamodelGeneratorTest {

    MetamodelGenerator metamodelGenerator = new MetamodelGenerator();

    @Test
    void noPIIClass() {
        Assertions.assertThrows(NoPIIAnnotationException.class,
                                () -> metamodelGenerator.generateMetamodel(NoPIIClass.class));
    }

    @Test
    void noSubjectIdClass() {
        Assertions.assertThrows(NoSubjectIdException.class,
                                () -> metamodelGenerator.generateMetamodel(NoSubjectIdClass.class));
    }

    static class NoPIIClass {

        @SubjectId
        String subjectId;
        @SensitiveData(replacementValue = "replacement-value")
        String sensitiveData;
    }

    @PII
    static class NoSubjectIdClass {

        String subjectId;
        @SensitiveData(replacementValue = "replacement-value")
        String sensitiveData;
    }
}
