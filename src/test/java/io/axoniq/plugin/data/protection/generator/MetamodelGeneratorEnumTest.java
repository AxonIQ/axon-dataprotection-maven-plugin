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

public class MetamodelGeneratorEnumTest {

    MetamodelGenerator metamodelGenerator = new MetamodelGenerator();

    @Test
    void enumSensitiveDataClass() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorEnumTest$EnumTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                new SensitiveDataConfig("$.enumeration", "enum"));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(MetamodelGeneratorEnumTest.EnumTest.class);
        Assertions.assertEquals(expected, result);
    }

    enum Enum {
        IGNORED
    }

    @SensitiveDataHolder
    static class EnumTest {

        @SubjectId
        Integer subjectId;

        @SensitiveData(replacementValue = "enum")
        Enum enumeration;
    }
}
