package io.axoniq.plugin.data.protection.generator.utils;

import io.axoniq.plugin.data.protection.testclasses.AEvent;
import io.axoniq.plugin.data.protection.testclasses.BEvent;
import io.axoniq.plugin.data.protection.testclasses.BaseEvent;
import io.axoniq.plugin.data.protection.testclasses.CEvent;
import io.axoniq.plugin.data.protection.testclasses.DeepInheritanceEvent;
import io.axoniq.plugin.data.protection.testclasses.DeepPathJavaEvent;
import io.axoniq.plugin.data.protection.testclasses.JavaInterface;
import io.axoniq.plugin.data.protection.testclasses.ShallowInheritanceEvent;
import io.axoniq.plugin.data.protection.testclasses.ShallowJavaEvent;
import io.axoniq.plugin.data.protection.testclasses.SimpleFlatEvent;
import kotlin.collections.CollectionsKt;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.List;

class ReflectionUtilsTest {

    @Test
    void getAllDeclaredFieldsShouldWorkForInterfaces() {
        List<Field> fields = ReflectionUtils.getAllDeclaredFields(JavaInterface.class);
        Assertions.assertEquals(1, fields.size());
    }

    @Test
    void getAllDeclaredFieldsShouldGetAllFieldsOnSimpleShallowClasses() {
        List<Field> fields = ReflectionUtils.getAllDeclaredFields(SimpleFlatEvent.class);
        Assertions.assertEquals(2, fields.size());
    }

    @Test
    void getAllDeclaredFieldsShouldGetAllFieldsIncludingParentsFields() {
        List<Field> fields = ReflectionUtils.getAllDeclaredFields(ShallowJavaEvent.class);
        Assertions.assertEquals(4, fields.size());
    }

    @Test
    void getAllDeclaredFieldsShouldGetAllFieldsIncludingParentsFieldsForFieldOverridingKotlinDataClasses() {
        List<Field> fields = ReflectionUtils.getAllDeclaredFields(ShallowInheritanceEvent.class);
        // each Kotlin override val will produce an additional field that will match that class
        Assertions.assertEquals(5, fields.size());
    }

    @Test
    void getAllDeclaredFieldsShouldGetAllFieldsFromAllParentsAndAccountForFieldOverridingKotlinDataClasses() {
        List<Field> fields = ReflectionUtils.getAllDeclaredFields(DeepInheritanceEvent.class);
        // each Kotlin override val will produce an additional field that will match that class
        Assertions.assertEquals(8, fields.size());
    }

    @Test
    void getAllDeclaredFieldsShouldGetAllFieldsIncludingParentsFieldsAndInterfaceFields() {
        List<Field> fields = ReflectionUtils.getAllDeclaredFields(DeepPathJavaEvent.class);
        Assertions.assertEquals(6, fields.size());
    }

    @Test
    void getAllParentsShouldNotReturnAnythingForNonExtendingInterface() {
        Assertions.assertTrue(ReflectionUtils.getAllParents(JavaInterface.class).isEmpty());
    }

    @Test
    void getAllParentsShouldFindAllParentsUpToObjectAndTheInterface() {
        List<Class<?>> allParents = ReflectionUtils.getAllParents(DeepPathJavaEvent.class);
        List<Class<?>> expected = CollectionsKt.listOf(CEvent.class, BEvent.class, AEvent.class, Object.class, JavaInterface.class);
        Assertions.assertEquals(expected, allParents);
    }

    @Test
    void getAllParentsShouldFindAllParentsForKotlinDataClasses() {
        List<Class<?>> allParents = ReflectionUtils.getAllParents(ShallowInheritanceEvent.class);
        List<Class<?>> expected = CollectionsKt.listOf(BaseEvent.class, Object.class);
        Assertions.assertEquals(expected, allParents);
    }


}