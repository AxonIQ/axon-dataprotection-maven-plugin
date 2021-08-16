package io.axoniq.plugin.data.protection.generator.errors;

public class NoSubjectIdException extends RuntimeException {

    public NoSubjectIdException(String message) {
        super(message);
    }

    public NoSubjectIdException(Class<?> clazz) {
        super("No SubjectId annotated field found in [" + clazz + "] or one of it's parents");
    }

}
