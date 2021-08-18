package io.axoniq.plugin.data.protection.generator.errors;

public class NoPIIAnnotationException extends RuntimeException {

    public NoPIIAnnotationException(String message) {
        super(message);
    }

    public NoPIIAnnotationException(Class<?> clazz) {
        super("No PII annotated class found in [" + clazz + "]");
    }
}
