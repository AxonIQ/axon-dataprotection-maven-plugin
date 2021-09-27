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

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.Date;

class MetamodelGeneratorDateTimeTypesTest {

    MetamodelGenerator metamodelGenerator = new MetamodelGenerator();

    @Test
    void dateTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorDateTimeTypesTest$DateTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                new SensitiveDataConfig("$.sensitiveData", "2021-08-06 06:30"));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(DateTest.class);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void localDateTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorDateTimeTypesTest$LocalDateTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                new SensitiveDataConfig("$.sensitiveData", "2021-08-06"));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(LocalDateTest.class);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void localTimeTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorDateTimeTypesTest$LocalTimeTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                new SensitiveDataConfig("$.sensitiveData", "06:30"));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(LocalTimeTest.class);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void localDateTimeTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorDateTimeTypesTest$LocalDateTimeTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                new SensitiveDataConfig("$.sensitiveData", "2021-08-06T06:30"));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(LocalDateTimeTest.class);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void offsetDateTimeTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorDateTimeTypesTest$OffsetDateTimeTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                new SensitiveDataConfig("$.sensitiveData", "2021-08-06T06:30:05.630+01:00"));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(OffsetDateTimeTest.class);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void zonedDateTimeTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorDateTimeTypesTest$ZonedDateTimeTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                new SensitiveDataConfig("$.sensitiveData", "2021-08-06T06:30:05+01:00[Europe/Amsterdam]"));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(ZonedDateTimeTest.class);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void instantTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorDateTimeTypesTest$InstantTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                new SensitiveDataConfig("$.sensitiveData", "1"));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(InstantTest.class);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void periodTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorDateTimeTypesTest$PeriodTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                new SensitiveDataConfig("$.sensitiveData", "2"));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(PeriodTest.class);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void durationTest() {
        DataProtectionConfig expected = new DataProtectionConfig(
                "io.axoniq.plugin.data.protection.generator.MetamodelGeneratorDateTimeTypesTest$DurationTest",
                "",
                new SubjectIdConfig("$.subjectId"),
                new SensitiveDataConfig("$.sensitiveData", "3"));

        DataProtectionConfig result = metamodelGenerator.generateMetamodel(DurationTest.class);

        Assertions.assertEquals(expected, result);
    }

    @SensitiveDataHolder
    static class DateTest {

        @SubjectId
        Date subjectId;

        @SensitiveData(replacementValue = "2021-08-06 06:30")
        Date sensitiveData;
    }

    @SensitiveDataHolder
    static class LocalDateTest {

        @SubjectId
        LocalDate subjectId;

        @SensitiveData(replacementValue = "2021-08-06")
        LocalDate sensitiveData;
    }

    @SensitiveDataHolder
    static class LocalTimeTest {

        @SubjectId
        LocalTime subjectId;

        @SensitiveData(replacementValue = "06:30")
        LocalTime sensitiveData;
    }

    @SensitiveDataHolder
    static class LocalDateTimeTest {

        @SubjectId
        LocalDateTime subjectId;

        @SensitiveData(replacementValue = "2021-08-06T06:30")
        LocalDateTime sensitiveData;
    }

    @SensitiveDataHolder
    static class OffsetDateTimeTest {

        @SubjectId
        OffsetDateTime subjectId;

        @SensitiveData(replacementValue = "2021-08-06T06:30:05.630+01:00")
        OffsetDateTime sensitiveData;
    }

    @SensitiveDataHolder
    static class ZonedDateTimeTest {

        @SubjectId
        ZonedDateTime subjectId;

        @SensitiveData(replacementValue = "2021-08-06T06:30:05+01:00[Europe/Amsterdam]")
        ZonedDateTime sensitiveData;
    }

    @SensitiveDataHolder
    static class InstantTest {

        @SubjectId
        Instant subjectId;

        @SensitiveData(replacementValue = "1")
        Instant sensitiveData;
    }

    @SensitiveDataHolder
    static class PeriodTest {

        @SubjectId
        Period subjectId;

        @SensitiveData(replacementValue = "2")
        Period sensitiveData;
    }

    @SensitiveDataHolder
    static class DurationTest {

        @SubjectId
        Duration subjectId;

        @SensitiveData(replacementValue = "3")
        Duration sensitiveData;
    }
}
