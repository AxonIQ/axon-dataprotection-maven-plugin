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

class MetamodelGeneratorBoxedTypesTest {

    MetamodelGenerator metamodelGenerator = new MetamodelGenerator();

    @Test
    void integerTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorBoxedTypesTest$IntegerTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                new SensitiveDataConfig("$.sensitiveData", "1"));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(IntegerTest.class);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void stringTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorBoxedTypesTest$StringTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                new SensitiveDataConfig("$.sensitiveData", "hidden"));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(StringTest.class);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void floatTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorBoxedTypesTest$FloatTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                new SensitiveDataConfig("$.sensitiveData", "2.5"));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(FloatTest.class);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void booleanTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorBoxedTypesTest$BooleanTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                new SensitiveDataConfig("$.sensitiveData", "false"));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(BooleanTest.class);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void longTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorBoxedTypesTest$LongTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                new SensitiveDataConfig("$.sensitiveData", "3"));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(LongTest.class);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void shortTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorBoxedTypesTest$ShortTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                new SensitiveDataConfig("$.sensitiveData", "4"));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(ShortTest.class);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void doubleTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorBoxedTypesTest$DoubleTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                new SensitiveDataConfig("$.sensitiveData", "5.5"));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(DoubleTest.class);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void characterTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorBoxedTypesTest$CharacterTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                new SensitiveDataConfig("$.sensitiveData", "A"));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(CharacterTest.class);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void byteTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorBoxedTypesTest$ByteTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                new SensitiveDataConfig("$.sensitiveData", "B"));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(ByteTest.class);

        Assertions.assertEquals(expected, result);
    }

    @PII
    static class IntegerTest {

        @SubjectId
        Integer subjectId;

        @SensitiveData(replacementValue = "1")
        Integer sensitiveData;
    }

    @PII
    static class StringTest {

        @SubjectId
        String subjectId;

        @SensitiveData(replacementValue = "hidden")
        String sensitiveData;
    }

    @PII
    static class FloatTest {

        @SubjectId
        Float subjectId;

        @SensitiveData(replacementValue = "2.5")
        Float sensitiveData;
    }

    @PII
    static class BooleanTest {

        @SubjectId
        Boolean subjectId;

        @SensitiveData(replacementValue = "false")
        Boolean sensitiveData;
    }

    @PII
    static class LongTest {

        @SubjectId
        Long subjectId;

        @SensitiveData(replacementValue = "3")
        Long sensitiveData;
    }

    @PII
    static class ShortTest {

        @SubjectId
        Short subjectId;

        @SensitiveData(replacementValue = "4")
        Short sensitiveData;
    }

    @PII
    static class DoubleTest {

        @SubjectId
        Double subjectId;

        @SensitiveData(replacementValue = "5.5")
        Double sensitiveData;
    }

    @PII
    static class CharacterTest {

        @SubjectId
        Character subjectId;

        @SensitiveData(replacementValue = "A")
        Character sensitiveData;
    }

    @PII
    static class ByteTest {

        @SubjectId
        Byte subjectId;

        @SensitiveData(replacementValue = "B")
        Byte sensitiveData;
    }
}
