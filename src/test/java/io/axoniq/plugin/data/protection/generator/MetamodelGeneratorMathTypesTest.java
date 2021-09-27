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
import io.axoniq.plugin.data.protection.config.DataProtectionConfig;
import io.axoniq.plugin.data.protection.config.SensitiveDataConfig;
import io.axoniq.plugin.data.protection.config.SubjectIdConfig;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.math.BigInteger;

class MetamodelGeneratorMathTypesTest {

    MetamodelGenerator metamodelGenerator = new MetamodelGenerator();

    @Test
    void bigDecimalTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorMathTypesTest$BigDecimalTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                new SensitiveDataConfig("$.sensitiveData", "123456789.123456789"));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(BigDecimalTest.class);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void bigIntegerTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorMathTypesTest$BigIntegerTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                new SensitiveDataConfig("$.sensitiveData", "123456789"));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(BigIntegerTest.class);

        Assertions.assertEquals(expected, result);
    }


    @SensitiveDataHolder
    static class BigDecimalTest {

        @SubjectId
        BigDecimal subjectId;

        @SensitiveData(replacementValue = "123456789.123456789")
        BigDecimal sensitiveData;
    }

    @SensitiveDataHolder
    static class BigIntegerTest {

        @SubjectId
        BigInteger subjectId;

        @SensitiveData(replacementValue = "123456789")
        BigInteger sensitiveData;
    }
}
