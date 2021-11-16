package io.mockative

import kotlin.reflect.KClass

abstract class MockativeError(message: String) : Error(message)

class NoSuchMockError(type: KClass<*>) : MockativeError(
    buildString {
        appendLine("A mock for the type ${type.name} was not generated.")
        appendLine()
        appendLine(1, "Make sure the property holding the mock is annotated with @Mock:")
        appendLine()
        appendLine(2, "@Mock")
        appendLine(2, "private val myMock = mock(${type.name}::class)")
        appendLine(1, "")
    }
)

class ReceiverNotMockedError(receiver: Any) : MockativeError(
    buildString {
        appendLine("Attempt to perform operation a non-mock instance of type ${receiver.getClassName()}.")
        appendLine()
        appendLine(1, "Make sure the property holding the mock is annotated with @Mock:")
        appendLine()
        appendLine(2, "@Mock")
        appendLine(2, "private val myMock = mock(${receiver.getClassName()}::class)")
        appendLine(1, "")
    }
)

class ExactVerificationError(
    instance: Any,
    expected: Int,
    actual: Int,
    expectation: Expectation,
    invocations: List<Invocation>
) : MockativeError(
    buildString {
        appendLine("A mock of type ${instance.getClassName()} was not invoked the expected number of times.")
        appendLine()
        appendLine(1, "Expected $expected invocations of $expectation")
        appendLine(1, "Actual: $actual")
        appendLine()

        if (invocations.isEmpty()) {
            appendLine(2, "No invocation on the mock were recorded.")
        } else {
            invocations.forEach { invocation ->
                appendLine(2, "$invocation")
            }
        }

        appendLine(1, "")
    }
)

class RangeVerificationError(
    instance: Any,
    atLeast: Int?,
    atMost: Int?,
    actual: Int,
    expectation: Expectation,
    invocations: List<Invocation>
) : MockativeError(
    buildString {
        appendLine("A mock of type ${instance.getClassName()} was not invoked the expected number of times.")
        appendLine()

        val expected = when {
            atLeast != null && atMost != null -> "at least $atLeast and at most $atMost "
            atLeast != null -> "at least $atLeast "
            atMost != null -> "at most $atMost "
            else -> "at least 1"
        }

        appendLine(1, "Expected ${expected}invocations of $expectation")
        appendLine(1, "Actual: $actual")
        appendLine()

        if (invocations.isEmpty()) {
            appendLine(2, "No invocation on the mock were recorded.")
        } else {
            invocations.forEach { invocation ->
                appendLine(2, "$invocation")
            }
        }

        appendLine(1, "")
    }
)

class UnverifiedInvocationsError(instance: Any, invocations: List<Invocation>) : MockativeError(
    buildString {
        appendLine(0, "A mock contains unverified invocations.")
        appendLine()
        appendLine(
            1,
            "The following invocations on the type ${instance.getClassName()} were not verified:"
        )
        appendLine()

        invocations.forEach { invocation ->
            appendLine(2, "$invocation")
        }

        appendLine(1, "")
    }
)

class MockValidationError(instance: Any, expectations: List<Expectation>, invocations: List<Invocation>) : MockativeError(
    buildString {
        appendLine("Validation of mock failed.")
        appendLine()
        appendLine(1, "The following expectations on the type ${instance.getClassName()} were not met.")
        appendLine()

        expectations.forEach { expectation ->
            appendLine(2, "$expectation")
        }

        appendLine()

        appendLine(1, "The following invocations were recorded:")
        appendLine()

        invocations.forEach { invocation ->
            appendLine(2, "$invocation")
        }

        appendLine(1, "")
    }
)

class MissingExpectationError(instance: Any, invocation: Invocation, isSuspend: Boolean) : MockativeError(
    buildString {
        appendLine("A function was called without a matching expectation.")
        appendLine()
        appendLine(1, "An expectation was not given on the function:")
        appendLine(2, "${instance.getClassName()}.$invocation")
        appendLine()
        appendLine(1, "Set up an expectation using:")
        appendLine(2, "given(instance)${if (isSuspend) ".coroutine" else ""} { $invocation }")
        appendLine(3, ".then { ... }")
        appendLine(1, "")
    }
)

private inline fun buildString(block: Appendable.() -> Unit): String {
    return StringBuilder().also { block(it) }.toString()
}

private fun Appendable.appendIndentation(level: Int) {
    for (i in 0 until level) {
        append("    ")
    }
}

private fun Appendable.appendLine(indentation: Int, value: String) {
    appendIndentation(indentation)
    appendLine(value)
}
