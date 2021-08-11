package io.axoniq.plugin.data.protection.testclasses;

import io.axoniq.plugin.data.protection.annotation.SensitiveData;

public interface JavaInterface {
    @SensitiveData(replacementValue = "interface data") String interfaceField = null;
}
