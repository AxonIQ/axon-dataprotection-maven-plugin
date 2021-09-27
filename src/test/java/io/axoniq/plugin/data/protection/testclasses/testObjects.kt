package io.axoniq.plugin.data.protection.testclasses

import io.axoniq.plugin.data.protection.annotation.SensitiveData
import io.axoniq.plugin.data.protection.annotation.SensitiveDataHolder
import io.axoniq.plugin.data.protection.annotation.SubjectId

open class BaseEvent(
    @SubjectId open val id: String,
    @SensitiveData(replacementValue = "null") open val auditId: String
)

open class AEvent(@SensitiveData(replacementValue = "null") open val aField: String?)
open class BEvent(@SensitiveData(replacementValue = "null") open val bField: String?, aField: String?) : AEvent(aField)
open class CEvent(
    @SensitiveData(replacementValue = "null") open val cField: String?,
    bField: String?,
    aField: String?
) : BEvent(bField, aField)

@SensitiveDataHolder
data class ShallowInheritanceEvent(
    override val id: String,
    @SensitiveData(replacementValue = "") val value: String,
    override val auditId: String = ""
) : BaseEvent(id, auditId)

@SensitiveDataHolder
data class DeepInheritanceEvent(
    @SubjectId val id: String,
    @SensitiveData(replacementValue = "") val value: String,
    override val cField: String?,
    override val bField: String?,
    override val aField: String?
) : CEvent(cField, bField, aField)

@SensitiveDataHolder
data class SimpleFlatEvent(
    @SubjectId val id: String,
    @SensitiveData(replacementValue = "") val value: String,
)