package io.github.deprecation

import io.mockative.Mockable

@Mockable
class ClassWithDeprecatedMembers {

    @Deprecated("This property is deprecated", level = DeprecationLevel.WARNING)
    val deprecatedPropertyWarning: String = "warning"

    @Deprecated("This function is deprecated", level = DeprecationLevel.WARNING)
    fun deprecatedFunctionWarning() {}

    @Deprecated("This property is deprecated", level = DeprecationLevel.ERROR)
    val deprecatedPropertyError: String = "error"

    @Deprecated("This function is deprecated", level = DeprecationLevel.ERROR)
    fun deprecatedFunctionError() {}
}
