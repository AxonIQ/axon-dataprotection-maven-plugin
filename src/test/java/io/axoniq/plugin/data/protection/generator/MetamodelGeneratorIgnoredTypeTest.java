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

import java.util.List;

public class MetamodelGeneratorIgnoredTypeTest {

    @Test
    void ignoreTypeClassTest() {
        MetamodelGenerator metamodelGenerator = new MetamodelGenerator(List.of(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorIgnoredTypeTest$IgnoredType"));

        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorIgnoredTypeTest$IgnoredClassTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                new SensitiveDataConfig("$.ignoredType", "ignored"));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(MetamodelGeneratorIgnoredTypeTest.IgnoredClassTest.class);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void ignoreTypePackageTest() {
        MetamodelGenerator metamodelGenerator = new MetamodelGenerator(List.of(
                "io.axoniq.plugin.data.protection.generator.*"));

        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorIgnoredTypeTest$IgnoredClassTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                new SensitiveDataConfig("$.ignoredType", "ignored"));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(MetamodelGeneratorIgnoredTypeTest.IgnoredClassTest.class);
        Assertions.assertEquals(expected, result);
    }

    @SensitiveDataHolder
    static class IgnoredClassTest {

        @SubjectId
        Integer subjectId;

        @SensitiveData(replacementValue = "ignored")
        IgnoredType ignoredType;
    }

    static class IgnoredType {

        IgnoredType recursiveTypeToBeIgnored;
    }
}
