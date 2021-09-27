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

import java.util.Collection;
import java.util.List;
import java.util.Set;

class MetamodelGeneratorCollectionTypesTest {

    MetamodelGenerator metamodelGenerator = new MetamodelGenerator();

    @Test
    void listTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorCollectionTypesTest$ListTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                new SensitiveDataConfig("$.sensitiveData", "list"));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(ListTest.class);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void setTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorCollectionTypesTest$SetTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                new SensitiveDataConfig("$.sensitiveData", "set"));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(SetTest.class);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void collectionTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorCollectionTypesTest$CollectionTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                new SensitiveDataConfig("$.sensitiveData", "collection"));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(CollectionTest.class);

        Assertions.assertEquals(expected, result);
    }

    @SensitiveDataHolder
    static class ListTest {

        @SubjectId
        List<String> subjectId;

        @SensitiveData(replacementValue = "list")
        List<String> sensitiveData;
    }

    @SensitiveDataHolder
    static class SetTest {

        @SubjectId
        Set<String> subjectId;

        @SensitiveData(replacementValue = "set")
        Set<String> sensitiveData;
    }

    @SensitiveDataHolder
    static class CollectionTest {

        @SubjectId
        Collection<String> subjectId;

        @SensitiveData(replacementValue = "collection")
        Collection<String> sensitiveData;
    }
}
