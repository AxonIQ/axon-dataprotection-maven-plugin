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
import io.axoniq.plugin.data.protection.config.DataProtectionConfig;
import io.axoniq.plugin.data.protection.config.SensitiveDataConfig;
import io.axoniq.plugin.data.protection.config.SubjectIdConfig;
import org.junit.jupiter.api.*;

import java.util.List;

class MetamodelGeneratorArrayTypesTest {

    MetamodelGenerator metamodelGenerator = new MetamodelGenerator();

    @Test
    void arrayTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorArrayTypesTest$ArrayTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                new SensitiveDataConfig("$.sensitiveData", "array"));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(ArrayTest.class);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void complexArrayTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorArrayTypesTest$ComplexArrayTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                List.of(new SensitiveDataConfig("$.sensitiveData[*].string", "string"),
                        new SensitiveDataConfig("$.sensitiveData[*].integer", "integer")));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(ComplexArrayTest.class);

        Assertions.assertEquals(expected, result);
    }

    @PII
    static class ArrayTest {

        @SubjectId
        String[] subjectId;

        @SensitiveData(replacementValue = "array")
        String[] sensitiveData;
    }

    @PII
    static class ComplexArrayTest {

        @SubjectId
        ComplexType subjectId;

        ComplexType[] sensitiveData;
    }

    static class ComplexType {

        @SensitiveData(replacementValue = "string")
        String string;

        @SensitiveData(replacementValue = "integer")
        Integer integer;
    }
}
