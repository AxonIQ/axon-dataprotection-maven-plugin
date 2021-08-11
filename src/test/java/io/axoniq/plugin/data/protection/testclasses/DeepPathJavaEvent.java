package io.axoniq.plugin.data.protection.testclasses;

import io.axoniq.plugin.data.protection.annotation.PII;
import io.axoniq.plugin.data.protection.annotation.SensitiveData;
import io.axoniq.plugin.data.protection.annotation.SubjectId;

@PII
public class DeepPathJavaEvent extends CEvent implements JavaInterface {

    @SubjectId
    String id;

    @SensitiveData(replacementValue = "")
    String deepEventJavaField;

    public DeepPathJavaEvent(String cField, String bField, String aField) {
        super(cField, bField, aField);
    }
}
