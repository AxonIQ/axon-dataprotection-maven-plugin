package io.axoniq.plugin.data.protection.testclasses;

import io.axoniq.plugin.data.protection.annotation.PII;
import io.axoniq.plugin.data.protection.annotation.SensitiveData;
import org.jetbrains.annotations.NotNull;

@PII
public class ShallowJavaEvent extends BaseEvent {

    @SensitiveData(replacementValue = "") private String shallowJavaField;

    public ShallowJavaEvent(@NotNull String id, @NotNull String auditId) {
        super(id, auditId);
    }
}
