package io.mockative

import com.google.devtools.ksp.symbol.KSCallableReference
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter

fun KSTypeReference.resolveUsageSyntax(): String {
    return when (val element = element) {
        is KSCallableReference -> element.resolveUsageSyntax()
        else -> resolve().resolveUsageSyntax()
    }
}

private fun KSType.resolveUsageSyntax(): String {
    val qualifiedName = declaration.qualifiedName!!.asString()
    val typeArguments = if (arguments.isEmpty()) "" else "<${arguments.joinToString(", ") { it.type!!.resolveUsageSyntax() }}>"
    val nullability = if (isMarkedNullable) "?" else ""
    return "$qualifiedName$typeArguments$nullability"
}

private fun KSCallableReference.resolveUsageSyntax(): String {
    val builder = StringBuilder()

    val receiverType = receiverType
    if (receiverType != null) {
        builder.append(receiverType.resolveUsageSyntax())
        builder.append('.')
    }

    builder.append('(')

    functionParameters
        .joinToString(", ") { parameter -> parameter.resolveUsageSyntax() }

    builder.append(')')
    builder.append(" -> ")
    builder.append(returnType.resolveUsageSyntax())

    return builder.toString()
}

private fun KSValueParameter.resolveUsageSyntax(): String {
    return "${name?.let { "$it: " }}${type.resolveUsageSyntax()}"
}