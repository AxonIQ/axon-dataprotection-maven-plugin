package io.axoniq.plugin.data.protection.generator;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import io.axoniq.plugin.data.protection.annotation.PII;
import io.axoniq.plugin.data.protection.annotation.SensitiveData;
import io.axoniq.plugin.data.protection.annotation.SubjectId;
import io.axoniq.plugin.data.protection.config.DataProtectionConfig;
import io.axoniq.plugin.data.protection.config.DataProtectionConfigList;
import io.axoniq.plugin.data.protection.config.SensitiveDataConfig;
import io.axoniq.plugin.data.protection.config.SubjectIdConfig;
import io.axoniq.plugin.data.protection.generator.utils.AnnotationUtils;
import io.axoniq.plugin.data.protection.generator.utils.ReflectionUtils;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Class responsible to hold the logic for generating the Metamodel Configuration.
 */
public class MetamodelGenerator {

    /**
     * Represents a path prefix for json path.
     */
    private static final String PATH_PREFIX = "$";

    /**
     * Represents a path divider for json path.
     */
    private static final String PATH_DIVIDER = ".";

    /**
     * Start the generation of the metamodel based on the given {@param basePackage}.
     *
     * @param basePackage The base package for look up for annotated classes.
     */
    public DataProtectionConfigList generateMetamodel(String basePackage) {
        List<DataProtectionConfig> dataProtectionConfigs = new ArrayList<>();
        // reflections lib code
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(PII.class);
        // all PII annotated class
        annotatedClasses.stream()
                        .map(this::createDataProtectionConfig)
                        .forEach(dataProtectionConfigs::add);

        // TODO: getFieldsAnnotatedWith can be used to validate if we got the right number of config entries
        return new DataProtectionConfigList(dataProtectionConfigs);
    }

    /**
     * Create a {@link DataProtectionConfig} instance based on the class and its fields.
     *
     * @param piiAnnotatedClass A class which is annotated with {@link PII}.
     * @return A new instance of a {@link DataProtectionConfig}.
     */
    private DataProtectionConfig createDataProtectionConfig(Class<?> piiAnnotatedClass) {
        List<SensitiveDataConfig> sensitiveDataList = new ArrayList<>();
        String type = ReflectionUtils.extractName(piiAnnotatedClass);
        String revision = ReflectionUtils.extractRevision(piiAnnotatedClass);

        Field[] piiClassFields = piiAnnotatedClass.getDeclaredFields();
        SubjectIdConfig subjectId = extractSubjectId(piiClassFields);

        extractSensitiveData(piiClassFields, sensitiveDataList, PATH_PREFIX);

        return new DataProtectionConfig(type, revision, subjectId, sensitiveDataList);
    }

    /**
     * Create a {@link SubjectIdConfig} instance based on a list of {@link Field}s. The first field annotated with
     * {@link SubjectId} is the one which the value will be taken.
     *
     * @param piiClassFields List of fields from a {@link Class}.
     * @return A new instance of a {@link SubjectIdConfig}.
     */
    private SubjectIdConfig extractSubjectId(Field[] piiClassFields) {
        Field subjectId = Arrays.stream(piiClassFields)
                                .filter(field -> AnnotationUtils.isAnnotationPresent(field, SubjectId.class))
                                .findFirst()
                                .orElse(null); // TODO: can it be null? what should we do?
        return new SubjectIdConfig(buildPath(PATH_PREFIX, ReflectionUtils.extractName(subjectId)));
    }

    /**
     * Create one or more instances of a {@link SensitiveDataConfig} which are added to the {@param sensitiveDataList}.
     * This method is called recursively.
     *
     * @param piiClassFields    A list of {@link Field}s that may contains {@link SensitiveData} annotated fields.
     * @param sensitiveDataList A list which will hold all the {@link SensitiveDataConfig} created during the calls.
     *                          Needed because this is meant to be a recursive method.
     * @param path              The path from the previous field. Needed because this is meant to be a recursive
     *                          method.
     */
    private void extractSensitiveData(Field[] piiClassFields,
                                      List<SensitiveDataConfig> sensitiveDataList,
                                      String path) {
        // direct annotated fields
        Arrays.stream(piiClassFields)
              .filter(f -> AnnotationUtils.isAnnotationPresent(f, SensitiveData.class))
              .forEach(f -> sensitiveDataList.add(
                      new SensitiveDataConfig(buildPath(path, ReflectionUtils.extractName(f)),
                                              ReflectionUtils.extractReplacementValue(f))
              ));
        // if it's not a primitive type, go deeper recursively
        Arrays.stream(piiClassFields)
              .forEach(field -> {
                  if (ReflectionUtils.isPrimitiveOrWrapperOrString(field)) {
                      return;
                  }
                  checkType(field, sensitiveDataList, buildPath(path, ReflectionUtils.extractName(field)));
              });
    }

    /**
     * Check the type of the given {@link Field} to decide if it's a form of Container or not. In case it's a container,
     * the method calls {@link MetamodelGenerator#extractSensitiveData(Field[], List, String)} on its type. If not, it
     * calls the method on its {@link Field}s.
     *
     * @param field             The {@link Field} we are going to perform the type check.
     * @param sensitiveDataList The container for all the {@link SensitiveDataConfig}. Needed because this is meant to
     *                          be a recursive method.
     * @param path              The path from the previous field. Needed because this is meant to be a recursive
     *                          method.
     */
    private void checkType(Field field, List<SensitiveDataConfig> sensitiveDataList, String path) {
        // TODO: can we replace the following 2 lines to avoid another dependency?
        TypeResolver resolver = new TypeResolver();
        ResolvedType type = resolver.resolve(field.getGenericType());

        // if it has parameters, it should be a form of Collection
        // TODO: really check if it's a collection/optional for example
        // TODO: we need to append an [*] for list (map later)
        // TODO: check Map
        // TODO: check Set
        // TODO: check Optional
        // TODO: check Array
        // TODO: check List
        if (!type.getTypeParameters().isEmpty()) {
            type.getTypeParameters().stream()
                .filter(tp -> !ReflectionUtils.isPrimitiveOrWrapperOrString(tp.getErasedType()))
                .forEach(tp -> extractSensitiveData(tp.getErasedType().getDeclaredFields(), sensitiveDataList, path));
        } else {
            extractSensitiveData(type.getErasedType().getDeclaredFields(), sensitiveDataList, path);
        }
    }

    /**
     * Build a path based on the previous path and the current one.
     *
     * @param previousPath Previous path on the json
     * @param path         Current path on the json
     * @return A new path built based on the parameters divided by the {@link MetamodelGenerator#PATH_DIVIDER}
     */
    private String buildPath(String previousPath, String path) {
        return previousPath + PATH_DIVIDER + path;
    }
}
