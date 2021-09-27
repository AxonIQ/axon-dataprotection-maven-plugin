package io.axoniq.plugin.data.protection.testclasses;

import io.axoniq.plugin.data.protection.annotation.SensitiveData;
import io.axoniq.plugin.data.protection.annotation.SensitiveDataHolder;

@SensitiveDataHolder
public class ShallowJavaEvent extends BaseEvent {

    @SensitiveData(replacementValue = "")
    private String shallowJavaField;
    private int amount;

    public ShallowJavaEvent(String id, String auditId) {
        super(id, auditId);
    }
}