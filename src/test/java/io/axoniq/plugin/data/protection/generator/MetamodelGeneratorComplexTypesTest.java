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

import java.util.Collection;
import java.util.List;
import java.util.Set;

class MetamodelGeneratorComplexTypesTest {

    MetamodelGenerator metamodelGenerator = new MetamodelGenerator();

    @Test
    void complexTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorComplexTypesTest$ComplexTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                List.of(new SensitiveDataConfig("$.sensitiveData.string", "string"),
                        new SensitiveDataConfig("$.sensitiveData.integer", "integer")));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(ComplexTest.class);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void complexListTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorComplexTypesTest$ComplexListTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                List.of(new SensitiveDataConfig("$.sensitiveData[*].string", "string"),
                        new SensitiveDataConfig("$.sensitiveData[*].integer", "integer")));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(ComplexListTest.class);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void complexSetTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorComplexTypesTest$ComplexSetTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                List.of(new SensitiveDataConfig("$.sensitiveData[*].string", "string"),
                        new SensitiveDataConfig("$.sensitiveData[*].integer", "integer")));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(ComplexSetTest.class);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void complexCollectionTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorComplexTypesTest$ComplexCollectionTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                List.of(new SensitiveDataConfig("$.sensitiveData[*].string", "string"),
                        new SensitiveDataConfig("$.sensitiveData[*].integer", "integer")));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(ComplexCollectionTest.class);

        Assertions.assertEquals(expected, result);
    }

    @PII
    static class ComplexTest {

        @SubjectId
        ComplexType subjectId;

        ComplexType sensitiveData;
    }

    @PII
    static class ComplexListTest {

        @SubjectId
        ComplexType subjectId;

        List<ComplexType> sensitiveData;
    }

    @PII
    static class ComplexSetTest {

        @SubjectId
        ComplexType subjectId;

        Set<ComplexType> sensitiveData;
    }

    @PII
    static class ComplexCollectionTest {

        @SubjectId
        ComplexType subjectId;

        Collection<ComplexType> sensitiveData;
    }

    static class ComplexType {

        @SensitiveData(replacementValue = "string")
        String string;

        @SensitiveData(replacementValue = "integer")
        Integer integer;
    }
}
