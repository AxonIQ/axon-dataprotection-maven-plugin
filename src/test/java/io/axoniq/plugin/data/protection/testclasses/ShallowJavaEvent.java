package io.axoniq.plugin.data.protection.testclasses;

import io.axoniq.plugin.data.protection.annotation.PII;
import io.axoniq.plugin.data.protection.annotation.SensitiveData;

@PII
public class ShallowJavaEvent extends BaseEvent {

    @SensitiveData(replacementValue = "")
    private String shallowJavaField;
    private int amount;

    public ShallowJavaEvent(String id, String auditId) {
        super(id, auditId);
    }
}